package fr.an.jsonnet4j;

import java.util.List;
import java.util.Map;

public class JsonnetJsonValue {
	public static enum Kind {
        ARRAY,
        BOOL,
        NULL_KIND,
        NUMBER,
        OBJECT,
        STRING,
    }

    Kind kind;
    String string;
    double number;  // Also used for boolean (0.0 and 1.0)
    List<JsonnetJsonValue> elements;
    Map<String, JsonnetJsonValue> fields;

//    JsonnetJsonValue() = default;
//    JsonnetJsonValue(JsonnetJsonValue&) = delete;
//    JsonnetJsonValue(JsonnetJsonValue&&) = default;

    public JsonnetJsonValue(Kind kind, String string, double number) {
    	this.kind = kind;
    	this.string = string;
    	this.number = number;
    }

}
