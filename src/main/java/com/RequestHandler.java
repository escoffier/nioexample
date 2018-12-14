package com;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class RequestHandler implements Handler {
    private ChannelIO channelIO;
    private ByteBuffer readBuffer;
    private Request request;
    private Reply reply;
    private boolean requestReceived = false;

    RequestHandler(ChannelIO cio) {
        this.channelIO = cio;
    }

    private boolean receive(SelectionKey sk) throws IOException {
        if (requestReceived) {
            return true;
        }
        if (channelIO.read() < 0 || Request.isComplete(channelIO.getReadBuf())){
            readBuffer = channelIO.getReadBuf();
            return (requestReceived = true);
        }
        return false;
    }

    private boolean parse() throws IOException {
        try {
            request = Request.parse(readBuffer);
            return true;
        } catch (MalformedRequestException x) {
            reply = new Reply(Reply.Code.BAD_REQUEST,
                    new StringContent(x));
        }
        return false;
    }

    private void build() throws IOException {
        Request.Action action = request.action();
        if ((action != Request.Action.GET) &&
                (action != Request.Action.HEAD)) {
            reply = new Reply(Reply.Code.METHOD_NOT_ALLOWED,
                    new StringContent(request.toString()));
        }
        reply = new Reply(Reply.Code.OK,
                new StringContent("hello world"));

//        reply = new Reply(Reply.Code.OK,
//                new FileContent(request.uri()), action);
    }

    private boolean send() throws IOException {
        try {
            return reply.send(channelIO);
        } catch (IOException x) {
            if (x.getMessage().startsWith("Resource temporarily")) {
                System.err.println("## RTA");
                return true;
            }
            throw x;
        }
    }


    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        try {
            if (request == null) {
                if (!receive(selectionKey)) {
                    return;
                }

                readBuffer.flip();
                if (parse()) {
                    build();
                }

                try {
                    reply.prepare();
                } catch (IOException ioex) {
                    reply.release();
                    reply = new Reply(Reply.Code.NOT_FOUND,
                            new StringContent(ioex));
                    reply.prepare();
                }

                if (send()) {
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                } else {
                    if (channelIO.shutdown()){
                        channelIO.close();
                        reply.release();
                    }
                }
             } else {
                if (!send()) {  // Should be rp.send()
                    if (channelIO.shutdown()) {
                        channelIO.close();
                        reply.release();
                    }
                }
            }

        } catch (IOException x) {
            String m = x.getMessage();
            if (!m.equals("Broken pipe") &&
                    !m.equals("Connection reset by peer")) {
                System.err.println("RequestHandler: " + x.toString());
            }

            try {
                /*
                 * We had a failure here, so we'll try to be nice
                 * before closing down and send off a close_notify,
                 * but if we can't get the message off with one try,
                 * we'll just shutdown.
                 */
                channelIO.shutdown();
            } catch (IOException e) {
                // ignore
            }

            channelIO.close();
            if (reply !=  null) {
                reply.release();
            }
        }
    }
}
