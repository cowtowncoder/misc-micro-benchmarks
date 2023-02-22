package com.cowtowncoder.microb.numbers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Test for measuring and comparing performance of
 * parsing longer numbers of types:
 *<ul>
 *<li>{@link java.lang.Double}
 * </li>
 *<li>{@link java.math.BigDecimal}
 * </li>
 *<li>{@link java.math.BigInteger}
 * </li>
 * </ul>
 *<p>
 * There is a published
 * <a href="https://cowtowncoder.medium.com/measuring-performance-of-java-string-format-or-lack-thereof-2e1c6a13362c">blog post</a>
 * available for some analysis.
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
public class LongNumberParsing
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    protected final static int NUM_LEN = 1000;

    protected final static int REPS = 100;
    
    protected final static String LONG_INT;
    protected final static String LONG_FP;

    // Create similar number (same length) for tested cases
    static {
        String digits = "1234567890";

        StringBuilder sbInt = new StringBuilder(1000).append(digits).append('1');
        StringBuilder sbFp = new StringBuilder(1000).append(digits).append('.');

        while (sbInt.length() < NUM_LEN) {
            sbInt.append(digits);
            sbFp.append(digits);
        }
        LONG_INT = sbInt.toString();
        LONG_FP = sbFp.toString();
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */
    
    @Benchmark
    public void perfParseDouble(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < REPS; ++i) {
            Double d = Double.parseDouble(LONG_FP);
            if (d > 0.0) {
                ++count;
            }
        }
        _verify(count);
        bh.consume(count);
    }

    @Benchmark
    public void perfParseBigDecimal(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < REPS; ++i) {
            BigDecimal d = new BigDecimal(LONG_FP);
            if (d.signum() > 0) {
                ++count;
            }
        }
        _verify(count);
        bh.consume(count);
    }

    @Benchmark
    public void perfParseBigInteger(Blackhole bh) {
        int count = 0;
        for (int i = 0; i < REPS; ++i) {
            BigInteger I = new BigInteger(LONG_INT);
            if (I.signum() > 0) {
                ++count;
            }
        }
        _verify(count);
        bh.consume(count);
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private void _verify(int count) {
        if (count != REPS) {
            throw new IllegalStateException("Count wrong: "+count);
        }
    }
}
