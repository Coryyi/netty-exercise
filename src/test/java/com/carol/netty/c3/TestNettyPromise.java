package com.carol.netty.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1.准备一个eventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();
        // 在这里自己创建 而不是提交任务后返回的promise
        // 泛型 指定将来promise中装什么
        // 可以主动创建promise对象而不是被动
        //2.主动创建Promise对象，用于存储结果的容器
        DefaultPromise<Integer> promise = new DefaultPromise<Integer>(eventLoop);

        new Thread(()->{
            //3.任意线程执行计算 计算完毕后向promise填充结果
            log.debug("开始计算");
            try {
                int i = 1/0;
                // 提供方法主动设置返回
                promise.setSuccess(100);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // 计算结果出错 让线程别等待了
                promise.setFailure(e);
                throw new RuntimeException(e);
            }

        }).start();

        // 4. 接收结果的线程
        log.debug("等待结果...");
        log.debug("结果是{}",promise.get());

    }
}
