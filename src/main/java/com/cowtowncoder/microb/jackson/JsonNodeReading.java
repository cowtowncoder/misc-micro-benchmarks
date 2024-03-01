package com.cowtowncoder.microb.jackson;

import java.io.*;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Test for measuring parsing performance from JSON to
 * {@JsonNode} using different implemnetations
 *
 * @author Tatu Saloranta
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
//During dev, use lower; for real measurements, higher
//@Fork(value = 1)
@Fork(value = 3)
@Measurement(iterations = 3, time = 3)
@Warmup(iterations = 3, time = 1)
public class JsonNodeReading
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    private final byte[] INPUT_JSON = InputJson.FRIENDS_WITH_VECTORS.bytes();

    private final ObjectMapper JSON_MAPPER = JsonMapper.builder().build();

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */
    
    @Benchmark
    public void perfJacksonObjectMapper(Blackhole bh) throws Exception {
        JsonNode doc = JSON_MAPPER.readTree(inputJson());
        bh.consume(doc);
    }

    @Benchmark
    public void perfHandOptimized(Blackhole bh) throws Exception {
        Object doc = JSON_MAPPER.readValue(inputJson(), Object.class);
        bh.consume(doc);
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private InputStream inputJson() {
        return new ByteArrayInputStream(INPUT_JSON);
    }

}
