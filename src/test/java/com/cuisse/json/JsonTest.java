package com.cuisse.json;

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
    void shouldCorrectlyParseABooleanValue() {
        String input = """
            {
                "bool_true": true 
            }
        """;

        assertTrue(
                assertDoesNotThrow(() -> Json.parse(input).object().get("bool_true").bool())
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
                assertDoesNotThrow(() -> Json.parse(input).object().get("population_2023").integral())
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
                assertDoesNotThrow(() -> Json.parse(input).object().get("PI").decimal())
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
                assertDoesNotThrow(() -> Json.parse(input).object().get("money"))
        );
    }

}