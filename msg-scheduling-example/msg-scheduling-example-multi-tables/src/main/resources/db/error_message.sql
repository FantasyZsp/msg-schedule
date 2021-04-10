create database if not exists `msg_schedule`;
use `msg_schedule`;
create table error_message
(
    id              varchar(64)                              not null comment '主键id'
        primary key,
    msg_id          varchar(64)                              not null comment '本地具体消息表id',
    matched         tinyint     default 1                    not null comment 'msg_id是否匹配到主消息表 1匹配 2不匹配(生产环节一定一致，但是消费环节，可能消费到不是主表中有的消息)',
    topic           varchar(128)                             not null,
    platform        tinyint     default 1                    not null comment 'mq平台 1 RocketMQ 2 RabbitMQ 3 Kafka 4其他',
    platform_msg_id varchar(64)                              null comment '中间件提供的消息标识，如rocketmq中的msgId',
    business_id     varchar(64)                              null comment '业务id，方便检索',
    error_type      tinyint                                  not null comment '错误类型: 1发送失败 2消费失败',
    retry_times     int         default 0                    not null comment '已重试次数',
    error_reason    varchar(128)                             not null comment '描述，可用于记录异常原因等',
    error_code      int                                      not null comment '异常码，细化具体的异常原因。如半消息投递异常，回查异常等',
    created_at      datetime(6) default CURRENT_TIMESTAMP(6) not null comment '创建时间'
)
    comment '消息处理失败记录表';

create index idx_business_id
    on error_message (business_id);

create index idx_channel_created_at
    on error_message (topic, created_at);

create index idx_msg_id
    on error_message (msg_id);