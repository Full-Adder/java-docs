# Kafka 集群安装与验证

## 安装
为了真实搭建kafka集群和zookeeper集群，我这里采用docker方式安装，这样安装也比较简单，只需要写一个docker-compose脚本，当然前提是你得安装了docker和docker-compose，如果没有对应环境，则可参考我往期docker视频。
### zookeeper
```
version: '2'
services:
 zoo1:
  image: zookeeper:3.4 # 镜像名称
  restart: always # 当发生错误时自动重启
  hostname: zoo1
  container_name: zoo1
  privileged: true
  ports: # 端口
   - 2181:2181
  volumes: # 挂载数据卷
   - ./zoo1/data:/data
   - ./zoo1/datalog:/datalog 
  environment:
   TZ: Asia/Shanghai
   ZOO_MY_ID: 1 # 节点ID
   ZOO_PORT: 2181 # zookeeper端口号
   ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888 # zookeeper节点列表

 zoo2:
  image: zookeeper:3.4
  restart: always
  hostname: zoo2
  container_name: zoo2
  privileged: true
  ports:
   - 2182:2181
  volumes:
   - ./zoo2/data:/data
   - ./zoo2/datalog:/datalog
  environment:
   TZ: Asia/Shanghai
   ZOO_MY_ID: 2
   ZOO_PORT: 2181
   ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888

 zoo3:
  image: zookeeper:3.4
  restart: always
  hostname: zoo3
  container_name: zoo3
  privileged: true
  ports:
   - 2183:2181
  volumes:
   - ./zoo3/data:/data
   - ./zoo3/datalog:/datalog
  environment:
   TZ: Asia/Shanghai
   ZOO_MY_ID: 3
   ZOO_PORT: 2181
   ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888

networks:
 default:
  external:
   name: zoo_kafka


```
docker-compose -f docker-compose-zookeeper.yaml up -d
### kafka
```
version: '2'

services:
 kafka1:
  image: wurstmeister/kafka
  restart: always
  hostname: broker1
  container_name: kafka1
  privileged: true
  ports:
   - "9091:9092"
  environment:
   KAFKA_BROKER_ID: 1
   KAFKA_LISTENERS: PLAINTEXT://broker1:9092
   KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.64.2:9091
   KAFKA_ADVERTISED_HOST_NAME: broker1
   KAFKA_ADVERTISED_PORT: 9092
   KAFKA_ZOOKEEPER_CONNECT: zoo1:2181,zoo2:2181,zoo3:2181
   #JMX_PORT: 9987
  volumes:
   - /var/run/docker.sock:/var/run/docker.sock
   - ./broker1:/kafka/kafka\-logs\-broker1
  external_links:
  - zoo1
  - zoo2
  - zoo3

 kafka2:
  image: wurstmeister/kafka
  restart: always
  hostname: broker2
  container_name: kafka2
  privileged: true
  ports:
   - "9092:9092"
  environment:
   KAFKA_BROKER_ID: 2
   KAFKA_LISTENERS: PLAINTEXT://broker2:9092
   KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.64.2:9092
   KAFKA_ADVERTISED_HOST_NAME: broker2
   KAFKA_ADVERTISED_PORT: 9092
   KAFKA_ZOOKEEPER_CONNECT: zoo1:2181,zoo2:2181,zoo3:2181
   #JMX_PORT: 9988
  volumes:
   - /var/run/docker.sock:/var/run/docker.sock
   - ./broker2:/kafka/kafka\-logs\-broker2
  external_links: # 连接本compose文件以外的container
  - zoo1
  - zoo2
  - zoo3

 kafka3:
  image: wurstmeister/kafka
  restart: always
  hostname: broker3
  container_name: kafka3
  privileged: true
  ports:
   - "9093:9092"
  environment:
   KAFKA_BROKER_ID: 3
   KAFKA_LISTENERS: PLAINTEXT://broker3:9092
   KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://192.168.64.2:9093
   KAFKA_ADVERTISED_HOST_NAME: broker3
   KAFKA_ADVERTISED_PORT: 9092
   KAFKA_ZOOKEEPER_CONNECT: zoo1:2181,zoo2:2181,zoo3:2181
   #JMX_PORT: 9989
  volumes:
   - /var/run/docker.sock:/var/run/docker.sock
   - ./broker3:/kafka/kafka\-logs\-broker3
  external_links: # 连接本compose文件以外的container
  - zoo1
  - zoo2
  - zoo3

 kafka-manager:
  image: sheepkiller/kafka-manager:latest
  restart: always
  container_name: kafka-manager
  hostname: kafka-manager
  ports:
   - "9000:9000"
  links:      # 连接本compose文件创建的container
   - kafka1
   - kafka2
   - kafka3
  external_links:  # 连接本compose文件以外的container
   - zoo1
   - zoo2
   - zoo3
  environment:
   ZK_HOSTS: zoo1:2181,zoo2:2181,zoo3:2181
   KAFKA_BROKERS: kafka1:9092,kafka2:9092,kafka3:9092
   APPLICATION_SECRET: letmein
   KM_ARGS: -Djava.net.preferIPv4Stack=true

networks:
 default:
  external:  # 使用已创建的网络
   name: zoo_kafka


```
docker-compose -f docker-compose-kafka.yaml up -d
## 验证
前提：进入到kafka容器 docker exec -it kafka1/2/3 /bin/bash
### 创建topic
```
kafka-topics.sh --zookeeper 192.168.64.3:2181 --create --topic topic_test2--replication-factor 3 --partitions 3
```
### 查看topic信息
```
kafka-topics.sh --zookeeper 192.168.64.3:2181 --describe --topic topic_test2
```
### 开启消费者
```
kafka-console-consumer.sh --bootstrap-server 192.168.64.3:9092 --topic topic_test2
```

### 创建生产者
```
kafka-console-producer.sh --broker-list 192.168.64.3:9092 --topic topic_test2
```
