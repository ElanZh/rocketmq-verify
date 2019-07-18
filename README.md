# rocketmq-verify

## rocketMQ验证项目

**好消息：从4.5版本开始rocketMQ已经支持主从自动切换了！**

rocketMQ的优点
> 1.rmq去除对zk的依赖
> 
> 2.rmq支持异步和同步两种方式刷磁盘
> 
> 3.rmq单机支持的队列或者topic数量是5w
> 
> 4.rmq支持消息重试
> 
> 5.rmq支持严格按照一定的顺序发送消息
> 
> 6.rmq支持定时发送消息
> 
> 7.rmq支持根据消息ID来进行查询
> 
> 8.rmq支持根据某个时间点进行消息的回溯
> 
> 9.rmq支持对消息服务端的过滤
> 
> 10.rmq消费并行度:顺序消费 取决于queue数量,乱序消费 取决于consumer数量