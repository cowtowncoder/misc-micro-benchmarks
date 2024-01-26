package com.cowtowncoder.microb.numbers;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Test for measuring performance of integer division by 1000,
 * both with default straight-forward approach, and hand-optimized
 * alternative.
 *
 * @author Tatu Saloranta
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
//During dev, use lower; for real measurements, higher
//@Fork(value = 3)
@Fork(value = 1)
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 3, time = 1)
public class DivBy1000
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    protected final static int RANGE = 500_000_000;

    protected final static int REPS = 1;
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */
    
    @Benchmark
    public void perfSimpleDivision(Blackhole bh) {
        int total = 0;
        for (int i = 0; i < REPS; ++i) {
            total += _simpleDiv(RANGE);
        }
        bh.consume(total);
    }

    private int _simpleDiv(int range) 
    {
        int total = 0;
        for (int nr = 1; nr < range; ++nr) {
            int thousands = nr / 1000;
            total += thousands;
        }
        return total;
    }

    @Benchmark
    public void perfHandOptimized(Blackhole bh) {
        int total = 0;
        for (int i = 0; i < REPS; ++i) {
            total += _optimizedDiv(RANGE);
        }
        bh.consume(total);
    }

    private int _optimizedDiv(int range) 
    {
        int total = 0;
        for (int nr = 1; nr < range; ++nr) {
            int thousands = (int) (nr * 274_877_907L >>> 38);            
            total += thousands;
        }
        return total;
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */
}
