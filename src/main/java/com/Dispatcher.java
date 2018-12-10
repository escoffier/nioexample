package com;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Dispatcher implements Runnable{
    private Selector selector;

    public Dispatcher() throws IOException {
        this.selector = Selector.open();
    }

    void register(SelectableChannel channel, int ops, Object att) {
        try {
            synchronized (gate) {
                selector.wakeup();
                channel.register(selector, ops, att);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Object gate = new Object();

    private void dispatch() {
        try {
            selector.select();

            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey)iterator.next();
                iterator.remove();
                System.out.println("receive data from");

            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        synchronized (gate) { }

    }

    @Override
    public void run() {

        while (true) {
            dispatch();
        }
    }
}
