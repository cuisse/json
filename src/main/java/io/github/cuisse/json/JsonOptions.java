package io.github.cuisse.json;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Brayan Roman
 */
public final class JsonOptions {

    public static final JsonOptions NONE  = new JsonOptions(List.of());
    public static final JsonOptions BASIC = new JsonOptions(List.of(
        new JsonOption("skipComments", true)
    ));

    private final Map<String, Object> options;

    public JsonOptions(Collection<JsonOption> options) {
        if (options == null) {
            throw new NullPointerException();
        } else {
            this.options = options.stream().collect(
                Collectors.toMap(option -> option.name(), option -> option.value())
            );
        }
    }

    /**
     * Get an specific option.
     * 
     * @param name     The name of the option to get.
     * @param type     The type of the option to get.
     * @param supplier The default value supplier if no option was found
     * @return         The desired option if found, otherwise the value provided by the supplier will be used.
     */
    public<T> T get(String name, Class<T> type, Supplier<T> supplier) {
        var value = options.get(name);
        if (value == null) {
            return supplier.get();
        } else {
            return type.cast(value);
        }
    }

}
