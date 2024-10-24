package com.cowtowncoder.microb.jackson.vectors;

import java.io.*;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.cowtowncoder.microb.jackson.model.HuggingFaceCohereScidocsQueries;
import com.cowtowncoder.microb.jackson.model.InputData;
import com.cowtowncoder.microb.jackson.model.InputJson;
import com.cowtowncoder.microb.util.NopOutputStream;

/**
 * Test for measuring parsing performance of reading and/or writing
 * JSON content with mostly floating-point content (big {@code float[]} values)
 * with and without FP-optimizations.
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
public class VectorHeavyReadWrite
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    private final InputData input = InputData.get(InputJson.HUGGING_FACE_QUERIES);

    private final byte[] _serialized = input.serialized();
    private final HuggingFaceCohereScidocsQueries _deserialized = input.deserialized();

    private final ObjectMapper JSON_MAPPER_VANILLA = new JsonMapper();

    private final ObjectMapper JSON_MAPPER_FAST_FP;
    {
        JsonFactory f = JsonFactory.builder()
                .enable(StreamReadFeature.USE_FAST_BIG_NUMBER_PARSER)
                .enable(StreamReadFeature.USE_FAST_DOUBLE_PARSER)
                .enable(StreamWriteFeature.USE_FAST_DOUBLE_WRITER)
                .build();
        JSON_MAPPER_FAST_FP = new JsonMapper(f);
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    @Benchmark
    public void defaultRead(Blackhole bh) throws Exception {
        Object doc = _readUsing(JSON_MAPPER_VANILLA);
        bh.consume(doc);
    }

    @Benchmark
    public void defaultWrite(Blackhole bh) throws Exception {
        int len = _writeUsing(JSON_MAPPER_VANILLA);
        bh.consume(len);
    }

    @Benchmark
    public void defaultWriteAndRead(Blackhole bh) throws Exception {
        Object doc = _readWriteUsing(JSON_MAPPER_VANILLA);
        bh.consume(doc);
    }

    @Benchmark
    public void fastFPRead(Blackhole bh) throws Exception {
        Object doc = _readUsing(JSON_MAPPER_FAST_FP);
        bh.consume(doc);
    }

    @Benchmark
    public void fastFPWrite(Blackhole bh) throws Exception {
        int len = _writeUsing(JSON_MAPPER_FAST_FP);
        bh.consume(len);
    }

    @Benchmark
    public void fastFPWriteAndRead(Blackhole bh) throws Exception {
        Object doc = _readWriteUsing(JSON_MAPPER_FAST_FP);
        bh.consume(doc);
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private Object _readUsing(ObjectMapper mapper) throws IOException {
        return mapper.readValue(_serialized, HuggingFaceCohereScidocsQueries.class);
    }
    
    private int _writeUsing(ObjectMapper mapper) throws IOException {
        try (NopOutputStream out = new NopOutputStream()) {
            mapper.writeValue(out, _deserialized);
            return out.size();
        }
    }

    private Object _readWriteUsing(ObjectMapper mapper) throws IOException {
        Object doc = _readUsing(mapper);
        /*int len =*/ _writeUsing(mapper);
        return doc;
    }
    
    /*
    /**********************************************************************
    /* Entry point
    /**********************************************************************
     */

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(VectorHeavyReadWrite.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}
