package com;

import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

public class Acceptor implements Runnable {

    Selector acceptSelector;

    public Acceptor(ServerSocketChannel serverSocketChannel) throws Exception{
        this.acceptSelector = SelectorProvider.provider().openSelector();
        serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        try {
            while (acceptSelector.select() > 0) {
                Set keys = acceptSelector.selectedKeys();

                Iterator iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey selectionKey =  (SelectionKey) iterator.next();
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                        Socket newSocket = serverSocketChannel.accept().socket();
                        System.out.println("Accept connection from: " + newSocket.getRemoteSocketAddress());
                    }
                }

            }
        } catch (Exception ex) {
            System.out.println("Exception" + ex.getStackTrace());
        }


    }
}
