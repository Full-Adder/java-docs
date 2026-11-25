# IDEA 5大调试技巧-实现高效率开发

## 条件调试
![image.png](media/16839009644840/16984583858128.png)
## 回到上一步
![image.png](media/16839009644840/16984583858138.png)
## 多线程调试
![image.png](media/16839009644840/16984583858146.png)
## 远程断点调试
![image.png](media/16839009644840/16984583858155.png)
```
java -server -Xms512m -Xmx512m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=9081 -jar springboot-nacos-0.0.1-SNAPSHOT.jar

```
## 实时修改运行中的变量值和执行表达式
![image.png](media/16839009644840/16984583858165.png)
![image.png](media/16839009644840/16984583858177.png)
![image.png](media/16839009644840/16984583858200.png)
