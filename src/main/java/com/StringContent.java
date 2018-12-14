package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class StringContent implements Content {
    private static Charset ascii = Charset.forName("UTF-8");

    private String type;                // MIME type
    private String content;

    StringContent(CharSequence c, String t) {
        content = c.toString();
        if (!content.endsWith("\n"))
            content += "\n";
        type = t + "; charset=utf-8";
    }

    StringContent(CharSequence c) {
        this(c, "text/plain");
    }

    StringContent(Exception x) {
        StringWriter sw = new StringWriter();
        x.printStackTrace(new PrintWriter(sw));
        type = "text/plain; charset=utf-8";
        content = sw.toString();
    }

    public String type() {
        return type;
    }

    private ByteBuffer bb = null;

    private void encode() {
        if (bb == null)
            bb = ascii.encode(CharBuffer.wrap(content));
    }

    public long length() {
        encode();
        return bb.remaining();
    }

    public void prepare() {
        encode();
        bb.rewind();
    }

    public boolean send(ChannelIO cio) throws IOException {
        if (bb == null)
            throw new IllegalStateException();

        byte[] newbytes = new byte[bb.limit()];
        ByteBuffer cpBuffer = bb.asReadOnlyBuffer();
        cpBuffer.get(newbytes, 0, cpBuffer.limit());

        System.out.println("StringContent send: " + new String(newbytes));
        cio.write(bb);

        return bb.hasRemaining();
    }

    public void release() throws IOException {
    }
}
