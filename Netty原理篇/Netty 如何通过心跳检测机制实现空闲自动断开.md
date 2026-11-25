# Netty 如何通过心跳检测机制实现空闲自动断开

## 什么是心跳检测
心跳检测指的是在客户端和服务端维护一种特殊的数据包，客户端通过这个数据包告诉服务端自己还是存活的，然后服务端可以通过这个心跳检测机制去实现一些业务功能，如：空闲自动断开、判断客户端是否在线等
## 如何实现心跳检测机制
其实只需要引入IdleStateHandler，就搞定了
```
try {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup) //设置两个线程组
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    log.info("客户端channel 初始化");
                    ch.pipeline().addLast(new IdleStateHandler(3,3,3, TimeUnit.SECONDS));
                    ch.pipeline().addLast(new NettyServerHandler());
                }
            });

    ChannelFuture cf = bootstrap.bind(9999).sync();
    log.info("服务启动成功....");
    cf.addListener((ChannelFutureListener) future -> log.info("监听端口:{}",future.isSuccess()));
    cf.channel().closeFuture().sync();
} finally {
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
}
```
## 心跳检测机制工作原理
进入源码之后，我们可以发现IdleStateHandler内部定义的一些结构
### 初始化构造器后
定义了读、写、读写 事件
### 触发channelActive后
```
@Override
public void channelActive(ChannelHandlerContext ctx) throws Exception {
    // This method will be invoked only if this handler was added
    // before channelActive() event is fired.  If a user adds this handler
    // after the channelActive() event, initialize() will be called by beforeAdd().
    initialize(ctx);
    super.channelActive(ctx);
}
```
调用了initialize(ctx);，我们点进去看看里面做了什么
```
private void initialize(ChannelHandlerContext ctx) {
    // Avoid the case where destroy() is called before scheduling timeouts.
    // See: https://github.com/netty/netty/issues/143
    switch (state) {
    case 1:
    case 2:
        return;
    }

    state = 1;
    initOutputChanged(ctx);

    lastReadTime = lastWriteTime = ticksInNanos();
    if (readerIdleTimeNanos > 0) {
        readerIdleTimeout = schedule(ctx, new ReaderIdleTimeoutTask(ctx),
                readerIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
    if (writerIdleTimeNanos > 0) {
        writerIdleTimeout = schedule(ctx, new WriterIdleTimeoutTask(ctx),
                writerIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
    if (allIdleTimeNanos > 0) {
        allIdleTimeout = schedule(ctx, new AllIdleTimeoutTask(ctx),
                allIdleTimeNanos, TimeUnit.NANOSECONDS);
    }
}
```
通过我们的配置，分别schedule三个任务：ReaderIdleTimeoutTask、WriterIdleTimeoutTask、AllIdleTimeoutTask
## 定时执行task
这里拿ReaderIdleTimeoutTask进行说明
```
private final class ReaderIdleTimeoutTask extends AbstractIdleTask {

    ReaderIdleTimeoutTask(ChannelHandlerContext ctx) {
        super(ctx);
    }

    @Override
    protected void run(ChannelHandlerContext ctx) {
        long nextDelay = readerIdleTimeNanos;
        if (!reading) {
            nextDelay -= ticksInNanos() - lastReadTime;
        }

        if (nextDelay <= 0) {
            // Reader is idle - set a new timeout and notify the callback.
            readerIdleTimeout = schedule(ctx, this, readerIdleTimeNanos, TimeUnit.NANOSECONDS);

            boolean first = firstReaderIdleEvent;
            firstReaderIdleEvent = false;

            try {
                IdleStateEvent event = newIdleStateEvent(IdleState.READER_IDLE, first);
                channelIdle(ctx, event);
            } catch (Throwable t) {
                ctx.fireExceptionCaught(t);
            }
        } else {
            // Read occurred before the timeout - set a new timeout with shorter delay.
            readerIdleTimeout = schedule(ctx, this, nextDelay, TimeUnit.NANOSECONDS);
        }
    }
}
```

- 获取读空闲的时长
- 如果当前没有在读，则将当前时间剪去上次读取channel的时间做差值，然后将配置的空闲时长剪去这个差值，得到 新的nextDelay
- 如果nextDelay小于0，则说明，距离上次读取channel的时间已经超过了程序配置的时间
   - 重新schedule一个新的task，时间重置
   - 触发IdleStateEvent 时间，一般业务逻辑回去订阅这个事件
- 如果大于0，则按照新的delay重新schedule一个task
## 编写业务逻辑，实现空闲自动断开
```
@Slf4j
class NettyServerHandler extends ChannelInboundHandlerAdapter {
    int i=0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("第{}次收到客户端消息:{}\n",i++,byteBuf.readableBytes());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case ALL_IDLE:
                    log.error("读写都空闲");
                    break;
                case READER_IDLE:
                    log.error("读空闲");
                    break;
                case WRITER_IDLE:
                    log.error("写空闲");
                    break;
            }
        }
    }
}
```
