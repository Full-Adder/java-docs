# Redis bigkey问题及解决方案

## 什么是bigkey
是指在Redis中存在的体积较大的键，这些键(其实是存储的值)由于其体积庞大而导致一系列性能和稳定性问题。

## bigkey 会有什么影响

**性能问题**：大键可能导致操作时间增加，包括读取、写入和删除操作，从而影响系统的响应时间和整体性能。

**阻塞**：由于 Redis 的单线程模型，对大键的操作可能会阻塞其他命令的执行，影响系统的并发处理能力。

**内存管理**：存储和删除大键会导致内存碎片化，降低内存利用效率，增加内存分配和释放操作的开销。

**集群扩展**：在 Redis 集群模式下，大键的迁移会耗费大量带宽和时间，导致集群性能下降，同时可能导致负载不均衡。

**缓存命中率降低**：大键会占用大量内存空间，可能导致有用的数据被逐出内存，降低缓存命中率，影响整体系统性能。

**持久化问题**：大键会增加生成快照和 AOF 追加的时间和文件大小，影响持久化性能和磁盘利用率。

**网络带宽**：大键在网络传输过程中占用更多带宽和时间，增加网络负载，尤其在复制和故障转移过程中影响明显。

## 如何定义bigkey
Redis 中定义 bigkey 的标准并没有一个严格的界限，因为是否被视为 bigkey 很大程度上取决于具体的应用场景和服务器的配置。但是，我们可以从几个角度来考虑一个键是否可能是 bigkey：

**键的长度：** 如果键名（key name）本身非常长，比如超过了几百个字符，它可能会被认为是 bigkey。键名的长度会影响 Redis 内部数据结构的存储和检索效率。

**序列化大小：** 在 Redis 协议中，键和值都需要序列化后进行传输。如果键的序列化后的大小很大，比如超过了 1KB 或 2KB，这可能会增加网络传输的开销。

**数据结构复杂性：** 如果键关联的数据结构复杂，比如一个哈希表包含大量的字段，或者是一个列表、集合或有序集合包含大量的元素，这可能导致序列化后的键值对变得较大。

**内存使用：** 如果一个键的值占用了大量的内存，即使键名本身不长，这个键也可能被认为是 bigkey。Redis 可以使用 MEMORY USAGE 命令来查看键的内存使用情况。

**性能影响：** 如果一个键的存储和检索操作显著影响了 Redis 服务器的性能，比如增加了响应时间或减少了吞吐量，这个键也可能被认为是 bigkey。

**业务逻辑：** 在某些情况下，即使键的大小没有达到上述标准，但由于业务逻辑的要求，比如需要频繁地对键进行复杂操作，它也可能被认为是 bigkey。

## 解决方案
### 如何发现bigkey
* --bigkeys
```
redis-cli --bigkeys

# Scanning the entire keyspace to find biggest keys as well as
# average sizes per key type.  You can use -i 0.1 to sleep 0.1 sec
# per 100 SCAN commands (not usually needed).

[00.00%] Biggest list   found so far '"big_list_key"' with 2 items
[00.00%] Biggest list   found so far '"bigkey"' with 10000 items
[00.00%] Biggest string found so far '"name"' with 52 bytes
[00.00%] Biggest hash   found so far '"big_hash_key"' with 1 fields

-------- summary -------

Sampled 4 keys in the keyspace!
Total key length in bytes is 34 (avg len 8.50)

Biggest   list found '"bigkey"' has 10000 items
Biggest   hash found '"big_hash_key"' has 1 fields
Biggest string found '"name"' has 52 bytes

2 lists with 10002 items (50.00% of keys, avg size 5001.00)
1 hashs with 1 fields (25.00% of keys, avg size 1.00)
1 strings with 52 bytes (25.00% of keys, avg size 52.00)
0 streams with 0 entries (00.00% of keys, avg size 0.00)
0 sets with 0 members (00.00% of keys, avg size 0.00)
0 zsets with 0 members (00.00% of keys, avg size 0.00)
```
这个会导致OPS 飙升，因为他是扫描所有的key,可以加上推荐的参数，每扫描一次休息0.1s

```
redis-cli --bigkeys -i 0.1
```

* memory usage

```
memory usage key [count]
root@server1:~# redis-cli
127.0.0.1:6379> memory usage big_list_key
(integer) 120
127.0.0.1:6379> memory usage bigkey
(integer) 140020

# 单位是字节
```
* 脚本任务
结合scan命令，扫描每一个key,计算每个key的大小来实现
### 如何清理bigkey
* string
    * delete
    * 特别大 使用unlink 先解除关系 后台默默删除
* hash
    * 使用hscan 每次获取少量字段

* list
    * 使用ltrim 进行渐进式删除

* set
    * 使用sccan 获取少量元素，在用srem删除

* zset
    * 使用zscan扫描少量元素，再用zremrangebyrank 删除
