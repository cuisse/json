### Json Java

Simple JSON parser written in Java with 0 dependencies.

# Usage

Parsing 
```java
String input = """
    {
        "key": "value",
        "todos": [
            "Sleep",
            "Sleep"
        ],
        "magic-number": 26,
        "PI": 3.141592653589793E+00
    }
""";

Parser parser = new Parser(input);
JsonObject object = parser.parse().object();
```

You can pretty print the object using:

```java
System.out.println(object.print());
```

Outputs

```text
{
 "todos": [
   "Sleep",
   "Sleep"
  ],
 "magic-number": 26,
 "PI": 3.141592653589793,
 "key": "value"
}
```

To access its values use

```java
// Prints:
//  - Sleep
//  - Sleep
JsonArray todos = object.get("todos").array();
for (JsonValue todo : todos) {
    System.out.println(todo.string());
}

// Accessing other values 
double pi  = object.get("PI").decimal(); // 3.141592653589793
String key = object.get("key").string(); // "value"
long   mn  = object.get("magic-number").integral(); // 26
```

Parsing arrays

```java
String input = "[5, 5.5, {}, \"Hello world\", false, true, null, []]";

Parser parser = new Parser(input);
JsonArray array = parser.parse().array();

System.out.println(array.print());
```

Outputs

```text
[
 5,
 5.5,
 {},
 "Hello world",
 false,
 true,
 null,
 []
]
```

# Data Types
Currently this library supports the following data types, all of them implementing the 'com.cuisse.json.JsonValue' interface.

```text
JsonArray    -> List<JsonValue>
JsonBoolean  -> record(boolean value)
JsonDecimal  -> record(double value)
JsonIntegral -> record(long value)
JsonNull     -> record()
JsonObject   -> Map<String, JsonValue>
JsonString   -> record(String value)
```

# Performance

The performance it is pretty good, I have run a benchmark test against other popular
JSON parsers in Java and the results are descent. You can give a check bellow.

<details>
<summary>Results</summary>

```text
# Run complete. Total time: 01:12:14

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

NOTE: Current JVM experimentally supports Compiler Blackholes, and they are in use. Please exercise
extra caution when trusting the results, look into the generated code to check the benchmark still
works, and factor in a small probability of new VM bugs. Additionally, while comparisons between
different JVMs are already problematic, the performance difference caused by different Blackhole
modes can be very significant. Please make sure you use the consistent Blackhole mode for comparisons.

Benchmark            Mode  Cnt  Score    Error  Units
Benchmark.a_json     avgt  500  0.129 ±  0.001  ms/op
Benchmark.b_jackson  avgt  500  0.236 ±  0.382  ms/op
Benchmark.c_gson     avgt  500  0.102 ±  0.002  ms/op
```

Of course, you can always run your benchmark tests by yourself. The JSON data used in this benchmark is bellow.

<details>
<summary>JSON</summary>

```json
{
  "string":"Hello, World!",
  "number":42,
  "boolean":true,
  "nullValue":null,
  "array":[
    1,
    2,
    3,
    "four",
    5.5,
    true,
    null
  ],
  "object":{
    "nestedString":"Nested Value",
    "nestedNumber":-123.456,
    "nestedBoolean":false,
    "nestedNull":null,
    "nestedArray":[
      10,
      "eleven",
      12.5,
      false,
      null
    ],
    "nestedObject":{
      "deepString":"Deep Nested Value",
      "deepNumber":987.654,
      "deepBoolean":true,
      "deepNull":null
    }
  },
  "scientificNotation":6.022e23,
  "unicodeString":"日本語",
  "multilineString":"This is a multiline string.\nLine 2.\nLine 3.",
  "date":"2023-11-13T15:30:00Z",
  "escapedCharacters":"Special characters: \n\t\r\f\b\"\\",
  "emptyArray":[

  ],
  "emptyObject":{

  },
  "largeArray":[
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16,
    17,
    18,
    19,
    20
  ],
  "nestedArrays":[
    [
      1,
      2,
      3
    ],
    [
      "a",
      "b",
      "c"
    ],
    [
      {
        "nestedKey":"nestedValue"
      }
    ]
  ],
  "mixedTypesArray":[
    1,
    "two",
    true,
    null,
    {
      "key":"value"
    }
  ],
  "binaryData":"SGVsbG8sIFdvcmxkIQ==",
  "comments":"This JSON may contain non-standard comments for illustrative purposes.",
  "specialTypes":{
    "regexp":"/pattern/g",
    "undefinedValue":"undefined"
  },
  "other-kjson499494":{
    "string":"Hello, World!",
    "number":42,
    "boolean":true,
    "nullValue":null,
    "array":[
      1,
      2,
      3,
      "four",
      5.5,
      true,
      null
    ],
    "object":{
      "nestedString":"Nested Value",
      "nestedNumber":-123.456,
      "nestedBoolean":false,
      "nestedNull":null,
      "nestedArray":[
        10,
        "eleven",
        12.5,
        false,
        null
      ],
      "nestedObject":{
        "deepString":"Deep Nested Value",
        "deepNumber":987.654,
        "deepBoolean":true,
        "deepNull":null
      }
    },
    "scientificNotation":6.022e23,
    "unicodeString":"日本語",
    "multilineString":"This is a multiline string.\nLine 2.\nLine 3.",
    "date":"2023-11-13T15:30:00Z",
    "escapedCharacters":"Special characters: \n\t\r\f\b\"\\",
    "emptyArray":[

    ],
    "emptyObject":{

    },
    "largeArray":[
      1,
      2,
      3,
      4,
      5,
      6,
      7,
      8,
      9,
      10,
      11,
      12,
      13,
      14,
      15,
      16,
      17,
      18,
      19,
      20
    ],
    "nestedArrays":[
      [
        1,
        2,
        3
      ],
      [
        "a",
        "b",
        "c"
      ],
      [
        {
          "nestedKey":"nestedValue"
        }
      ]
    ],
    "mixedTypesArray":[
      1,
      "two",
      true,
      null,
      {
        "key":"value"
      }
    ],
    "binaryData":"SGVsbG8sIFdvcmxkIQ==",
    "comments":"This JSON may contain non-standard comments for illustrative purposes.",
    "specialTypes":{
      "regexp":"/pattern/g",
      "undefinedValue":"undefined"
    }
  }
}
```
</details>
</details>

# Contribution 

Help is always appreciate, if you feel something can be improved please let me know by a pull request.