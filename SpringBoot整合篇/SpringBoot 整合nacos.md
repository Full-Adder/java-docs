# SpringBoot 整合nacos

## 背景
最近有好多同学咨询我：他们项目没有用到spring cloud，但是也想使用nacos作为配置中心，应该怎么做呢？
## 安装nacos
个人学习推荐使用 docker安装
```
git clone https://github.com/nacos-group/nacos-docker.git
cd nacos-docker
docker-compose -f example/standalone-mysql-5.7.yaml up
```
### 添加依赖
```
<dependency>
    <groupId>com.alibaba.boot</groupId>
    <artifactId>nacos-config-spring-boot-starter</artifactId>
    <version>0.2.1</version>
</dependency>
```
## 添加配置
```
spring.application.name=NACOS-SERVER
server.port=10010
  
nacos.config.server-addr=127.0.0.1:8848

```
## 启动类添加nacos注解
```
@NacosPropertySource(dataId = "example", autoRefreshed = true)
```
## 项目使用
```
@NacosValue(value = "${useLocalCache:false}", autoRefreshed = true)
```
