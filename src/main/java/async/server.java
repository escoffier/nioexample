package async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

public class server {

    static AsynchronousServerSocketChannel listener;

    private static Charset utf8 = Charset.forName("UTF-8");

    public static void main( String[] args ) throws Exception {


        System.out.println("AsynchronousServerSocketChannel test");

        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withFixedThreadPool(4, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                System.out.println("Create new thread");
                return new Thread();
            }
        });

        listener = //AsynchronousChannelProvider.provider().openAsynchronousServerSocketChannel(channelGroup).bind(new InetSocketAddress(6999));
                AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(6999));

        while (true) {
            System.out.println("----AsynchronousServerSocketChannel test");
            listener.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel ch, Void attachment) {

                    if (listener.isOpen()) {
                        try {
                            System.out.println("new connection: " + ch.getRemoteAddress().toString()+ "  ---thread: " + Thread.currentThread().getName());
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                        listener.accept(null, this);
                    }

                    ByteBuffer buffer = ByteBuffer.allocate(32);

                    Future<Integer> len =  ch.read(buffer);

                    try {
                        byte[] buf = new byte[len.get()];
                        buffer.flip();

                        buffer.get(buf);
                        System.out.println("received data: " +  new String(buf));
                    } catch (Exception ex) {
                        System.out.println("read data ex: " + ex.getMessage());
                    }

                    CharBuffer charBuffer = CharBuffer.allocate(128);
                    charBuffer.put("my name is robbie");

                    String reply = new String("robbie");

                    ch.write(utf8.encode(charBuffer));
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    System.out.println("new connection: " + exc.getMessage());
                }
            });

            System.in.read();
        }

    }

}
