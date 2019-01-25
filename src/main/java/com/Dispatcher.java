package com;

import java.io.IOException;
import java.nio.ByteBuffer;
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
            System.out.println("Dispatcher register ex: "+  ex.toString());
            //ex.printStackTrace();
        }
    }

    private Object gate = new Object();

    private void dispatch() {
        try {
            System.out.println("Dispatcher selecting");
            selector.select();

            Iterator iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey)iterator.next();
                iterator.remove();
                System.out.println("receive data in thread " + Thread.currentThread().getName());
                //SocketChannel clientChannel = (SocketChannel)selectionKey.channel();
                //clientChannel.read()

                RequestHandler requestHandler = (RequestHandler)selectionKey.attachment();
                requestHandler.handle(selectionKey);


//                String resp = "HTTP/1.1 200 \n" +
//                        "Content-Type: application/xml\n" +
//                        "Content-Length: 0\n" +
//                        "Date: Mon, 10 Dec 2018 10:06:21 GMT";
//                ByteBuffer buffer = ByteBuffer.allocate(1000);
//
//                byte[] data = resp.toString().getBytes();
//                buffer.put(data);
//                buffer.flip();
//                clientChannel.write(buffer);

            }
        } catch (Exception ex) {
            System.out.println("dispatch ex: "+ ex.toString());
            //ex.printStackTrace();

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
