package io.github.cuisse.json;

import java.util.List;
import java.util.function.BiPredicate;

/**
 * <p> A 'simple' class that can be used validate the structure a json value. For example: </p>
 *
 * <pre>
 * {@code
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
 *     Validator validator = new Validator(JsonType.OBJECT, List.of(
 *          new Validator("entities", JsonType.OBJECT, true, null, List.of(
 *              new Validator("spider", JsonType.OBJECT, true, null, List.of(
 *                  new Validator("name", JsonType.STRING, true, (value, current) -> value.string().equals("Lucas"), null),
 *                  new Validator("happiness", JsonType.NUMBER, true, (value, current) -> value.decimal() > 50d, null)
 *              ))
 *          ))
 *     ));
 *
 *     validator.validate(Json.parse(input));
 * }
 * </pre>
 *
 * @author Brayan Roman
 *
 * @param name       The name of the field this validator is targeting, you can leave empty (or null) for the root validator.
 * @param type       The type this validator is targeting.
 * @param required   Tell whether or no the field is required.
 * @param tester     An optional predicate to test extra attributes of the field, for example its value.
 * @param validators A list of validators to continue the sequence.
 *
 */
public record Validator(String name, JsonType type, boolean required, BiPredicate<JsonValue, Validator> tester, List<Validator> validators) {
    public static final String NO_NAME = null;
    public static final BiPredicate<JsonValue, Validator> NO_TEST = null;
    public static final List<Validator> NO_SEQUENCE = null;

    public Validator(JsonType type, List<Validator> validators) {
        this(NO_NAME, type, true, NO_TEST, validators);
    }

    /**
     * Validate the structure of a JSON value.
     *
     * @param value                The JSON value to validate.
     * @throws ValidationException If anything in the validation has failed.
     */
    public void validate(JsonValue value) throws ValidationException {
        if (value == null) {
            if (required) {
                throw new ValidationException("Required value '" + name + "' is missing.");
            }
        }
        if (value != null) {
            if (value.is(type)) {
                if (tester != null) {
                    if (false == tester.test(value, this)) {
                        throw new ValidationException("Value '" + name + "' of type '" + value.type() + "' didn't pass the test.");
                    }
                }
                if (type == JsonType.OBJECT && validators != null) {
                    for (Validator validator : validators) {
                        validator.validate(
                                value.object().get(validator.name)
                        );
                    }
                }
            } else {
                throw new ValidationException("Json type mismatch, expecting " + type + " but got " + value.type());
            }
        }
    }

}
