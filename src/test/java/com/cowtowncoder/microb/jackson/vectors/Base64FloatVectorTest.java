package com.cowtowncoder.microb.jackson.vectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class Base64FloatVectorTest
{
    @Test
    public void testRoundTrip() throws Exception
    {
        SimpleModule mod = new SimpleModule()
                .addDeserializer(float[].class, new Base64FloatVectorDeserializer())
                .addSerializer(float[].class, new Base64FloatVectorSerializer());
        JsonMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .build();
        float[] input = new float[] { 1.0f, 2.0f, -0.5f, 12.25f, -99999.5f, 0.0f };
        String json = mapper.writeValueAsString(input);
        float[] output = mapper.readValue(json, float[].class);
        assertArrayEquals(input, output);
    }
}
