package com;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.nio.channels.FileChannel;

class FileContent implements Content {

    private static File ROOT = new File("root");

    private File fn;

    FileContent(URI uri) {
        fn = new File(ROOT,
                uri.getPath()
                        .replace('/',
                                File.separatorChar));
    }

    private String type = null;

    public String type() {
        if (type != null)
            return type;
        String nm = fn.getName();
        if (nm.endsWith(".html"))
            type = "text/html; charset=iso-8859-1";
        else if ((nm.indexOf('.') < 0) || nm.endsWith(".txt"))
            type = "text/plain; charset=iso-8859-1";
        else
            type = "application/octet-stream";
        return type;
    }

    private FileChannel fc = null;
    private long length = -1;
    private long position = -1;         // NB only; >= 0 if transferring

    public long length() {
        return length;
    }

    public void prepare() throws IOException {
        if (fc == null)
            fc = new RandomAccessFile(fn, "r").getChannel();
        length = fc.size();
        position = 0;                   // NB only
    }

    public boolean send(ChannelIO cio) throws IOException {
        if (fc == null)
            throw new IllegalStateException();
        if (position < 0)               // NB only
            throw new IllegalStateException();

        /*
         * Short-circuit if we're already done.
         */
        if (position >= length) {
            return false;
        }

        position += cio.transferTo(fc, position, length - position);
        return (position < length);
    }

    public void release() throws IOException {
        if (fc != null) {
            fc.close();
            fc = null;
        }
    }
}
