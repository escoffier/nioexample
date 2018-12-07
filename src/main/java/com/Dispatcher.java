package com;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.Selector;

public class Dispatcher implements Runnable{
    private Selector selector;

    public Dispatcher() throws IOException {
        this.selector = Selector.open();
    }

    void register(SelectableChannel channel, int ops, Object att) {
        channel.register(selector, ops, att);

    }

    @Override
    public void run() {

    }
}
