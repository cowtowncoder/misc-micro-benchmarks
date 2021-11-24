package com.cowtowncoder.microb.uuid;

import com.eatthepath.uuid.FastUUID;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.UUIDUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Test for measuring and comparing performance of
 * constructing a {@link java.util.UUID} from a valid 36-character
 * representation.
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
public class ValidUUIDFromString
{
    /*
    /**********************************************************************
    /* Constants, test input
    /**********************************************************************
     */

    // Let's test with 32 uuids
    private final static int UUIDS_TO_TEST = 32;

    private final static UUID[] INPUT_UUIDS = InputGenerator.generate(UUIDS_TO_TEST);
    private final static String[] INPUT_UUID_STRINGS = Stream.of(INPUT_UUIDS)
            .map(UUID::toString)
            .toArray(String[]::new);

    // We'll do basic sanity check that conversion actually works:
    private final static long EXP_TOTAL_SUM;
    static {
        long sum = 0;
        for (UUID uuid : INPUT_UUIDS) {
            sum += uuid.getLeastSignificantBits() + uuid.getMostSignificantBits();
        }
        EXP_TOTAL_SUM = sum;
    }

    /*
    /**********************************************************************
    /* Implementation 1: JDK-provided, slow
    /**********************************************************************
     */

    @Benchmark
    public long m1_UUID_with_JDK(Blackhole bh) {
        long totalSum = _uuidWithJdk(INPUT_UUID_STRINGS);
        return _check("JDK-UUID", totalSum);
    }

    private long _uuidWithJdk(String[] inputs) {
        long sum = 0L;
        for (String str : inputs) {
            UUID uuid = UUID.fromString(str);
            sum += uuid.getLeastSignificantBits() + uuid.getMostSignificantBits();
        }
        return sum;
    }

    /*
    /**********************************************************************
    /* Implementation 3: JUG's UUIDUtil
    /**********************************************************************
     */

    @Benchmark
    public long m2_UUID_with_JUG(Blackhole bh) {
        long totalSum = _uuidWithJUG(INPUT_UUID_STRINGS);
        return _check("JUG-UUID", totalSum);
    }

    private long _uuidWithJUG(String[] inputs) {
        long sum = 0L;
        for (String str : inputs) {
            UUID uuid = _uuidWithJUG(str);
            sum += uuid.getLeastSignificantBits() + uuid.getMostSignificantBits();
        }
        return sum;
    }

    private UUID _uuidWithJUG(String id) {
        return UUIDUtil.uuid(id);
    }

    /*
    /**********************************************************************
    /* Implementation 3: manual, code from Jackson (UUIDDeserializer.java)
    /**********************************************************************
     */

    @Benchmark
    public long m3_UUID_with_manual(Blackhole bh) {
        long totalSum = _uuidWithManual(INPUT_UUID_STRINGS);
        return _check("Manual-UUID", totalSum);
    }

    private long _uuidWithManual(String[] inputs) {
        long sum = 0L;
        for (String str : inputs) {
            UUID uuid = _uuidWithManual(str);
            sum += uuid.getLeastSignificantBits() + uuid.getMostSignificantBits();
        }
        return sum;
    }

    private UUID _uuidWithManual(String id) {
        if (id.length() != 36) {
            return _badFormat(id);
        }
        if ((id.charAt(8) != '-') || (id.charAt(13) != '-')
                || (id.charAt(18) != '-') || (id.charAt(23) != '-')) {
            return _badFormat(id);
        }
        long l1 = intFromChars(id, 0);
        l1 <<= 32;
        long l2 = ((long) shortFromChars(id, 9)) << 16;
        l2 |= shortFromChars(id, 14);
        long hi = l1 + l2;

        int i1 = (shortFromChars(id, 19) << 16) | shortFromChars(id, 24);
        l1 = i1;
        l1 <<= 32;
        l2 = intFromChars(id, 28);
        l2 = (l2 << 32) >>> 32; // sign removal, Java-style. Ugh.
        long lo = l1 | l2;

        return new UUID(hi, lo);
    }

    int intFromChars(String str, int index) {
        return (byteFromChars(str, index) << 24)
                + (byteFromChars(str, index+2) << 16)
                + (byteFromChars(str, index+4) << 8)
                + byteFromChars(str, index+6);
    }
    
    int shortFromChars(String str, int index) {
        return (byteFromChars(str, index) << 8) + byteFromChars(str, index+2);
    }
    
    int byteFromChars(String str, int index)
    {
        final char c1 = str.charAt(index);
        final char c2 = str.charAt(index+1);

        if (c1 <= 127 && c2 <= 127) {
            int hex = (HEX_DIGITS[c1] << 4) | HEX_DIGITS[c2];
            if (hex >= 0) {
                return hex;
            }
        }
        if (c1 > 127 || HEX_DIGITS[c1] < 0) {
            return _badChar(str, index, c1);
        }
        return _badChar(str, index+1, c2);
    }

    int _badChar(String uuidStr, int index, char c) {
        throw new IllegalArgumentException(
                String.format(
                "Non-hex character '%c' (value 0x%s), not valid for UUID String",
                c, Integer.toHexString(c)));
    }
    
    private UUID _badFormat(String id) {
        throw new IllegalArgumentException("Invalid UUID: \""+id+"\"");
    }

    final static int[] HEX_DIGITS = new int[127];
    static {
        Arrays.fill(HEX_DIGITS, -1);
        for (int i = 0; i < 10; ++i) { HEX_DIGITS['0' + i] = i; }
        for (int i = 0; i < 6; ++i) {
            HEX_DIGITS['a' + i] = 10 + i;
            HEX_DIGITS['A' + i] = 10 + i;
        }
    }

    /*
    /**********************************************************************
    /* Implementation 5: fast-uuid
    /**********************************************************************
     */

    @Benchmark
    public long m2_UUID_with_fast_uuid(Blackhole bh) {
        long totalSum = _uuidWithFastUuid(INPUT_UUID_STRINGS);
        return _check("fast-uuid-UUID", totalSum);
    }

    private long _uuidWithFastUuid(String[] inputs) {
        long sum = 0L;
        for (String str : inputs) {
            UUID uuid = _uuidWithFastUuid(str);
            sum += uuid.getLeastSignificantBits() + uuid.getMostSignificantBits();
        }
        return sum;
    }

    private UUID _uuidWithFastUuid(String id) {
        return FastUUID.parseUUID(id);
    }

    /*
    /**********************************************************************
    /* Helper class(es), methods
    /**********************************************************************
     */

    private long _check(String testName, long totalSum)
    {
        if (totalSum != EXP_TOTAL_SUM) {
            throw new IllegalArgumentException("Test '"+testName+"' fail: Excepted total sum "
                    +EXP_TOTAL_SUM+"; got "+EXP_TOTAL_SUM);
        }
        return totalSum;
    }

    static class InputGenerator {
        public static UUID[] generate(int count) {
            // No need for cryptographic random here; can use count as stable seed
            final RandomBasedGenerator gen = Generators.randomBasedGenerator(new Random(count));
            return IntStream.range(0, count)
                    .mapToObj(x -> gen.generate())
                    .toArray(UUID[]::new);
        }
    }
}
