package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class ChannelIO {

    private ByteBuffer buffer;


    private static  int requestBufferSize = 4096;

    private SocketChannel socketChannel;

    public ChannelIO(SocketChannel socketChannel, boolean blocking) throws IOException  {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(blocking);
    }

    public static ChannelIO getInstance (SocketChannel socketChannel, boolean blocking) throws IOException {
        ChannelIO channelIO = new ChannelIO(socketChannel, blocking);
        channelIO.buffer = ByteBuffer.allocate(requestBufferSize);
        return channelIO;
    }

    private void resizeBuffer(int remaining) {
        if (buffer.remaining() < remaining) {
            ByteBuffer bb = ByteBuffer.allocate(buffer.capacity() * 2);
            bb.flip();
            bb.put(buffer);
            buffer = bb;
        }
    }

    public ByteBuffer getReadBuf() {
        return buffer;
    }

    public int read() throws IOException {
        resizeBuffer(requestBufferSize /20);
        return socketChannel.read(buffer);
    }

    public int write(ByteBuffer buf) throws IOException {
        return socketChannel.write(buf);
    }

    boolean dataFlush() throws IOException {
        return true;
    }

    long transferTo(FileChannel fc, long pos, long len) throws IOException {
        return fc.transferTo(pos, len, socketChannel);
    }

    boolean shutdown() throws IOException {
        return true;
    }

    void close() throws IOException {
        socketChannel.close();
    }
}
