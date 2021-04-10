create database if not exists `msg_schedule`;
use `msg_schedule`;

create table if not exists `local_instant_message`
(
    id              varchar(64)                              not null comment '主键id'
        primary key,
    topic           varchar(128)                             not null,
    tag             varchar(128)                             not null comment '消息标签',
    is_tx           tinyint     default 1                    not null comment '是否事务消息:1是0否',
    platform        tinyint     default 1                    not null comment '消息平台 1 RocketMQ 2 RabbitMQ 3 Kafka',
    platform_msg_id varchar(64)                              null comment '中间件提供的消息标识，如rocketmq中的msgId',
    trace_id        varchar(128)                             null comment '分布式追踪id',
    trace_version   varchar(128)                             null comment '分布式追踪版本',
    business_id     varchar(64)                              null comment '业务id，方便检索',
    payload         json                                     not null comment '消息json',
    status          tinyint     default 0                    not null comment '-1 send error -2consume error 0 created default 1 sent 2 consumed',
    created_at      datetime(6) default CURRENT_TIMESTAMP(6) not null comment '创建时间，视为生效时间',
    updated_at      datetime(6) default CURRENT_TIMESTAMP(6) not null on update CURRENT_TIMESTAMP(6) comment '更新时间'
);

create index idx_business_id
    on local_instant_message (business_id, status);

create index idx_mq_platform_msg
    on local_instant_message (platform, platform_msg_id);

create index idx_time_status
    on local_instant_message (created_at, status);