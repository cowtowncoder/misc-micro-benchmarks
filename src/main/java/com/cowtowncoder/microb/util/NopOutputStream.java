package com.cowtowncoder.microb.util;

import java.io.IOException;
import java.io.OutputStream;

public class NopOutputStream extends OutputStream
{
    protected int size = 0;
    
    public NopOutputStream() { }

    @Override
    public void write(int b) throws IOException { ++size; }

    @Override
    public void write(byte[] b) throws IOException { size += b.length; }

    @Override
    public void write(byte[] b, int offset, int len) throws IOException { size += len; }

    public NopOutputStream reset() {
        size = 0;
    	    return this;
    }
    public int size() { return size; }
}
