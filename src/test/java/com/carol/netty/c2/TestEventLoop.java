package com.carol.netty.c2;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 1.创建事件循环组

        // 到底是几个线程？默认是使用CPU线程数乘以2，至少保证一个线程
        EventLoopGroup group = new NioEventLoopGroup(2);// io 事件 普通任务 定时任务
        //EventLoopGroup group1 = new DefaultEventLoopGroup();//普通任务 定时任务
        //2.获取下一个事件循环对象 简单轮询效果
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        //3.执行普通任务 继承了线程池，因此有相关方法
        /*group.next().submit(()->{//将来会将此任务提交给循环组中某一个对象去执行 submit与execute效果一样
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // 用日志的好处在于可以看出是哪个线程操作
            log.debug("ok");
        });*/

        // 4.执行定时任务 间隔一段时间执行 FixedRate以一定的频率执行
        group.next().scheduleAtFixedRate(()->{
            log.debug("schedule");
        },0,1, TimeUnit.SECONDS);// 第二个参数初始延时时间 第三个间隔时间
        log.debug("main");

    }
}
