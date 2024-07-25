package com.carol.netty.c2;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        // 1.�����¼�ѭ����

        // �����Ǽ����̣߳�Ĭ����ʹ��CPU�߳�������2�����ٱ�֤һ���߳�
        EventLoopGroup group = new NioEventLoopGroup(2);// io �¼� ��ͨ���� ��ʱ����
        //EventLoopGroup group1 = new DefaultEventLoopGroup();//��ͨ���� ��ʱ����
        //2.��ȡ��һ���¼�ѭ������ ����ѯЧ��
        System.out.println(group.next());
        System.out.println(group.next());
        System.out.println(group.next());

        //3.ִ����ͨ���� �̳����̳߳أ��������ط���
        /*group.next().submit(()->{//�����Ὣ�������ύ��ѭ������ĳһ������ȥִ�� submit��executeЧ��һ��
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // ����־�ĺô����ڿ��Կ������ĸ��̲߳���
            log.debug("ok");
        });*/

        // 4.ִ�ж�ʱ���� ���һ��ʱ��ִ�� FixedRate��һ����Ƶ��ִ��
        group.next().scheduleAtFixedRate(()->{
            log.debug("schedule");
        },0,1, TimeUnit.SECONDS);// �ڶ���������ʼ��ʱʱ�� ���������ʱ��
        log.debug("main");

    }
}
