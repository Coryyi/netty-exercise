package com.carol.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyFuture {
    // 类似JdkFuture
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //netty中线程池叫eventLoop
        //EventLoop中就一个线程
        NioEventLoopGroup group = new NioEventLoopGroup();// group中是多线程 但是每个EventLoop是单线程

        EventLoop eventLoop = group.next();

        // netty下的future
        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 70;// 结果填充到future中
            }
        });

        // future 就是在线程间传递结果传递数据
        // submit返回的就是一个future对象
        //3.主线程通过future获取返回 同步方式！！！
        /*log.debug("主线程等待结果");
        //阻塞 直到任务运行结束 拿到返回
        log.debug("结果是"+future.get());*/

        //4.异步方式！！！
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override //异步执行
            public void operationComplete(Future<? super Integer> future) throws Exception {
                // 由执行者去接受结果 执行线程处理返回结果
                log.debug("接收结果{}",future.getNow());// 用非阻塞方法 因为此时已经通知有结果了

            }
        });;


    }
}
