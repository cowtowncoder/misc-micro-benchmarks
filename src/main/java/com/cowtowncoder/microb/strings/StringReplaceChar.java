package com.cowtowncoder.microb.strings;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

// 28-Aug-2021, tatu: Incomplete / Work-in-progress

/*
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
//During dev, use lower; for real measurements, higher
@Fork(value = 3)
//@Fork(value = 1)
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 3, time = 1)
*/
public class StringReplaceChar
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    public static final String[] TEST_STRINGS_FOR_ACTUAL_REPLACEMENT = new String[] {
            "'short'",
            "It's ok too!",
            "Value with 'single quoted stuff'",
            "Much longer \"thing\" with numbers (12498043589834, -124980932853, 0.999999) and 'text' too"
    };

    public static final String[] TEST_STRINGS_FOR_NO_REPLACEMENT = new String[] {
            "shortish",
            "Something medium long with other \"quotes\"",
            "Much longer \"thing\" with numbers (12498043589834, -124980932853, 0.999999)"
    };

    /*
    /**********************************************************************
    /* Main test methods: JDK, third-party
    /**********************************************************************
     */

    /*
    @Benchmark
    public int noReplacementsJDK(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_NO_REPLACEMENT) {
            count += _loopUsingStream(term);
        }
        return _verifyCount(count);
    }

    private int _noReplacementsJDK(String str) {
        return CHECKED_CHARS_AS_LIST.stream().anyMatch(ch -> str.indexOf(ch) >= 0) ? 1 : 0;
    }
    */
    
    /*
    /**********************************************************************
    /* Test method: hand-written
    /**********************************************************************
     */

}
