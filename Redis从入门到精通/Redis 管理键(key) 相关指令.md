# Redis 管理键(key) 相关指令


| 指令 | 用法 | 描述 |
| --- | --- | --- |
| DEL | `DEL key [key ...]` | 删除指定的key，返回被删除的key的数量 |
| DUMP | `DUMP key` | 序列化给定key，并返回被序列化的值 |
| EXISTS | `EXISTS key [key ...]` | 检查给定key是否存在，存在返回1，不存在返回0 |
| EXPIRE | `EXPIRE key seconds` | 为给定key设置过期时间（秒） |
| EXPIREAT | `EXPIREAT key timestamp` | 为key设置过期时间，接受的时间参数是UNIX时间戳 |
| PEXPIRE | `PEXPIRE key milliseconds` | 设置key的过期时间，以毫秒计 |
| PEXPIREAT | `PEXPIREAT key milliseconds-timestamp` | 设置key过期时间的时间戳（unix timestamp），以毫秒计 |
| KEYS | `KEYS pattern` | 查找所有符合给定模式的key |
| MOVE | `MOVE key db` | 将当前数据库的key移动到给定的数据库中 |
| PERSIST | `PERSIST key` | 移除key的过期时间，key将持久保持 |
| PTTL | `PTTL key` | 以毫秒为单位返回key的剩余过期时间 |
| TTL | `TTL key` | 以秒为单位，返回给定key的剩余生存时间 |
| RANDOMKEY | `RANDOMKEY` | 从当前数据库中随机返回一个key |
| RENAME | `RENAME key newkey` | 修改key的名称 |
| RENAMENX | `RENAMENX key newkey` | 仅当newkey不存在时，将key改名为newkey |
| TYPE | `TYPE key` | 返回key所储存的值的类型 |

## DEL
**用法**：`DEL key [key ...]`
**描述**：删除指定的key，返回被删除的key的数量。

## DUMP
**用法**：`DUMP key`
**描述**：序列化给定key，并返回被序列化的值。

## EXISTS
**用法**：`EXISTS key [key ...]`
**描述**：检查给定key是否存在，存在返回1，不存在返回0。

## EXPIRE
**用法**：`EXPIRE key seconds`
**描述**：为给定key设置过期时间（秒）。

## EXPIREAT
**用法**：`EXPIREAT key timestamp`
**描述**：为key设置过期时间，接受的时间参数是UNIX时间戳。

## PEXPIRE
**用法**：`PEXPIRE key milliseconds`
**描述**：设置key的过期时间，以毫秒计。

## PEXPIREAT
**用法**：`PEXPIREAT key milliseconds-timestamp`
**描述**：设置key过期时间的时间戳（unix timestamp），以毫秒计。

## KEYS
**用法**：`KEYS pattern`
**描述**：查找所有符合给定模式的key。

## MOVE
**用法**：`MOVE key db`
**描述**：将当前数据库的key移动到给定的数据库中。

## PERSIST
**用法**：`PERSIST key`
**描述**：移除key的过期时间，key将持久保持。

## PTTL
**用法**：`PTTL key`
**描述**：以毫秒为单位返回key的剩余过期时间。

## TTL
**用法**：`TTL key`
**描述**：以秒为单位，返回给定key的剩余生存时间。

## RANDOMKEY
**用法**：`RANDOMKEY`
**描述**：从当前数据库中随机返回一个key。

## RENAME
**用法**：`RENAME key newkey`
**描述**：修改key的名称。

## RENAMENX
**用法**：`RENAMENX key newkey`
**描述**：仅当newkey不存在时，将key改名为newkey。

## TYPE
**用法**：`TYPE key`
**描述**：返回key所储存的值的类型。