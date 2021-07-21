# Miscellaneous Micro-benchmarks by mr. Jackson

This is a repo containing a set of Java micro-benchmarks using
[jmh](https://openjdk.java.net/projects/code-tools/jmh/) framework.

## Results

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

