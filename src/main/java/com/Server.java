package com;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Queue;

public class Server {

//    String host;
//    int port;
    ByteBuffer byteBuffer;

    //Selector readSelector;

    //Acceptor acceptor;

    Thread acceptThread;

    ServerSocketChannel serverSocketChannel;

    private Queue<ServerSocketChannel> socketQueue;

    public Server(String host, int port) throws Exception{
        byteBuffer = ByteBuffer.allocate(128);

        //readSelector = Selector.open();


        //acceptConnections(host, port);
    }

    public void start(String host, int port) {

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(host, port);
            serverSocketChannel.socket().bind(address);

            Dispatcher dispatcher = new Dispatcher();
            acceptThread = new Thread(new Acceptor(serverSocketChannel, dispatcher));
            acceptThread.start();
            dispatcher.run();

        } catch (Exception ex) {
            //System.out.println("ex: " + ex.getStackTrace());
            ex.printStackTrace();
        }

        try {
            acceptThread.join();
        } catch (InterruptedException ex) {
            System.out.println("thread Interrupted ");
        }
    }

    private void acceptConnections(String host, int port) throws Exception {

//        serverSocketChannel = ServerSocketChannel.open();
//
//        serverSocketChannel.configureBlocking(false);
//
//        InetSocketAddress address = new InetSocketAddress(host, port);
//
//        serverSocketChannel.socket().bind(address);


//        try {
//            Selector acceptSelector = SelectorProvider.provider().openSelector();
//
//            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//
//            serverSocketChannel.configureBlocking(false);
//
//            InetSocketAddress address = new InetSocketAddress(host, port);
//
//            serverSocketChannel.socket().bind(address);
//
//            SelectionKey acceptKey = serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT);
//
//            while (acceptSelector.select() > 0){
//
//               // System.out.println("selected channels ready for IO operations");
//
//                Set readyKeys = acceptSelector.selectedKeys();
//
//                Iterator iterator = readyKeys.iterator();
//
//                while (iterator.hasNext()){
//
//                    SelectionKey selectionKey = (SelectionKey)iterator.next();
//                    if (selectionKey.isAcceptable()) {
//
//                        ServerSocketChannel socketChannel = (ServerSocketChannel)selectionKey.channel();
//                        Socket newSocket = socketChannel.accept().socket();
//                        System.out.println("Accept connection from: " + newSocket.getRemoteSocketAddress());
//                        socketChannel.register(readSelector, SelectionKey.OP_READ);
//                        socketQueue.add(socketChannel);
//
//                    } else if (selectionKey.isReadable()) {
//                        SocketChannel readChannel = (SocketChannel)selectionKey.channel();
//                        readChannel.read(byteBuffer);
//
//                        //Socket socket = nextReady.socket().;
//                        System.out.println("recv data  from: " + byteBuffer);
//
//                    }
//
//                    iterator.remove();
//                }
//            }
//
//        } catch (Exception ex) {
//            System.out.println("ex: " + ex.getStackTrace());
//        }

    }

    public static void main( String[] args ) throws Exception
    {

        //ReadFile();
        //writeFile();
        //ReadMappedFile();
        Server server = new Server("192.168.21.197", 18099);
        server.start("192.168.21.197", 8090);
        System.out.println( "end of example!" );
    }
}
