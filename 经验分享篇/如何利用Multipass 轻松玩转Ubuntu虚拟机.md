# 如何利用Multipass 轻松玩转Ubuntu虚拟机

## 为什么要用这个
此款软件对我是非常有帮助的：利用这款软件可轻松装Ubuntu虚拟机，并且可以多开，轻松快速的做集群实验。
## 官网
[https://multipass.run/install](https://multipass.run/install)
## 查找镜像
```
multipass find
```
## 创建虚拟机
```
multipass launch -n 自己取个名字 -c 4 -m 4G -d 40G
```
## 进入虚拟机
```
multipass shell 你取得名字
```
## 虚拟机列表
```
multipass list
```
## 删除虚拟机
```
multipass delete --purge 虚拟机的名字 彻底删除
```
