package io.github.cuisse.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonTest {

    @Test
    void shouldCorrectlyParseAJsonObjectString() {
        String input = """
            {
                "key1": "value 1"
            }
        """;

        JsonValue value = Json.parse(input);

        assertAll(
                () -> assertTrue(value.is(JsonType.OBJECT)),
                () -> assertEquals(1, value.object().size()),
                () -> assertNotNull(value.object().get("key1")),
                () -> assertTrue(value.object().get("key1").is(JsonType.STRING)),
                () -> assertEquals("value 1", value.object().get("key1").string())
        );
    }

    @Test
    void shouldNotAllowDuplicatedKeysInObject() {
        String input = """
            {
                "one": 1, 
                "two": 2,
                "one": 1
            }
        """;

        assertThrows(ParsingException.class, () -> {
            Json.parse(input);
        }, "Duplicated key one in object.");
    }

    @Test
    void shouldThrowParsingExceptionBecauseOfMissingBracket() {
        String input = """
            {
                "mappings": {
                    "v1": {
                        "values": {
                            "one": 1,
                            "two": 2
                   }
                }
            }
        """;

        ParsingException error = assertThrows(ParsingException.class, () -> {
            Json.parse(input);
        });

        assertTrue(error.getMessage().contains("Expecting OBJECT_CLOSE but got "));
    }

    @Test
    void shouldCorrectlyParseAJsonArrayString() {
        String input = """
            [
                {
                    "position": {
                        "x": 45.687,
                        "y": 200.0,
                        "z": 8.95
                    }
                }
            ]
        """;

        JsonValue value = Json.parse(input);

        assertAll(
                () -> assertTrue(value.is(JsonType.ARRAY)),
                () -> assertEquals(1, value.array().size()),
                () -> assertNotNull(value.array().get(0)),
                () -> assertTrue(value.array().get(0).is(JsonType.OBJECT)),
                () -> assertEquals(1, value.array().get(0).object().size())
        );
    }

    @Test
    void shouldCorrectlyParseAIntegralValue() {
        String input = """
            {
                "population_2023": 8045311447 
            }
        """;

        assertEquals(8_045_311_447L,
                Assertions.assertDoesNotThrow(() -> Json.parse(input).object().get("population_2023").integral64())
        );
    }

    @Test
    void shouldCorrectlyParseADecimalValue() {
        String input = """
            {
                "PI": 3.141592653589793 
            }
        """;

        assertEquals(Math.PI,
                Assertions.assertDoesNotThrow(() -> Json.parse(input).object().get("PI").decimal64())
        );
    }

    @Test
    void shouldCorrectlyParseADecimalAndReturnItsStaticValueIfBetween0and9() {
        String input = """
            {
                "number0": 0,
                "number1": 1,
                "number2": 2,
                "number3": 3,
                "number4": 4,
                "number5": 5,
                "number6": 6,
                "number7": 7,
                "number8": 8,
                "number9": 9
            }
        """;

        JsonObject object = Json.parse(input).object();

        assertAll(
                () -> assertEquals(JsonIntegral.INTEGRAL_0, object.get("number0")),
                () -> assertEquals(JsonIntegral.INTEGRAL_1, object.get("number1")),
                () -> assertEquals(JsonIntegral.INTEGRAL_2, object.get("number2")),
                () -> assertEquals(JsonIntegral.INTEGRAL_3, object.get("number3")),
                () -> assertEquals(JsonIntegral.INTEGRAL_4, object.get("number4")),
                () -> assertEquals(JsonIntegral.INTEGRAL_5, object.get("number5")),
                () -> assertEquals(JsonIntegral.INTEGRAL_6, object.get("number6")),
                () -> assertEquals(JsonIntegral.INTEGRAL_7, object.get("number7")),
                () -> assertEquals(JsonIntegral.INTEGRAL_8, object.get("number8")),
                () -> assertEquals(JsonIntegral.INTEGRAL_9, object.get("number9"))
        );
    }

    @Test
    void shouldCorrectlyParseABooleanValue() {
        String input = """
            {
                "!true" : false,
                "!false": true 
            }
        """;

        JsonObject object = Json.parse(input).object();

        assertAll(
                () -> assertEquals(JsonBoolean.FALSE, object.get("!true") ),
                () -> assertEquals(JsonBoolean.TRUE , object.get("!false"))
        );
    }

    @Test
    void shouldCorrectlyParseANullValue() {
        String input = """
            {
                "money": null 
            }
        """;

        assertInstanceOf(JsonNull.class,
                Assertions.assertDoesNotThrow(() -> Json.parse(input).object().get("money"))
        );
    }

    @Test
    void shouldCorrectlyParseSingleLineComment() {
        String input = """
            {
                // single line comment
                "money": null 
                ,
                // another single line comment
                "happiness": "yes"
            }
        """;

        assertInstanceOf(JsonNull.class,
                Assertions.assertDoesNotThrow(() -> Json.parse(input, JsonOptions.BASIC).object().get("money"))
        );
        assertInstanceOf(JsonString.class,
                Assertions.assertDoesNotThrow(() -> Json.parse(input, JsonOptions.BASIC).object().get("happiness"))
        );
    }

    @Test
    void shouldCorrectlyParseMultiLineComment() {
        String input = """
            {
                /*
                    Multiline Comment 
                    "money": null 
                */
                "happiness": "yes"
                /*
                    Success?
                */
            }
        """;
        assertInstanceOf(JsonString.class,
                Assertions.assertDoesNotThrow(() -> Json.parse(input, JsonOptions.BASIC).object().get("happiness"))
        );
    }

    @Test
    void shouldCorrectlyPassValidator() {
        String input = """
                     {
                         "entities": {
                             "spider": {
                                 "name": "Lucas",
                                 "happiness": 100.0
                             }
                         }
                     }
                """;

        Validator validator = Validator.of(JsonType.OBJECT).fields(
                Validator.of("entities", JsonType.OBJECT).fields(
                        Validator.of("spider", JsonType.OBJECT).fields(
                                Validator.of("name", JsonType.STRING)
                                        .condition((value, current) -> value.string().equals("Lucas")),
                                Validator.of("happiness", JsonType.NUMBER)
                                        .condition((value, current) -> value.decimal64() > 50d))));

        Assertions.assertDoesNotThrow(() -> validator.validate(Json.parse(input)));
 
    }

    @Test
    void shouldNotPassValidator() {
        String input = """
                     {
                         "entities": {
                             "spider": {
                                 "name": "Mark",
                                 "happiness": 5.0
                             }
                         }
                     }
                """;

        Validator validator = Validator.of(JsonType.OBJECT).fields( // root object
                Validator.of("entities", JsonType.OBJECT).fields(
                        Validator.of("spider", JsonType.OBJECT).fields(
                                Validator.of("name", JsonType.STRING)
                                        .condition((value, current) -> value.string().equals("Lucas")),
                                Validator.of("happiness", JsonType.NUMBER)
                                        .condition((value, current) -> value.decimal64() > 50d))));

        Assertions.assertThrows(JsonValidationException.class, () -> validator.validate(Json.parse(input)));
    }

    @Test
    void shouldParseComplexNumbers() {
        String input = """
            {
                "complex1": 3.14e-2,
                "complex2": 3.14e2,
                "complex3": 3.14e+2,
            }
        """;

        JsonObject object = Json.parse(input).object();

        assertAll(
                () -> assertEquals(3.14e-2, object.get("complex1").decimal64()),
                () -> assertEquals(3.14e2, object.get("complex2").decimal64()),
                () -> assertEquals(3.14e+2, object.get("complex3").decimal64())
        );
    }

}