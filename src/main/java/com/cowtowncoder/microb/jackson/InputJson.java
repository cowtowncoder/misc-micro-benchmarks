package com.cowtowncoder.microb.jackson;

import java.io.*;

public enum InputJson
{
    // Data for JsonNode testing:

    FRIENDS_WITH_VECTORS("data/json/friends-with-vectors.json")
    ;

    private final byte[] _json;

    private InputJson(String filename) {
        try {
            _json = _read(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] bytes() { return _json; }
    
    private static byte[] _read(String filename) throws IOException
    {
        File f = new File(filename);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(5000);
        byte[] buf = new byte[4000];
        int count;
        FileInputStream in = new FileInputStream(f);
        
        while ((count = in.read(buf)) > 0) {
            bytes.write(buf, 0, count);
        }
        in.close();
        bytes.close();
        return bytes.toByteArray();
    }
}
