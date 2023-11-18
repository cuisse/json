### Json Java

Simple JSON parser written in Java with 0 dependencies.

# Basic Usage

Parsing 
```java
import com.cuisse.json.Json;
import com.cuisse.json.JsonObject;

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

JsonObject object = Json.parse(input).object();
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
import com.cuisse.json.JsonArray;
import com.cuisse.json.JsonValue;

JsonArray todos = object.get("todos").array();
for (JsonValue todo : todos) {
    System.out.println(todo.string()); // Sleep
}

// Accessing other values 
double pi  = object.get("PI").decimal(); // 3.141592653589793
String key = object.get("key").string(); // "value"
long   mn  = object.get("magic-number").integral(); // 26
```

Parsing arrays

```java
import com.cuisse.json.Json;
import com.cuisse.json.JsonArray;

String input = "[5, 5.5, {}, \"Hello world\", false, true, null, []]";

JsonArray array = Json.parse(input).array();

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

# Parsing Custom Values
First we need to create a our data type
```java
record Item(String name, int quantity, double price) { }
```

Then we need a JsonConverter

```java
import com.cuisse.json.JsonConverter;
import com.cuisse.json.JsonDecimal;
import com.cuisse.json.JsonIntegral;
import com.cuisse.json.JsonObject;
import com.cuisse.json.JsonString;
import com.cuisse.json.JsonType;
import com.cuisse.json.JsonValue;

class ItemJsonConverter implements JsonConverter<Item> {

    @Override
    public JsonType type() {
        return JsonType.OBJECT;
    }

    @Override
    public Item convert(JsonObject value) {
        return new Item(
                value.object().get("name").string(),
                value.object().get("quantity").integral(),
                value.object().get("price").decimal()
        );
    }

    @Override
    public JsonValue convert(Item value) {
        JsonObject object = new JsonObject();
        object.put("name", new JsonString(value.name()));
        object.put("quantity", new JsonIntegral(value.quantity()));
        object.put("price", new JsonDecimal(value.price()));
        return object;
    }
    
}
```

Remember to register it, so the library can use it.

```java
import com.cuisse.json.JsonConverters;

JsonConverters.register(Item.class, new ItemJsonConverter());
```

Done. Now we are ready to start working with it.

```java
import com.cuisse.json.Json;

String input = """
    {
        "name": "Chocolate cake",
        "quantity": 2,
        "price": 16.85
    }
""";

Item   item = Json.parse(input, Item.class);
String json = Json.json(item, true); // convert it back to a pretty printed json string
```

# Validators
We also have the option to validate our data before processing it. Let's take the previous converter as an example.

```java
import com.cuisse.json.JsonConverter;
import com.cuisse.json.JsonDecimal;
import com.cuisse.json.JsonIntegral;
import com.cuisse.json.JsonObject;
import com.cuisse.json.JsonString;
import com.cuisse.json.JsonType;
import com.cuisse.json.JsonValue;
import com.cuisse.json.Validator;

class ItemJsonConverter implements JsonConverter<Item> {

    // I recommend to reuse validators 
    private static final Validator validator;

    static {
        validator = new Validator(JsonType.OBJECT, List.of( // ROOT
                new Validator("name"    , JsonType.STRING, true, this::validateName    , Validator.NO_SEQUENCE),
                new Validator("quantity", JsonType.NUMBER, true, this::validateQuantity, Validator.NO_SEQUENCE),
                new Validator("price"   , JsonType.NUMBER, true, this::validatePrice   , Validator.NO_SEQUENCE)
        ));
    }

    @Override

    public JsonType type() {
        return JsonType.OBJECT;
    }

    @Override
    public Item convert(JsonObject value) {
        validator.validate(value); // Apply the validation before creating our object 
        return new Item(
                value.object().get("name").string(),
                value.object().get("quantity").integral(),
                value.object().get("price").decimal()
        );
    }

    @Override
    public JsonValue convert(Item value) {
        JsonObject object = new JsonObject();
        object.put("name", new JsonString(value.name()));
        object.put("quantity", new JsonIntegral(value.quantity()));
        object.put("price", new JsonDecimal(value.price()));
        // We can also apply the validation after creating the json object
        validator.validate(object);
        return object;
    }

    private boolean validateName(JsonValue value, Validator current) {
        return false == value.string().isBlank();
    }

    private boolean validateQuantity(JsonValue value, Validator current) {
        return value.is(JsonIntegral.class) &&
               value.integral() > 0;
    }

    private boolean validatePrice(JsonValue value, Validator current) {
        return value.is(JsonDecimal.class) &&
               value.decimal() > 0.00d;
    }

}
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
# Run complete. Total time: 00:35:59

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

Benchmark          Mode  Cnt  Score   Error  Units
Benchmark.gson     avgt  100  0.096 ± 0.002  ms/op
Benchmark.jackson  avgt  100  0.106 ± 0.006  ms/op
Benchmark.json     avgt  100  0.086 ± 0.001  ms/op
```

<details>
    <summary>Detailed Logs</summary>

```text

Result "com.cuisse.json.Benchmark.gson":
  0.096 ±(99.9%) 0.002 ms/op [Average]
  (min, avg, max) = (0.093, 0.096, 0.134), stdev = 0.005
  CI (99.9%): [0.094, 0.097] (assumes normal distribution)

Result "com.cuisse.json.Benchmark.jackson":
  0.106 ±(99.9%) 0.006 ms/op [Average]
  (min, avg, max) = (0.066, 0.106, 0.128), stdev = 0.018
  CI (99.9%): [0.100, 0.112] (assumes normal distribution)
  
Result "com.cuisse.json.Benchmark.json":
  0.086 ±(99.9%) 0.001 ms/op [Average]
  (min, avg, max) = (0.080, 0.086, 0.097), stdev = 0.004
  CI (99.9%): [0.085, 0.087] (assumes normal distribution)
  
```

</details>

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