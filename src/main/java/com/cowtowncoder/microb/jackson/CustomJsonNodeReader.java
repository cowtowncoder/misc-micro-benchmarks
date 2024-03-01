package com.cowtowncoder.microb.jackson;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomJsonNodeReader
{
    private final ObjectMapper _mapper;

    public CustomJsonNodeReader(ObjectMapper m) {
        _mapper = m;
    }

    public JsonNode readTree(InputStream in) {
        try {
            return _mapper.readTree(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
