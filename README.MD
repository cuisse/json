### Json Java

Simple JSON parser written in Java with 0 dependencies.

# Usage

Parsing 
```java
String input = "{ \"key\": \"value\", \"todos\": [\"Sleep\", \"Sleep\"], \"magic-number\": 26, \"PI\": 3.141592653589793E+00 }";

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