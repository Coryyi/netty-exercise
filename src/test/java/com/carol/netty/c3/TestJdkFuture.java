package com.carol.netty.c3;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // jdk关联线程池使用一般
        // 1.创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 2.提交任务         Callable 是有返回结果的 Runnable 是没有返回结果的
        Future<Integer> future = executorService.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50; //这里是在线程池中的某个线程执行 最终是主线程需要获取值 结果 如何通信？future对象
            }
        });
        // future 就是在线程间传递结果传递数据
        // submit返回的就是一个future对象
        //3.主线程通过future获取返回
        log.debug("主线程等待结果");
        //阻塞 直到任务运行结束 拿到返回
        log.debug("结果是"+future.get());
    }
}
