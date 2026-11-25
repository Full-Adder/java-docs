# Netty 快速入门

## 为什么Netty 会诞生
为了解决NIO 编码复杂，但是又想使用NIO，所以netty来了，netty 通过对nio复杂的api进行了封装，使得netty在具备高性能、高吞吐量、低延迟的前提下，还能方便开发人员进行快速开发。
## 有哪些使用场景
### 互联网行业
成为大多数中间件和rpc框架的底层通信组件，如dubbo、rocket mq
### 游戏行业
账号登陆服务器、地图服务器之间可以方便的通过 Netty 进行高性能的通信。
### 大数据领域
hadoop和序列化组件avro的rpc框架，都是采用netty作为底层通信组件
## 浅试一下
### 服务端
```
package com.example.day2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.flogger.Flogger;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @Author 编程学习
 * @Date 2022/12/3 12:34
 */
@Slf4j
public class NettyServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //8
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
    }

}
@Slf4j
class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("收到客户端消息:{}",byteBuf.toString(CharsetUtil.UTF_8));
        // 现在通过通道给客户端回消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("客户端你好：我收到你的消息了", CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        
    }
}
```
### 客户端
```
package com.example.day2;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description TODO
 * @Author 编程学习
 * @Date 2022/12/3 12:55
 */
public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            //设置相关参数
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler()); //加入自己的处理器
                        }
                    });

            System.out.println("客户端 ok..");
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9999).sync();
            //给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } finally {

            group.shutdownGracefully();

        }
    }
}

@Slf4j
class NettyClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("服务端你好：我和你建立连接了", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        log.info("收到服务端消息:{}", byteBuf.toString(CharsetUtil.UTF_8));
    }
}
```