use msg_schedule;

select time, updated_at, floor(TIMESTAMPDIFF(MICROSECOND, time, updated_at) / 1000) as `ms`
from local_delay_message
# ORDER BY ms desc
ORDER BY time desc
;

select created_at, updated_at, floor(TIMESTAMPDIFF(MICROSECOND, created_at, updated_at) / 1000) as `ms`
from local_instant_message
# ORDER BY ms desc
ORDER BY created_at desc
;


(select 'delay'                                                                    as msgType,
        max(TIMESTAMPDIFF(MICROSECOND, time, updated_at) / 1000)                   as `maxMs`,
        min(TIMESTAMPDIFF(MICROSECOND, time, updated_at) / 1000)                   as `minMs`,
        sum(TIMESTAMPDIFF(MICROSECOND, time, updated_at) / 1000) / count(*)        as aveMs,
        floor(TIMESTAMPDIFF(MICROSECOND, min(created_at), max(created_at)) / 1000) as `duringMS`,
        count(*)                                                                   as count
 from local_delay_message
 ORDER BY minMs desc)
union all
(select 'instant'                                                                  as msgType,
        max(TIMESTAMPDIFF(MICROSECOND, created_at, updated_at) / 1000)             as `maxMs`,
        min(TIMESTAMPDIFF(MICROSECOND, created_at, updated_at) / 1000)             as `minMs`,
        sum(TIMESTAMPDIFF(MICROSECOND, created_at, updated_at) / 1000) / count(*)  as aveMs,
        floor(TIMESTAMPDIFF(MICROSECOND, min(created_at), max(created_at)) / 1000) as `duringMS`,
        count(*)                                                                   as count
 from local_instant_message
 ORDER BY minMs desc);


truncate local_delay_message;
truncate local_instant_message;
truncate `order`;

select min(time),
       max(time),
       min(created_at),
       max(created_at),
       min(updated_at),
       max(updated_at)
from local_delay_message;
