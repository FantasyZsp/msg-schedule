package xyz.mydev.msg.schedule.mq.rocketmq.producer;


import com.google.common.util.concurrent.AtomicLongMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.mydev.msg.common.Constants;
import xyz.mydev.msg.schedule.bean.StringMessage;
import xyz.mydev.msg.schedule.infrastruction.repository.route.MessageRepositoryRouter;
import xyz.mydev.msg.schedule.mq.error.record.ErrorMessage;
import xyz.mydev.msg.schedule.mq.error.record.MsgSendErrorCodeEnum;

/**
 * @author zhaosp
 */
@Slf4j
@Component
public class TransactionMessageListenerImpl implements TransactionListener {

  private final MessageRepositoryRouter messageRepositoryRouter;
  private final RocketMqSendFailureHandler rocketMqSendFailureHandler;

  public TransactionMessageListenerImpl(MessageRepositoryRouter messageRepositoryRouter,
                                        RocketMqSendFailureHandler rocketMqSendFailureHandler) {
    this.messageRepositoryRouter = messageRepositoryRouter;
    this.rocketMqSendFailureHandler = rocketMqSendFailureHandler;
  }

  /**
   * id: check times
   * TODO 调整为redis，并加入过期处理防止OOM
   */
  private static final AtomicLongMap<String> MSG_CHECK_TIMES_MAP = AtomicLongMap.create();
  private final static int MAX_CHECK_TIMES = 10;


  /**
   * 投递消息，当本地消息状态不是0时，默认其已经投递过，将会回滚消息。
   *
   * <p>
   * 答疑记录：
   * 1. 对于redis延时队列，可能因为故障重启时重新消费（投递消息），在此方法中，会将已经投递的消息截断不进行后续commit。
   * 2. 对于redis延时队列来说，只要执行过此方法，就可以认为消费成功了。因为后续的check流程可以与redis延时队列无关。
   * 3.如果后续有 手动/定时故障 重新投递的需求，需要另行实现，禁止重用此实现。因为对msgDb状态的校验会导致无法重新投递。
   *
   * @param businessId 透传业务id
   */
  @Override
  @Transactional
  public LocalTransactionState executeLocalTransaction(Message msg, Object businessId) {
    log.info("executing LocalTransaction, msgKey [{}] topic [{}] , businessId [{}]------------", msg.getKeys(), msg.getTopic(), businessId);

    String tableName = msg.getProperty("tableName");
    boolean success = messageRepositoryRouter.get(tableName).updateToSent(msg.getKeys());

    if (!success) {
      if (log.isDebugEnabled()) {
        log.debug("msg [{}] update status error, rollback message[{}] ", msg.getKeys(), new String(msg.getBody()));
      }

      recordSendFailureAndRemoveCacheQuietly(msg.getTopic(), msg.getKeys(),
        ErrorMessage.MATCHED,
        null,
        msg.getProperty(Constants.MsgPropertiesKey.BUSINESS_ID),
        0,
        MsgSendErrorCodeEnum.EXECUTE_LOCAL_TX.getDescription(),
        MsgSendErrorCodeEnum.EXECUTE_LOCAL_TX.getCode());
      return LocalTransactionState.ROLLBACK_MESSAGE;
    }

    log.info("submit msg: msgKey [{}] topic [{}]------------", msg.getKeys(), msg.getTopic());
    return LocalTransactionState.COMMIT_MESSAGE;
  }


  /**
   * 检查消息是否成功
   * 两种情况(消息库中不一定有消息)
   * 1. 实际上消息状态已经被置为已发送，只是确认消息晚到达或者遗失。
   * 2. 半消息发送后，未来得及执行本地事务，出现异常，半消息也未回滚。
   */
  @Override
  public LocalTransactionState checkLocalTransaction(MessageExt msg) {
    String msgIdInDb = msg.getKeys();
    String tableName = msg.getProperty(Constants.MsgPropertiesKey.TABLE_NAME);

    StringMessage messageEntity = messageRepositoryRouter.get(tableName).selectById(msgIdInDb);

    if (localTransactionSuccess(messageEntity)) {

      log.info("msg [{}] checkLocalTransaction success", msgIdInDb);

      return LocalTransactionState.COMMIT_MESSAGE;

    } else {


      int checkTimes = (int) MSG_CHECK_TIMES_MAP.incrementAndGet(msgIdInDb);

      log.info("msg [{}] check times [{}]", msgIdInDb, checkTimes);

      if (checkTimes < MAX_CHECK_TIMES) {

        return LocalTransactionState.UNKNOW;

      } else {
        // 超过最大重试次数，回滚消息。
        log.info("reach maxCheckTimes ,rollback msg [{}]", msg);

        if (messageEntity == null) {

          recordSendFailureAndRemoveCacheQuietly(msg.getTopic(),
            msg.getKeys(),
            ErrorMessage.NOT_MATCHED,
            msg.getMsgId(),
            msg.getProperty(Constants.MsgPropertiesKey.BUSINESS_ID),
            checkTimes,
            MsgSendErrorCodeEnum.CHECK_LOCAL_TX_MAX_RETRY_NO_MSG.getDescription(),
            MsgSendErrorCodeEnum.CHECK_LOCAL_TX_MAX_RETRY_NO_MSG.getCode());

        } else {
          // 存在消息
          messageRepositoryRouter.get(tableName).updateStatus(msgIdInDb, Constants.MessageStatus.SEND_ERROR);
          recordSendFailureAndRemoveCacheQuietly(msg.getTopic(),
            msg.getKeys(),
            ErrorMessage.MATCHED,
            msg.getMsgId(),
            msg.getProperty(Constants.MsgPropertiesKey.BUSINESS_ID),
            checkTimes,
            MsgSendErrorCodeEnum.CHECK_LOCAL_TX_MAX_RETRY.getDescription(),
            MsgSendErrorCodeEnum.CHECK_LOCAL_TX_MAX_RETRY.getCode());
        }

        return LocalTransactionState.ROLLBACK_MESSAGE;
      }
    }
  }

  private boolean localTransactionSuccess(StringMessage stringMessage) {
    // 如果状态是已发送，说明本地事务执行成功，可以进行发送了
    return stringMessage != null && stringMessage.getStatus() == Constants.MessageStatus.SENT;
  }


  /**
   * 记录失败的消息
   * 删除计数缓存
   *
   * @param platformMsgId 当执行本地事务时，总是拿不到。回查时才会有
   * @param matched       回查时查不到主表消息时，就是没匹配到
   */
  private void recordSendFailureAndRemoveCacheQuietly(String topic,
                                                      String idInMsgDb,
                                                      int matched,
                                                      String platformMsgId,
                                                      String businessId,
                                                      int retryTimes,
                                                      String errorReason,
                                                      int errorCode) {
    try {

      rocketMqSendFailureHandler.recordSendFailure(topic, idInMsgDb, matched, platformMsgId, businessId, retryTimes, errorReason, errorCode);

    } catch (Throwable ignoredEx) {

      log.error("record send failed msg error, ignored but log", ignoredEx);

    } finally {
      MSG_CHECK_TIMES_MAP.remove(idInMsgDb);
    }
  }

}
