package xyz.mydev.msg.schedule.example.delay.repository.impl;

import org.apache.ibatis.annotations.Mapper;
import xyz.mydev.msg.schedule.mq.error.record.ErrorMessage;


/**
 * @author ZSP
 */
@Mapper
public interface ErrorMessageMapper {
  int insert(ErrorMessage entity);
}