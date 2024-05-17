package io.github.cuisse.json;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * <p> A 'simple' class that can be used validate the structure a json value. For example: </p>
 *
 * <pre>
 * {@code
 * 
 *     String input = """
 *          {
 *              "entities": {
 *                  "spider": {
 *                      "name": "Lucas",
 *                      "happiness": 100.0
 *                  }
 *              }
 *          }
 *     """;
 *
 *     Validator validator = Validator.of(JsonType.OBJECT).fields(
 *                    Validator.of("entities", JsonType.OBJECT).fields(
 *                          Validator.of("spider", JsonType.OBJECT).fields(
 *                                  Validator.of("name", JsonType.STRING)
 *                                            .condition((value, current) -> value.string().equals("Lucas")),
 *                                   Validator.of("happiness", JsonType.NUMBER)
 *                                           .condition((value, current) -> value.decimal() > 50d))));
 *
 *     validator.validate(Json.parse(input));
 * 
 * }
 * </pre>
 *
 * @author Brayan Roman
 *
 * @param name       The name of the field this validator is targeting, you can leave empty (or null) for the root validator.
 * @param type       The type this validator is targeting.
 * @param required   Tell whether or no the field is required.
 * @param condition     An optional predicate to test extra attributes of the field, for example its value.
 * @param fields A list of validators to continue the sequence.
 *
 */
public record Validator(String name, JsonType type, boolean required, BiPredicate<JsonValue, Validator> condition, List<Validator> fields) {

    /**
     * Create a new validator for a JSON type.
     * 
     * @param type The type of JSON value to validate.
     * @return     A new validator for the given type.
     */
    public static Validator of(JsonType type) {
        return new Validator(null, type, true, null, null);
    }

    /**
     * Create a new validator for a JSON type.
     * 
     * @param name The name of the field to validate.
     * @param type The type of JSON value to validate.
     * @return     A new validator for the given type.
     */
    public static Validator of(String name, JsonType type) {
        return new Validator(name, type, true, null, null);
    }

    /**
     * Make the field optional.
     * 
     * @return A new validator with the same attributes but with the required flag set to false.
     */
    public Validator optional() {
        return new Validator(name, type, false, condition, fields);
    }

    /**
     * Add a condition to the field.
     * 
     * @param condition The condition to test.
     * @return          A new validator with the same attributes but with the condition set.
     */
    public Validator condition(BiPredicate<JsonValue, Validator> condition) {
        return new Validator(name, type, required, condition, fields);
    }

    /**
     * Add a list of validators to the field.
     * 
     * @param validators The list of validators to add.
     * @return           A new validator with the same attributes but with the fields set.
     */
    public Validator fields(Validator... validators) {
        return fields(List.of(validators));
    }

    /**
     * Add a list of validators to the field.
     * 
     * @param validators The list of validators to add.
     * @return           A new validator with the same attributes but with the fields set.
     */
    public Validator fields(List<Validator> validators) {
        return new Validator(name, type, required, condition, validators);
    }

    /**
     * Validate the structure of a JSON value.
     *
     * @param value                The JSON value to validate.
     * @throws JsonValidationException If anything in the validation has failed.
     */
    public void validate(JsonValue value) throws JsonValidationException {
        if (value == null) {
            if (required) {
                throw new JsonValidationException("Required field '" + name + "' is missing.");
            }
        }
        if (value != null) {
            if (value.is(type)) {
                if (condition != null) {
                    if (false == condition.test(value, this)) {
                        throw new JsonValidationException("Field '" + name + "' of type '" + value.type() + "' failed validation.");
                    }
                }
                if (fields != null) {
                    if (type == JsonType.OBJECT) {
                        for (Validator field : fields) {
                            field.validate(
                                    value.object().get(field.name)
                            );
                        }
                    } else {
                        if (type == JsonType.ARRAY) {
                            JsonArray array = value.array();
                            if (array.size() != fields.size()) {
                                throw new JsonValidationException("Array size mismatch, expecting " + fields.size() + " but got " + array.size() + " for field '" + name + "'.");
                            } else {
                                for (int i = 0; i < array.size(); i++) {
                                    fields.get(i).validate(array.get(i));
                                }
                            }
                        }
                    }
                }
            } else {
                throw new JsonValidationException("Json type mismatch, expecting " + type + " but got " + value.type());
            }
        }
    }

}
