package com;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class Acceptor implements Runnable {

    Selector acceptSelector;
    Dispatcher dispatcher;

    public Acceptor(ServerSocketChannel serverSocketChannel, Dispatcher dispatcher) throws Exception{
        this.dispatcher = dispatcher;
        this.acceptSelector = SelectorProvider.provider().openSelector();
        serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            System.out.println("acceptSelector selecting");
            for (;;) {
            //while (acceptSelector.select() > 0) {

                acceptSelector.select();
                System.out.println("acceptSelector select return");
                Set keys = acceptSelector.selectedKeys();
                Iterator iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey =  (SelectionKey) iterator.next();
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        //socketChannel.register(acceptSelector, SelectionKey.OP_READ);

                        ChannelIO channelIO = ChannelIO.getInstance(socketChannel, false);

                        RequestHandler requestHandler = new RequestHandler(channelIO);

                        dispatcher.register(socketChannel, selectionKey.OP_READ, requestHandler);

                        //Socket newSocket = serverSocketChannel.accept().socket();
                       // System.out.println("Accept connection from: " + newSocket.getRemoteSocketAddress());

                    } else if (selectionKey.isReadable()) {

                    }
                }

            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getStackTrace());
        }
    }

    private static void response() {

    }
}
