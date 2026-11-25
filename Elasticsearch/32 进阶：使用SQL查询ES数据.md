# 32 进阶：使用SQL查询ES数据
## 环境准备
需要首先安装好docker环境，请自行安装，这里不做介绍
### 安装ES
```
# 创建network kibana和elastic需要用到
docker network create elastic

# 启动es docker容器
docker run --name es01 --net elastic -p 9200:9200 -it -m 1GB docker.elastic.co/elasticsearch/elasticsearch:8.11.1
```
启动之后，保存安装页面的密码(=k8dCIlKnwM=mSXHVGO8)和token,后面用得到
```
✅ Elasticsearch security features have been automatically configured!
✅ Authentication is enabled and cluster connections are encrypted.

ℹ️  Password for the elastic user (reset with `bin/elasticsearch-reset-password -u elastic`):
  =k8dCIlKnwM=mSXHVGO8

ℹ️  HTTP CA certificate SHA-256 fingerprint:
  4c4e6cb854e056ce8e9d0ccf88ca33d11b7025ab4f46a557b3dc3661104b6d01

ℹ️  Configure Kibana to use this cluster:
• Run Kibana and click the configuration link in the terminal when Kibana starts.
• Copy the following enrollment token and paste it into Kibana in your browser (valid for the next 30 minutes):
  eyJ2ZXIiOiI4LjExLjEiLCJhZHIiOlsiMTcyLjE5LjAuMjo5MjAwIl0sImZnciI6IjRjNGU2Y2I4NTRlMDU2Y2U4ZTlkMGNjZjg4Y2EzM2QxMWI3MDI1YWI0ZjQ2YTU1N2IzZGMzNjYxMTA0YjZkMDEiLCJrZXkiOiJXMWlZQTR3Qm5ZM210NklfTXFRazpRbW5Yb19zQVJpTzAtbE5tRUFZdm5nIn0=

ℹ️ Configure other nodes to join this cluster:
• Copy the following enrollment token and start new Elasticsearch nodes with `bin/elasticsearch --enrollment-token <token>` (valid for the next 30 minutes):
  eyJ2ZXIiOiI4LjExLjEiLCJhZHIiOlsiMTcyLjE5LjAuMjo5MjAwIl0sImZnciI6IjRjNGU2Y2I4NTRlMDU2Y2U4ZTlkMGNjZjg4Y2EzM2QxMWI3MDI1YWI0ZjQ2YTU1N2IzZGMzNjYxMTA0YjZkMDEiLCJrZXkiOiJYVmlZQTR3Qm5ZM210NklfTXFRejowY0lvdkRnTFJ6YXJHbUhzU01YN3Z3In0=

  If you're running in Docker, copy the enrollment token and run:
  `docker run -e "ENROLLMENT_TOKEN=<token>" docker.elastic.co/elasticsearch/elasticsearch:8.11.1`
```
到这里es安装完成，接下来安装kibana
### 安装kibana
```
# 启动kibana容器
docker run --name kib01 --net elastic -p 5601:5601 docker.elastic.co/kibana/kibana:8.11.1
  
# 生成kibana 登录token
docker exec -it es01 /usr/share/elasticsearch/bin/elasticsearch-create-enrollment-token -s kibana
```
等待安装成功，访问kibana
### 访问kibana
http://192.168.64.7:5601
第一步：输入token，就是你刚才生成的
第二部：输入elastic用户名(elasctic)和密码(上面保存的那个)
登录之后点击dev-tools就能进入如下页面。
![](media/17008766484810/17008814135168.jpg)

### 利用脚本导入测试数据
这里使用的是pythonj脚本，直接运行即可
Elasticsearch(["https://elastic:=k8dCIlKnwM=mSXHVGO8@192.168.64.7:9200"], verify_certs=False)
这一步部分填写你自己的信息即可
```
from elasticsearch import Elasticsearch
from faker import Faker
import ssl
import random
es = Elasticsearch(["https://elastic:=k8dCIlKnwM=mSXHVGO8@192.168.64.7:9200"], verify_certs=False)
# 连接到 Elasticsearch 实例

# 创建一个 Faker 实例，用于生成有意义的假数据
fake = Faker()

# 创建索引（类似于数据库中的表）
index_name = 'test_sql_query4'
es.indices.create(index=index_name, ignore=400)

# 插入 2000 条有意义的数据
for i in range(2000):
    doc = {
        'name': random.choice(['秦', '楚', '齐', '燕', '韩', '魏', '赵']),
        'age': fake.random_int(min=18, max=99, step=1),
        'city': fake.city(),
        'occupation': fake.job(),
        'salary': fake.random_int(min=30000, max=100000, step=1000),
        'timestamp': fake.date_time_this_decade().strftime("%Y-%m-%d")
    }
    es.index(index=index_name, body=doc)

print("数据插入完成。")

```
如何按照上面的步骤，安装成功后就可以学习今天的内容了

## SQL学习
```
# 解析SQL
POST /_sql/translate
{
  "query": """
  SELECT * FROM "test_sql_query4" order by age  LIMIT 10
  """
}
```
### 基本查询
```
POST /_sql?format=txt
{
  "query": """
  SELECT * FROM "test_sql_query4"
  """
}
```
### 排序查询
```
POST /_sql?format=txt
{
  "query": """
  SELECT * FROM "test_sql_query4" order by age desc
  """
}
```
### 过滤查询
```
POST /_sql?format=txt
{
  "query": """
  SELECT * FROM "test_sql_query4" where name='秦' order by age desc
  """
}

POST /_sql?format=txt
{
  "query": """
  SELECT * FROM "test_sql_query4" where name in ('秦','赵') order by age desc
  """
}
```
### 范围查询
```
POST /_sql?format=txt
{
  "query": """
  SELECT * FROM "test_sql_query4" where age<50 and age>30  order by age desc
  """
}
```
### 分组查询(group)
```
POST /_sql?format=txt
{
  "query": """
  SELECT name,count(1) FROM "test_sql_query4" group by name
  """
}
```
### 分组过滤查询(group+having)
```
POST /_sql?format=txt
{
  "query": """
  SELECT name,count(1) FROM "test_sql_query4" group by name having count(1)>300
  """
}
```
### 聚合函数统计
```
POST /_sql?format=txt
{
  "query": """
  SELECT count(name),max(age),min(age),sum(salary) FROM "test_sql_query4"
  """
}
```
### limit查询
```
POST /_sql?format=txt
{
  "query": """
  SELECT * FROM "test_sql_query4" order by age limit 10
  """
}
```
### 分页查询
```
POST /_sql?format=json
{
  "query": """
  SELECT * FROM "test_sql_query4" order by age
  """,
  "fetch_size":2
}
```
```
POST /_sql?format=json
{
  "cursor": "从前面一次查询中获得"
}
```