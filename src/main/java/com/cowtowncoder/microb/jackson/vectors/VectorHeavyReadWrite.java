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
    public void readUsingVanilla(Blackhole bh) throws Exception {
        HuggingFaceCohereScidocsQueries doc = _readUsing(JSON_MAPPER_VANILLA);
        bh.consume(doc);
    }

    @Benchmark
    public void readUsingFastFP(Blackhole bh) throws Exception {
        HuggingFaceCohereScidocsQueries doc = _readUsing(JSON_MAPPER_FAST_FP);
        bh.consume(doc);
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private HuggingFaceCohereScidocsQueries _readUsing(ObjectMapper mapper) throws IOException {
        return mapper.readValue(_serialized, HuggingFaceCohereScidocsQueries.class);
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
