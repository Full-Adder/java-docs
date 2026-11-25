# Redis 集群教程：深入指南与集群工作原理

Redis 集群是一个强大的分布式数据存储解决方案，它通过多个节点提供数据自动分片、高可用性和可扩展性。本教程将详细介绍 Redis 集群的工作原理、搭建步骤、管理实践以及故障排除方法。

## 搭建集群
### 自动创建
找到位于源码下面的 create-cluster 指令
```
~/redis-stable/utils/create-cluster
```
# 启动6个redis
```
./create-cluster start
```

```
root@server1:~/redis-stable/utils/create-cluster# ./create-cluster start
Starting 30001
Starting 30002
Starting 30003
Starting 30004
Starting 30005
Starting 30006
root@server1:~/redis-stable/utils/create-cluster# ps aux | grep redis
root       18453  0.4  0.2  36108  5492 ?        Ssl  07:48   0:00 /root/redis-stable/utils/create-cluster/../../src//redis-server *:30001 [cluster]
root       18455  0.2  0.2  36108  5680 ?        Ssl  07:48   0:00 /root/redis-stable/utils/create-cluster/../../src//redis-server *:30002 [cluster]
root       18457  0.4  0.2  36108  5540 ?        Ssl  07:48   0:00 /root/redis-stable/utils/create-cluster/../../src//redis-server *:30003 [cluster]
root       18465  0.4  0.2  36108  5532 ?        Ssl  07:48   0:00 /root/redis-stable/utils/create-cluster/../../src//redis-server *:30004 [cluster]
root       18470  0.2  0.2  36108  5516 ?        Ssl  07:48   0:00 /root/redis-stable/utils/create-cluster/../../src//redis-server *:30005 [cluster]
root       18472  0.2  0.2  36108  5568 ?        Ssl  07:48   0:00 /root/redis-stable/utils/create-cluster/../../src//redis-server *:30006 [cluster]
```
创建集群
```
./create-cluster create
```

```
root@server1:~/redis-stable/utils/create-cluster# ./create-cluster create
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 127.0.0.1:30005 to 127.0.0.1:30001
Adding replica 127.0.0.1:30006 to 127.0.0.1:30002
Adding replica 127.0.0.1:30004 to 127.0.0.1:30003
>>> Trying to optimize slaves allocation for anti-affinity
[WARNING] Some slaves are in the same host as their master
M: 4841a5160d80c9fd0a323e76012665cfe781f010 127.0.0.1:30001
   slots:[0-5460] (5461 slots) master
M: 4cf73b092c85984442f03a3687996cffb06386bd 127.0.0.1:30002
   slots:[5461-10922] (5462 slots) master
M: 157c4f68b029e30f0871685a6cc743329f8c183d 127.0.0.1:30003
   slots:[10923-16383] (5461 slots) master
S: 418857b17b9437c14e7728580f74166803eb0898 127.0.0.1:30004
   replicates 4841a5160d80c9fd0a323e76012665cfe781f010
S: 71a134a9d2017fe485e0d3907218bd5f93b4536d 127.0.0.1:30005
   replicates 4cf73b092c85984442f03a3687996cffb06386bd
S: 550469fcc9a7f2530385c1cd8ffc3da2aa2c5c5f 127.0.0.1:30006
   replicates 157c4f68b029e30f0871685a6cc743329f8c183d
Can I set the above configuration? (type 'yes' to accept): yes
```
### 手动搭建集群
准备六个配置文件
```
-rw-r--r--  1 root   root   107696 Jun  9 06:17 redis-cluster-6379.conf
-rw-r--r--  1 root   root   107696 Jun  9 06:16 redis-cluster-6380.conf
-rw-r--r--  1 root   root   107696 Jun  9 06:16 redis-cluster-6381.conf
-rw-r--r--  1 root   root   107696 Jun  9 06:16 redis-cluster-6382.conf
-rw-r--r--  1 root   root   107696 Jun  9 06:16 redis-cluster-6383.conf
-rw-r--r--  1 root   root   107696 Jun  9 06:16 redis-cluster-6384.conf
```
内容如下
```
dir "/root/redis-stable/db/{6379，6380，6381，6382，6383，6384}"
cluster-enabled yes
cluster-config-file {6379，6380，6381，6382，6383，6384}.conf
daemonize yes
port {6379，6380，6381，6382，6383，6384}
```
分别启动这个六个redis
```
redis-server redis-cluster-6379.conf
redis-server redis-cluster-6380.conf
redis-server redis-cluster-6381.conf
redis-server redis-cluster-6382.conf
redis-server redis-cluster-6383.conf
redis-server redis-cluster-6384.conf
```
创建集群
```
redis-cli --cluster create 127.0.0.1:6379 127.0.0.1:6380 127.0.0.1:6381 127.0.0.1:6382 127.0.0.1:6383 127.0.0.1:6384 --cluster-replicas 1
```
```
>>> Performing hash slots allocation on 6 nodes...
Master[0] -> Slots 0 - 5460
Master[1] -> Slots 5461 - 10922
Master[2] -> Slots 10923 - 16383
Adding replica 127.0.0.1:6383 to 127.0.0.1:6379
Adding replica 127.0.0.1:6384 to 127.0.0.1:6380
Adding replica 127.0.0.1:6382 to 127.0.0.1:6381
>>> Trying to optimize slaves allocation for anti-affinity
[WARNING] Some slaves are in the same host as their master
M: e2d2cea520af1f3e1d7a9db3b658df0996451f86 127.0.0.1:6379
   slots:[0-5460] (5461 slots) master
M: 41036334cd1e8fe6da08021133c63204e4412ebc 127.0.0.1:6380
   slots:[5461-10922] (5462 slots) master
M: c3de56de9cd0d19361fd9bba1f9c3dc6ea77db2e 127.0.0.1:6381
   slots:[10923-16383] (5461 slots) master
S: a0b9f2a4b7cdb3512c63c465508c8b4938ac970b 127.0.0.1:6382
   replicates c3de56de9cd0d19361fd9bba1f9c3dc6ea77db2e
S: bf4b2edf28f2dd3e4be4ae6522255ea5ea2f8ef5 127.0.0.1:6383
   replicates e2d2cea520af1f3e1d7a9db3b658df0996451f86
S: 4db6c41e57c59df0bf73418eeb03b2b914759d62 127.0.0.1:6384
   replicates 41036334cd1e8fe6da08021133c63204e4412ebc
Can I set the above configuration? (type 'yes' to accept): yes
```
输入yes，进行槽位分配，等待分配完成
```
>>> Nodes configuration updated
>>> Assign a different config epoch to each node
>>> Sending CLUSTER MEET messages to join the cluster
Waiting for the cluster to join
.
>>> Performing Cluster Check (using node 127.0.0.1:6379)
M: e2d2cea520af1f3e1d7a9db3b658df0996451f86 127.0.0.1:6379
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: 4db6c41e57c59df0bf73418eeb03b2b914759d62 127.0.0.1:6384
   slots: (0 slots) slave
   replicates 41036334cd1e8fe6da08021133c63204e4412ebc
M: 41036334cd1e8fe6da08021133c63204e4412ebc 127.0.0.1:6380
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: a0b9f2a4b7cdb3512c63c465508c8b4938ac970b 127.0.0.1:6382
   slots: (0 slots) slave
   replicates c3de56de9cd0d19361fd9bba1f9c3dc6ea77db2e
M: c3de56de9cd0d19361fd9bba1f9c3dc6ea77db2e 127.0.0.1:6381
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: bf4b2edf28f2dd3e4be4ae6522255ea5ea2f8ef5 127.0.0.1:6383
   slots: (0 slots) slave
   replicates e2d2cea520af1f3e1d7a9db3b658df0996451f86
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```

## 集群基本操作指令
* 使用-c  能够自动重定向到对应槽位
```
redis-cli -c
```
* 随意添加两个key 有可能会落到不同的槽位
```
127.0.0.1:6381> set name example
-> Redirected to slot [5798] located at 127.0.0.1:6380
OK
127.0.0.1:6380> get name
"example"
127.0.0.1:6380> set age 20
-> Redirected to slot [741] located at 127.0.0.1:6379
OK
127.0.0.1:6379> 
```
* 如果我想让两个key 存放在相同的槽位怎么操作呢？可以使用tag
```
127.0.0.1:6379> set {user1}name example
-> Redirected to slot [8106] located at 127.0.0.1:6380
OK
127.0.0.1:6380> set {user}age 20
OK
127.0.0.1:6380> get {user1}name
"example"
127.0.0.1:6380> get {user1}age
"123"

# 可以看出age和name 都在6380节点的槽位上
```
* 查看集群信息
```
root@server1:~/redis-stable# redis-cli  --cluster info 127.0.0.1 6379
127.0.0.1:6379 (e2d2cea5...) -> 1 keys | 5461 slots | 1 slaves.
127.0.0.1:6380 (41036334...) -> 5 keys | 5462 slots | 1 slaves.
127.0.0.1:6381 (c3de56de...) -> 2 keys | 5461 slots | 1 slaves.
```
* 检查集群

```
root@server1:~/redis-stable# redis-cli  --cluster check 127.0.0.1 6379
127.0.0.1:6379 (e2d2cea5...) -> 1 keys | 5461 slots | 1 slaves.
127.0.0.1:6380 (41036334...) -> 5 keys | 5462 slots | 1 slaves.
127.0.0.1:6381 (c3de56de...) -> 2 keys | 5461 slots | 1 slaves.
[OK] 8 keys in 3 masters.
0.00 keys per slot on average.
>>> Performing Cluster Check (using node 127.0.0.1:6379)
M: e2d2cea520af1f3e1d7a9db3b658df0996451f86 127.0.0.1:6379
   slots:[0-5460] (5461 slots) master
   1 additional replica(s)
S: 4db6c41e57c59df0bf73418eeb03b2b914759d62 127.0.0.1:6384
   slots: (0 slots) slave
   replicates 41036334cd1e8fe6da08021133c63204e4412ebc
M: 41036334cd1e8fe6da08021133c63204e4412ebc 127.0.0.1:6380
   slots:[5461-10922] (5462 slots) master
   1 additional replica(s)
S: a0b9f2a4b7cdb3512c63c465508c8b4938ac970b 127.0.0.1:6382
   slots: (0 slots) slave
   replicates c3de56de9cd0d19361fd9bba1f9c3dc6ea77db2e
M: c3de56de9cd0d19361fd9bba1f9c3dc6ea77db2e 127.0.0.1:6381
   slots:[10923-16383] (5461 slots) master
   1 additional replica(s)
S: bf4b2edf28f2dd3e4be4ae6522255ea5ea2f8ef5 127.0.0.1:6383
   slots: (0 slots) slave
   replicates e2d2cea520af1f3e1d7a9db3b658df0996451f86
[OK] All nodes agree about slots configuration.
>>> Check for open slots...
>>> Check slots coverage...
[OK] All 16384 slots covered.
```
* 查看槽位分布

```
cluster slots
```

## 集群工作原理
Redis 集群是一种分布式数据库解决方案，旨在实现高可用性和可扩展性。其核心在于数据分片、节点通信、故障检测与恢复以及数据迁移等机制。

### 一、数据分片机制
Redis 集群采用哈希槽（hash slots）分片机制，共有 16384 个哈希槽。每个键通过 CRC16 校验和映射到一个哈希槽，每个节点负责一定数量的哈希槽。分片机制确保数据均匀分布，提高集群性能和扩展性。

### 二、节点通信与 Gossip 协议
节点通过 Gossip 协议进行通信，定期交换状态信息，保持集群一致性和健康状态。每个节点使用两个端口：
- **客户端连接端口**（如 6379）：处理客户端请求。
- **集群总线端口**（如 16379）：用于节点间通信。

节点发送 PING 消息检查其他节点的状态，接收 PONG 消息确认节点健康。如果节点未响应，会被标记为疑似下线（PFAIL），并在多个节点确认后标记为正式下线（FAIL）。

### 三、故障检测与自动恢复
Redis 集群通过以下步骤进行故障检测与自动恢复：
1. **故障检测**：节点定期发送 PING 消息，未响应时认为节点失联（PFAIL）。
2. **仲裁与确认**：多个节点确认后将节点标记为正式下线（FAIL）。
3. **主从切换**：选择失效节点的一个从节点晋升为主节点。
4. **数据重分配**：更新哈希槽分配，确保数据继续可用。

### 四、数据迁移与重分片
数据迁移和重分片机制保证了集群的灵活性和数据的均匀分布。当增加或移除节点时，需要进行数据迁移：
1. **选择源节点和目标节点**：确定需要迁移哈希槽的节点。
2. **迁移数据**：源节点将部分哈希槽的数据迁移到目标节点。
3. **更新路由**：通知集群其他节点和客户端更新路由信息。

### 五、读写操作
- **读操作**：直接由负责该哈希槽的从节点处理。
- **写操作**：由主节点处理，并异步复制到从节点。

尽管 Redis 集群使用异步复制，可能导致短暂的数据不一致，但提供了 `WAIT` 命令来增加一致性，确保写操作复制到指定数量的从节点后才返回。

#### 六、扩展性与高可用性
Redis 集群的设计允许通过增加节点来提高存储容量和处理能力，实现线性扩展。主从复制和自动故障恢复机制确保集群的高可用性，即使部分节点失效，集群仍能继续提供服务。

### 总结
Redis 集群通过高效的数据分片、节点通信、故障检测与自动恢复、数据迁移等机制，实现了高可用性和可扩展性。虽然一致性保证并不完美，但通过适当的配置和操作，可以在实际应用中提供稳定和高效的服务。更多细节请参考 Redis 官方文档。