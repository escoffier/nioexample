package com;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public interface Handler {
    void handle(SelectionKey selectionKey) throws IOException;
}
