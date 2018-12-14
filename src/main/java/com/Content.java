package com;

interface Content extends Sendable {

    String type();

    // Returns -1 until prepare() invoked
    long length();

}
