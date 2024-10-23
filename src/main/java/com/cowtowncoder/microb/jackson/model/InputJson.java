package com.cowtowncoder.microb.jackson.model;

import java.io.*;

public enum InputJson
{
    // Data for JsonNode testing:

    FRIENDS_WITH_VECTORS("data/json/friends-with-vectors.json", FriendsWithVectors[].class),
    HUGGING_FACE_QUERIES("data/json/hf-cohere-beir-scidocs-queries.json",
            HuggingFaceCohereScidocsQueries.class)
    ;

    private final String _inputFilename;
    private final Class<?> _modelClass;

    private InputJson(String filename, Class<?> modelClass) {
        _inputFilename = filename;
        _modelClass = modelClass;
    }

    public byte[] load() throws IOException {
        return _read(_inputFilename);
    }

    public Class<?> modelClass() { return _modelClass; }

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
