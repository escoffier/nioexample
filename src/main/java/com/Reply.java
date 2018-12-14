package com;


import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class Reply implements Sendable {

    /**
     * A helper class which define the HTTP response codes
     */
    static class Code {

        private int number;
        private String reason;
        private Code(int i, String r) { number = i; reason = r; }
        public String toString() { return number + " " + reason; }

        static Code OK = new Code(200, "OK");
        static Code BAD_REQUEST = new Code(400, "Bad Request");
        static Code NOT_FOUND = new Code(404, "Not Found");
        static Code METHOD_NOT_ALLOWED = new Code(405, "Method Not Allowed");

    }

    private Code code;
    private Content content;
    private boolean headersOnly;

    private ByteBuffer headerBuffer = null;

    private static String CRLF = "\r\n";
    private static Charset utf8 = Charset.forName("UTF-8");

    Reply(Code rc, Content c) {
        this(rc, c , null);
    }

    Reply(Code rc, Content c, Request.Action head) {
        code = rc;
        content = c;
        headersOnly = (head == Request.Action.HEAD);
    }

    private ByteBuffer headers() {
        CharBuffer buffer = CharBuffer.allocate(1024);

        for (;;) {
            try {
                buffer.put("HTTP/1.0 ").put(code.toString()).put(CRLF);
                buffer.put("Server: niossl/0.1").put(CRLF);
                buffer.put("Content-type: ").put(content.type()).put(CRLF);
                buffer.put("Content-length: ")
                        .put(Long.toString(content.length())).put(CRLF);
                buffer.put(CRLF);
                break;

            } catch (BufferOverflowException ex) {
                assert buffer.capacity() < (1<<16);
                buffer = CharBuffer.allocate(buffer.capacity() * 2);
                continue;
            }
        }
        buffer.flip();
        return utf8.encode(buffer);
    }

    @Override
    public void prepare() throws IOException {
        content.prepare();
        headerBuffer = headers();

    }

    @Override
    public boolean send(ChannelIO cio) throws IOException {

        if (headerBuffer == null) {
            throw  new IllegalStateException();
        }
        if (headerBuffer.hasRemaining()) {
            if (cio.write(headerBuffer) > 0){
                return true;
            }
        }

        if (!headersOnly) {
            if (content.send(cio))
                return true;
        }

        if (!cio.dataFlush())
            return true;

        return false;
    }

    @Override
    public void release() throws IOException {

            content.release();
    }
}
