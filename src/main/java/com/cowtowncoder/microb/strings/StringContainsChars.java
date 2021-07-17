package com.cowtowncoder.microb.strings;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableList;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
//During dev, use lower; for real measurements, higher
//@Fork(value = 3)
@Fork(value = 1)
@Measurement(iterations = 5, time = 1)
@Warmup(iterations = 3, time = 1)
public class StringContainsChars
{
    /*
    /**********************************************************************
    /* Constants
    /**********************************************************************
     */

    // Source: characters to check (in ascending ascii order)
    public static final String CHECKED_CHARS_AS_STRING = "()*?[]";

    public static final char[] CHECKED_CHARS_AS_ARRAY = CHECKED_CHARS_AS_STRING.toCharArray();
    
    // Naive lookup iterators over List of Characters:
    public static final List<Character> CHECKED_CHARS_AS_LIST;
    static {
        ImmutableList.Builder<Character> b = ImmutableList.builder();
        for (char c : CHECKED_CHARS_AS_STRING.toCharArray()) {
            b.add(c);
        }
        CHECKED_CHARS_AS_LIST = b.build();
    }

    // Mask for ASCII range 32 - 95
    private final static long CHECKED_CHARS_MASK;
    static {
        long l = 0L;
        for (char c : CHECKED_CHARS_AS_ARRAY) {
            l |= (1L << (c - 32));
        }
        CHECKED_CHARS_MASK = l;
    }

    private final static CharMatcher CHECKED_CHARS_GUAVA_MATCHER = CharMatcher.anyOf(CHECKED_CHARS_AS_STRING);

    private final static Pattern CHECKED_CHARS_PATTERN;
    static {
        StringBuilder sb = new StringBuilder().append("[");
        // to add escaping dynamically
        for (char c : CHECKED_CHARS_AS_ARRAY) {
            sb.append(Pattern.quote(String.valueOf(c)));
        }
        CHECKED_CHARS_PATTERN = Pattern.compile(sb.append("]").toString());
    }
    
    // For test data find some balance; most with no special characters;
    // one or two with match, including first and last entries
    public static final String[] TEST_STRINGS_FOR_CHECKED_CHARS = new String[] {
            "basicName",
            "another one",
            "Foo*", // match
            "!",
            "Surely? Not", // match
            "Completely safe & sound!!!",
            "[nope]", // match
            "Typical_very",
            "--- Just some more stuff like that ---",
            "1",
            "Some-stuff-(optional?)" // match
    };

    // And of test Strings, 3 have characters of interest
    public static final int TEST_STRING_MATCHES = 4;

    /*
    /**********************************************************************
    /* Main test methods: initial case to optimize
    /**********************************************************************
     */

    @Benchmark
    public int method1_streamWithIndexOf(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _loopUsingStream(term);
        }
        return _verifyCount(count);
    }

    private int _loopUsingStream(String str) {
        return CHECKED_CHARS_AS_LIST.stream().anyMatch(ch -> str.indexOf(ch) >= 0) ? 1 : 0;
    }

    /*
    /**********************************************************************
    /* Main test methods/2: 3rd party
    /**********************************************************************
     */
    
    @Benchmark
    public int method2a_guavaBasedCheck(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _guavaBasedCheck(term);
        }
        return _verifyCount(count);
    }

    private int _guavaBasedCheck(String str) {
        return CHECKED_CHARS_GUAVA_MATCHER.matchesAnyOf(str) ? 1 : 0;
    }

    @Benchmark
    public int method2b_commonsLang3ContainsAny(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _commonsLang3Check(term);
        }
        return _verifyCount(count);
    }

    private int _commonsLang3Check(String str) {
        return StringUtils.containsAny(str, CHECKED_CHARS_AS_STRING) ? 1 : 0;
    }

    /*
    /**********************************************************************
    /* Main test methods/3: straight-forward, JDK
    /**********************************************************************
     */

    @Benchmark
    public int method3a_stringIndexOfTimesN(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _stringIndexOfTimesN(term);
        }
        return _verifyCount(count);
    }

    private int _stringIndexOfTimesN(String str) {
        final String toCheck = CHECKED_CHARS_AS_STRING;
        for (int i = 0, len = toCheck.length(); i < len; ++i) {
            if (str.indexOf(toCheck.charAt(i)) >= 0) {
                return 1;
            }
        }
        return 0;
    }

    @Benchmark
    public int method3b_scanStringAndIndexOf(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _scanStringAndIndexOf(term);
        }
        return _verifyCount(count);
    }

    private int _scanStringAndIndexOf(String str) {
        final String toCheck = CHECKED_CHARS_AS_STRING;
        for (int i = 0, len = str.length(); i < len; ++i) {
            if (toCheck.indexOf(str.charAt(i)) >= 0) {
                return 1;
            }
        }
        return 0;
    }

    @Benchmark
    public int method3c_scanWithSwitch(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _scanWithSwitchCheck(term);
        }
        return _verifyCount(count);
    }

    private int _scanWithSwitchCheck(String str) {
        for (int i = 0, len = str.length(); i < len; ++i) {
            //"()*?[]";
            switch (str.charAt(i)) {
            case '(': // 0x28
            case ')': // 0x29
            case '*': // 0x2A
            case '?': // 0x3F
            case '[': // 0x5B
            case ']': // 0x5C
                return 1;
            }
        }
        return 0;
    }

    @Benchmark
    public int method3d_toCharArrayWithSwitch(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _toCharArrayWithSwitch(term.toCharArray());
        }
        return _verifyCount(count);
    }

    private int _toCharArrayWithSwitch(char[] str) {
        for (int i = 0, len = str.length; i < len; ++i) {
            //"()*?[]";
            switch (str[i]) {
            case '(': // 0x28
            case ')': // 0x29
            case '*': // 0x2A
            case '?': // 0x3F
            case '[': // 0x5B
            case ']': // 0x5C
                return 1;
            }
        }
        return 0;
    }

    /*
    /**********************************************************************
    /* Main test methods/4: advanced
    /**********************************************************************
     */

    @Benchmark
    public int method4a_jdkRegExpBasedCheck(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _regExpBasedCheck(term);
        }
        return _verifyCount(count);
    }

    private int _regExpBasedCheck(String str) {
        return CHECKED_CHARS_PATTERN.matcher(str).find() ? 1 : 0;
    }

    @Benchmark
    public int method4b_bitsetBasedCheck(Blackhole bh) {
        int count = 0;
        for (String term : TEST_STRINGS_FOR_CHECKED_CHARS) {
            count += _bitsetBasedCheck(term);
        }
        return _verifyCount(count);
    }

    private int _bitsetBasedCheck(String str) {
        for (int i = 0, len = str.length(); i < len; ++i) {
            final int ch = str.charAt(i);
            int offset = ch - 32;
            if ((offset < 64) && (offset >= 0)
                    && (CHECKED_CHARS_MASK & (1L << offset)) != 0) {
                return 1;
            }
        }
        return 0;
    }
    
    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private int _verifyCount(int count) {
        if (count != TEST_STRING_MATCHES) {
            throw new RuntimeException(String.format("Wrong: should get %d matches, got %s",
                    TEST_STRING_MATCHES, count));
        }
        return count;
    }
}
