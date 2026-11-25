# Arthas 调试线上代码技巧

## Arthas 是Alibaba开源的Java诊断工具，深受开发者喜爱。
当你遇到以下类似问题而束手无策时，Arthas可以帮助你解决：

1. 这个类从哪个 jar 包加载的？为什么会报各种类相关的 Exception？
2. 我改的代码为什么没有执行到？难道是我没 commit？分支搞错了？
3. 遇到问题无法在线上 debug，难道只能通过加日志再重新发布吗？
4. 线上遇到某个用户的数据处理有问题，但线上同样无法 debug，线下无法重现！
5. 是否有一个全局视角来查看系统的运行状况？
6. 有什么办法可以监控到JVM的实时运行状态？
7. 怎么快速定位应用的热点，生成火焰图？
8. 怎样直接从JVM内查找某个类的实例？
## 安装
curl -O [https://arthas.aliyun.com/arthas-boot.jar](https://arthas.aliyun.com/arthas-boot.jar)
## 启动
java -jar arthas-boot.jar
## 反编译代码
jad --source-only com.ams.testarthas.IndexController test --lineNumber false
jad --source-only com.ams.testarthas.TestConstants hello --lineNumber false
## 监视方法执行
watch com.ams.testarthas.IndexController test '{params,returnObj,throwExp}'  -n 5  -x 3 
## 获取静态字段
getstatic com.ams.testarthas.TestConstants a -x 3
## 执行静态方法
ognl '@com.ams.testarthas.TestConstants@hello("ams")'
## 编译class文件
mc -d /Users/neil/Desktop/ /Users/neil/Desktop/TestConstants.java
## jad和mc和retransform 结合使用

-  不允许新增加field/method
- 正在跑的函数，没有退出不能生效，比如下面新增加的System.out.println，只有run()函数里的会生效

jad --source-only com.ams.testarthas.TestConstants > /Users/neil/Desktop/TestConstants.java
mc /Users/neil/Desktop/TestConstants.java -d /Users/neil/Desktop/ 
retransform /Users/neil/Desktop/com/ams/testarthas/TestConstants.class
## 列出retransform
retransform -l
## 删除指定retransform
retransform -d 1
retransform --deleteAll


