/**
 * 对投递消息的加载
 * 对仓储层的封装，结合检查点，达到以下目的：
 * 1. 利用检查点快速加载消息的目的。
 * 2. 对调度层提供消息访问的api
 *
 * @author ZSP
 */
package xyz.mydev.msg.schedule.load;