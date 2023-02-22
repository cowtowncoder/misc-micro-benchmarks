# Miscellaneous Micro-benchmarks by mr. Jackson

This is a repo containing a set of Java micro-benchmarks using
[jmh](https://openjdk.java.net/projects/code-tools/jmh/) framework.
Tests focus on use cases that:

1. Are relatively common (author has bumped into them in real codebases)
2. Have a simple canonical, usually JDK-provided way to do things
3. Where the canonical approach appears to have non-trivial overhead associated with it
4. And there is at least one easily applicable alternative with (expected) improved performance

Code written by @cowtowncoder unless otherwise noted; blog posts for results
published at [Cowtowncoder@medium](https://cowtowncoder.medium.com/).

## Test currently included

Currently we have following tests (along with blog posts about results)

* Number parsing tests `com.cowtowncoder.microb.numbers`
    * `LongNumberParsing` (2023-02-21) -- not blogged about yet
* String tests under `com.cowtowncoder.microb.strings`
    * `StringContainsChars`, see: [Measuring “String.indexOfAny(String)” performance](https://cowtowncoder.medium.com/measuring-string-indexofany-string-performance-java-fecb9eb473fa) (2021-07-21)
    * `StringConcatenation`, see: [Measuring performance of Java String.format()](https://cowtowncoder.medium.com/measuring-performance-of-java-string-format-or-lack-thereof-2e1c6a13362c) (2021-08-29)
* UUID tests under `com.cowtowncoder.microb.uuid`
    * `ValidUUIDFromString`, see: [Measuring performance of Java UUID.fromString()](https://cowtowncoder.medium.com/measuring-performance-of-java-uuid-fromstring-or-lack-thereof-d16a910fa32a) (2021-09-14)

-----

## Test Descriptions

### LongNumberParsing test

Tests simple parsing of 1000-digit long numbers of types:

* `java.lang.Double`
* `java.math.BigDecimal`
* `java.math.BigInteger`

where number used for first two is identical, and for `BigInteger` same digits but without decimal point.

For further information check out `com.cowtowncoder.microb.numbers.LongNumberParsing`
but here are quick numbers from running (on JDK 8 and 17, very similar results)

java -jar target/microbenchmarks.jar LongNumberParsing

```
Benchmark                               Mode  Cnt    Score   Error  Units
LongNumberParsing.perfParseBigDecimal  thrpt    5  639.419 ± 3.421  ops/s
LongNumberParsing.perfParseBigInteger  thrpt    5  672.288 ± 1.835  ops/s
LongNumberParsing.perfParseDouble      thrpt    5  435.754 ± 2.247  ops/s
```

so `BigDecimal` and `BigInteger` are -- interestingly enough -- equally fast/slow; and `java.lang.Double` is bit slower (maybe due to base-2 vs base-10 difference?)

### StringContainsChars test

This set of tests is for comparing various ways of answering the question:

    Does this String contain one of these characters?

which could be hypothetically supported by JDK like so (but isn't):

```
String xmlToCheck = ...;
if (textToCheck.containsAnyOf("&<>'\"")) {
   // do some escape/quote magic
}
```

For further information check out `com.cowtowncoder.microb.strings.StringContainsChars`
but here are quick numbers from running

    java -jar target/microbenchmarks.jar StringContainsChars

```
Benchmark                                              Mode  Cnt        Score        Error  Units
StringContainsChars.method1_streamWithIndexOf         thrpt   15  1451222.313 ±   6683.463  ops/s
StringContainsChars.method2a_stringIndexOfTimesN      thrpt   15  3485362.606 ± 147550.312  ops/s
StringContainsChars.method2b_scanStringAndIndexOf     thrpt   15  6007080.938 ±  24865.942  ops/s
StringContainsChars.method2c_scanWithSwitch           thrpt   15  9464115.514 ± 377737.768  ops/s
StringContainsChars.method2d_toCharArrayWithSwitch    thrpt   15  8134744.129 ± 386362.447  ops/s
StringContainsChars.method3a_guavaBasedCheck          thrpt   15  1840523.997 ±  12226.246  ops/s
StringContainsChars.method3b_commonsLang3ContainsAny  thrpt   15  5335174.154 ±  19938.776  ops/s
StringContainsChars.method4a_jdkRegExpBasedCheck      thrpt   15  1838346.183 ±   6484.875  ops/s
StringContainsChars.method4b_bitsetBasedCheck         thrpt   15  9819436.570 ± 300016.151  ops/s
```

There is a blog post
[Measuring “String.indexOfAny(String)” performance](https://cowtowncoder.medium.com/measuring-string-indexofany-string-performance-java-fecb9eb473fa) for further discussion

### StringConcatenation test

This set of tests is for comparing various ways of producing a String that consists of 2 argument Strings, separated by a constant separator (`char` or `String`): something for which a simple readable example looks like:

    String qname = String.format("%s.%s", namespace, localName);

but for which there are more efficient "manual" alternatives.

For further information check out `com.cowtowncoder.microb.strings.StringConcatenation`.
You may can also run the tests yourself with something like:

    java -jar target/microbenchmarks.jar StringContainsChars

There is a blog post
[Measuring performance of Java String.format()](https://cowtowncoder.medium.com/measuring-performance-of-java-string-format-or-lack-thereof-2e1c6a13362c) for further discussion on the test case and observed results.

### ValidUUIDFromString

This set of tests is for comparing various ways of reading a `java.util.UUID` from a String representation. With JDK you can do that with

    UUID uuid = UUID.fromString(uuidAsString);

but for which there are more efficient alternatives in existence as well.

For further information check out `com.cowtowncoder.microb.uuid.ValidUUIDFromString`.
You may can also run the tests yourself with something like:

    java -jar target/microbenchmarks.jar ValidUUIDFromString

There is a blog post
[Measuring performance of Java UUID.fromString()](https://cowtowncoder.medium.com/measuring-performance-of-java-uuid-fromstring-or-lack-thereof-d16a910fa32a) for further discussion on the test case and observed results.
