package com.cowtowncoder.microb.strings;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Test for measuring and comparing performance of
 * "Concate two Strings separated by a fixed short String (or char)"
 * implementations.
 *<p>
 * No published blog post yet.
 *
 * @author Tatu Saloranta
 */
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
//During dev, use lower; for real measurements, higher
@Fork(value = 3)
//@Fork(value = 1)
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 3, time = 1)
public class StringConcatenation
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    static final String[] TEST_STRINGS_FIRST = new String[] {
            "Namespace1",
            "www.facebook.com",
            "afa46602-6b19-4425-93bf-d37ccd5352f9",
            "x"
    };

    static final String[] TEST_STRINGS_SECOND = new String[] {
            "abc",
            "03d8ded0-cf09-4386-9ad0-1b7ad0b04a27",
            "www.altavista.com",
            "16378"
    };

    static final String[] TEST_PAIRS = new String[TEST_STRINGS_FIRST.length
                                                  * TEST_STRINGS_SECOND.length
                                                  * 2 // pairs (first & second)
                                                  ];
    static {
        int ix = 0;
        for (int i = 0; i < TEST_STRINGS_FIRST.length; ++i) {
            for (int j = 0; j < TEST_STRINGS_SECOND.length; ++j) {
                TEST_PAIRS[ix++] = TEST_STRINGS_FIRST[i];
                TEST_PAIRS[ix++] = TEST_STRINGS_SECOND[j];
            }
        }
    }

    // Let's also verify the results
    static final int EXPECTED_TOTAL_COUNT;
    static {
        int tc = 0;
        for (int i = 0, end = TEST_PAIRS.length; i < end; i += 2) {
            int entryLen = TEST_PAIRS[i].length() + 1 + TEST_PAIRS[i+1].length();
            // since we do first/second and second/first permutations, count twice:
            tc += 2 * entryLen;
        }
        EXPECTED_TOTAL_COUNT = tc;
    }

    /*
    /**********************************************************************
    /* Implementation 1: Convenient but slow, String.format()
    /**********************************************************************
     */

    @Benchmark
    public int method1_StringFormat(Blackhole bh) {
        int totalCount = 0;
        for (int i = 0, end = TEST_PAIRS.length; i < end; i += 2) {
            // do both permutations for fun
            totalCount += _method1_StringFormat(TEST_PAIRS[i], TEST_PAIRS[i+1]);
            totalCount += _method1_StringFormat(TEST_PAIRS[i+1], TEST_PAIRS[i]);
        }
        return _check("StringFormat", totalCount);
    }

    private final int _method1_StringFormat(String first, String second) {
        String concat = String.format("%s.%s", first, second);
        return concat.length();
    }
    
    /*
    /**********************************************************************
    /* Implementation 2: More efficient, StringBuilder
    /**********************************************************************
     */

    @Benchmark
    public int method2_StringBuilder(Blackhole bh) {
        int totalCount = 0;
        for (int i = 0, end = TEST_PAIRS.length; i < end; i += 2) {
            // both permutations for all tests
            totalCount += _method2_StringBuilder(TEST_PAIRS[i], TEST_PAIRS[i+1]);
            totalCount += _method2_StringBuilder(TEST_PAIRS[i+1], TEST_PAIRS[i]);
        }
        return _check("StringBuilder", totalCount);
    }

    private final int _method2_StringBuilder(String first, String second) {
        String concat = new StringBuilder().append(first).append('.').append(second)
                .toString();
        return concat.length();
    }

    /*
    /**********************************************************************
    /* Implementation 3: Hand-written, still simpleish
    /**********************************************************************
     */

    @Benchmark
    public int method3_StringBuilderWithPrealloc(Blackhole bh) {
        int totalCount = 0;
        for (int i = 0, end = TEST_PAIRS.length; i < end; i += 2) {
            // both permutations for all tests
            totalCount += _method3_StringBuilderWithPrealloc(TEST_PAIRS[i], TEST_PAIRS[i+1]);
            totalCount += _method3_StringBuilderWithPrealloc(TEST_PAIRS[i+1], TEST_PAIRS[i]);
        }
        return _check("StringBuilderWithPrealloc", totalCount);
    }

    private final int _method3_StringBuilderWithPrealloc(String first, String second) {
        final int len = first.length() + 1 + second.length();
        String concat = new StringBuilder(len).append(first).append('.').append(second)
                .toString();
        return concat.length();
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private int _check(String testName, int actualTotalCount) {
        if (actualTotalCount != EXPECTED_TOTAL_COUNT) {
            throw new IllegalArgumentException("Test '"+testName+"' fail: Excepted total count "
                    +EXPECTED_TOTAL_COUNT+"; got "+actualTotalCount);
        }
        return actualTotalCount;
    }
}
