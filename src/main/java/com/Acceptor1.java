package com;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class Acceptor1 implements Runnable {
    Selector acceptSelector;

    ServerSocketChannel serverSocketChannel;
    DispatcherPool dispatcherPool;


    public Acceptor1(ServerSocketChannel serverSocketChannel, DispatcherPool dispatcherPool) throws Exception{
        this.dispatcherPool = dispatcherPool;
        this.serverSocketChannel = serverSocketChannel;
        //this.acceptSelector = SelectorProvider.provider().openSelector();
        //serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);

    }

    @Override
    public void run() {
        System.out.println("before accept new connection");
        for (;;) {
            try {

                SocketChannel socketChannel = serverSocketChannel.accept();
                System.out.println("accept new connection");
                socketChannel.configureBlocking(false);

                ChannelIO channelIO = ChannelIO.getInstance(socketChannel, false);
                RequestHandler requestHandler = new RequestHandler(channelIO);
                dispatcherPool.register(socketChannel, SelectionKey.OP_READ, requestHandler);

            } catch (IOException ex) {
                System.out.println("accept ex: " + ex.getMessage());
            }

        }

    }
}
