package com.nio.service;


import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class SenderService {


    @PostConstruct
    public void Sender() throws IOException {
        // 开启一个server channel来监听
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 开启非阻塞模式
        ssc.configureBlocking(false);
        ServerSocket socket = ssc.socket();
        socket.bind(new InetSocketAddress("127.0.0.1",18081));//绑定相应IP及port
        Selector selector = Selector.open();//开启一个selector
        ssc.register(selector,SelectionKey.OP_ACCEPT);//绑定注册事件

        //使用线程处理
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        
        while(true){
            selector.select();//阻塞,只有当至少一个注册的事件发生的时候才会继续.
            Set<SelectionKey> selectKeys = selector.selectedKeys();
            Iterator<SelectionKey> it = selectKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                // 处理事件. 可以用多线程来处理.
                //selector.dispatch(key);
                scheduledExecutorService.execute( () -> {

                    String res = key.attachment().toString();
                    System.out.println(res);
                });
            }
        }
    }

}
