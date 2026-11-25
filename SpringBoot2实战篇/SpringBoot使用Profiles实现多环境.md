# SpringBoot使用Profiles实现多环境

## 配置方式
### 多文档块
```
---
server:
  port: 8083
spring:
  profiles: dev #配置环境的名称


---
server:
  port: 8084
spring:
  profiles: prod  #配置环境的名称
```
### 多环境文件
![image.png](media/16838989033350/16984588297025.png)
## 激活的三种方式		
### 主配置文件中激活
```
spring:
  profiles:
    active: dev
```
### 通过设置程序参数激活
--spring-profiles.active=prod
### 通过虚拟机参数激活
-Dspring-profiles.active=dev
