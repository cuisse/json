package io.github.cuisse.json;

/**
 * @author Brayan Roman
 */
public record SimpleJsonPrinter(boolean pretty) implements JsonPrinter {

    public static final SimpleJsonPrinter PRETTY   = new SimpleJsonPrinter(true);
    public static final SimpleJsonPrinter MINIFIED = new SimpleJsonPrinter(false);

    protected static final String OBJECT_EMPTY = "{}";
    protected static final String ARRAY_EMPTY  = "[]";
    protected static final String BLANK        = "";

    /**
     * Returns a pretty-printed JSON string.
     * 
     * @param value The JSON value.
     * @return      The JSON string.
     */
    public static final String pretty(JsonValue value) {
        return PRETTY.print(value);
    }

    /**
     * Returns a minified JSON string.
     * 
     * @param value The JSON value.
     * @return      The JSON string.
     */
    public static final String minified(JsonValue value) {
        return MINIFIED.print(value);
    }
    
    @Override
    public String print(JsonValue value, int indent) {
        if (value == null) {
            return "null";
        }
        return switch (value) {
            case JsonBoolean  json -> String.valueOf(json.value());
            case JsonString   json -> '"' + json.value() + '"';
            case JsonIntegral json -> Long.toString(json.value());
            case JsonDecimal  json -> Double.toString(json.value());
            case JsonObject   json -> printObject(json, indent);
            case JsonArray    json -> printArray(json, indent);
            case JsonNull     json -> "null";
            default                -> throw new UnsupportedOperationException("Unsupported JSON value: " + value);
        };
    }

    protected String printObject(JsonObject object, int indent) {
        if (object.isEmpty()) {
            return OBJECT_EMPTY;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('{');
            object.forEach((key, val) -> {
                if (pretty) {
                    sb.append('\n');
                    sb.repeat(' ', indent + 1);
                }
                sb.append('"').append(key).append('"').append(':');
                if (pretty) {
                    sb.append(' ');
                    sb.append(print(val, indent + 2));
                } else {
                    sb.append(print(val, indent));
                }
                sb.append(',');
            });
            sb.replace(sb.length() - 1, sb.length(), BLANK);
            if (pretty) {
                sb.append('\n').repeat(' ', indent);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    protected String printArray(JsonArray array, int indent) {
        if (array.isEmpty()) {
            return ARRAY_EMPTY;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            array.forEach(val -> {
                if (pretty) {
                    sb.append('\n');
                    sb.repeat(' ', indent + 1);
                }
                if (pretty) {
                    sb.append(print(val, indent + 2));
                } else {
                    sb.append(print(val, indent));
                }
                sb.append(',');
            });
            sb.replace(sb.length() - 1, sb.length(), "");
            if (pretty) {
                sb.append('\n').repeat(' ', indent);
            }
            sb.append(']');
            return sb.toString();
        }
    }

}
