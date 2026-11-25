# Netty 稍微改动实现断线重连

## 实现思路
首先思考一下，断开连接大概分为两种情况：

- 服务端主动和客户端断开连接，有可能由于网络问题导致
- 客户端和服务端主动断开连接，有可能是客户端的一些问题导致整个进程直接不work，彻底失去连接 (这种你只能重启)

那么针对这两种情况，我们想一下重连是发生在客户端还是服务端？

不用想，肯定是在客户端进行重连，服务端才不管那么多事。

那么我们是不是可以在客户端程序里面加重连逻辑就可以了，那么又有新的问题了，应该加在哪里呢，我怎么知道它们断开连接了呢？
当然有办法知道，netty就给我门提供了这样一个机制：channelInactive 当通道不活跃时，会调用这个方法，那么我们就用这个来整一波。

## 代码实现(只需修改客户端)

详见视频

```
public void connect() throws InterruptedException {
    ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9999);
    channelFuture.addListener((ChannelFutureListener) future -> {
        if (!future.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> {
                log.error("我与服务端失去了连接，我正在进行重连...");
                try {
                    connect();
                    failedCount++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, 3L, TimeUnit.SECONDS);
        } else {
            System.err.println("服务端连接成功...");
        }
    });
    //给关闭通道进行监听
    channelFuture.channel().closeFuture().sync();
}
```

### 重写handler的channelInactive方法
```
@Override
public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    try {
        ctx.pipeline().remove(this);
        ctx.channel().close();
        nettyClient.connect();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```
