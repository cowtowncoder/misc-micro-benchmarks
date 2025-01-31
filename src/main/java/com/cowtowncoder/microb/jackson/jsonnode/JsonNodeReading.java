package com.cowtowncoder.microb.jackson.jsonnode;

import java.io.*;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.cowtowncoder.microb.jackson.model.InputData;
import com.cowtowncoder.microb.jackson.model.InputJson;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Test for measuring parsing performance from JSON to
 * {@JsonNode} using different implementations
 *
 * @author Tatu Saloranta
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
//During dev, use lower; for real measurements, higher
//@Fork(value = 1)
@Fork(value = 3)
@Measurement(iterations = 3, time = 3)
@Warmup(iterations = 4, time = 1)
public class JsonNodeReading
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    private final byte[] INPUT_JSON = InputData.get(InputJson.FRIENDS_WITH_VECTORS).serialized();

    private final ObjectMapper JSON_MAPPER;
    {
        JsonFactory f = JsonFactory.builder()
                .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                .enable(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER)
                .enable(StreamReadFeature.USE_FAST_DOUBLE_PARSER)
                .enable(StreamWriteFeature.USE_FAST_DOUBLE_WRITER)
                .build();
        JSON_MAPPER = JsonMapper.builder(f)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .build();
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    @Benchmark
    public void perfDefaultJacksonReader(Blackhole bh) throws Exception {
        JsonNode doc = JSON_MAPPER.readTree(inputJson());
        bh.consume(doc);
    }

    @Benchmark
    public void perfOptimizedBasic(Blackhole bh) throws Exception {
        JsonParser p = JSON_MAPPER.createParser(inputJson());
        JsonNode doc = new CustomJsonNodeReader(JSON_MAPPER, p,
                CustomJsonNodeReader.VectorsAs.LIST_OF_NUMBERS)
                    .readTree();
        p.close();
        bh.consume(doc);
    }

    @Benchmark
    public void perfOptimizedFloatArray(Blackhole bh) throws Exception {
        JsonParser p = JSON_MAPPER.createParser(inputJson());
        JsonNode doc = new CustomJsonNodeReader(JSON_MAPPER, p,
                CustomJsonNodeReader.VectorsAs.ARRAY_OF_FLOATS)
                    .readTree();
        p.close();
        bh.consume(doc);
    }

    @Benchmark
    public void perfOptimizedStringList(Blackhole bh) throws Exception {
        JsonParser p = JSON_MAPPER.createParser(inputJson());
        JsonNode doc = new CustomJsonNodeReader(JSON_MAPPER, p,
                CustomJsonNodeReader.VectorsAs.LIST_OF_STRINGS)
                	.readTree();
        p.close();
        bh.consume(doc);
    }

    @Benchmark
    public void perfOptimizedVectorSkip(Blackhole bh) throws Exception {
        JsonParser p = JSON_MAPPER.createParser(inputJson());
        JsonNode doc = new CustomJsonNodeReader(JSON_MAPPER, p,
                CustomJsonNodeReader.VectorsAs.SKIP)
                    .readTree();
        p.close();
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

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(JsonNodeReading.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
