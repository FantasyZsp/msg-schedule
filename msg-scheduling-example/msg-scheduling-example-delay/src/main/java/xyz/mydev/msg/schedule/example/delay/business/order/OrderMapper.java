package xyz.mydev.msg.schedule.example.delay.business.order;

import org.apache.ibatis.annotations.Mapper;


/**
 * @author ZSP
 */
@Mapper
public interface OrderMapper {
  int insert(Order entity);
}