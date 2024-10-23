package com.cowtowncoder.microb.jackson.model;

import java.io.IOException;
import java.util.EnumMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.jr.ob.JSON;

public class InputData
{
    private final Class<?> _modelClass;

    private final byte[] _serialized;
    private final Object _deserialized;

    InputData(Class<?> modelClass,
            byte[] serialized, Object deserialized) {
        _modelClass = modelClass;
        _serialized = serialized;
        _deserialized = deserialized;
    }

    public Class<?> modelClass() { return _modelClass; }
    public byte[] serialized() { return _serialized; }
    @SuppressWarnings("unchecked")
    public <T> T deserialized() {
        return (T) _deserialized;
    }

    public static InputData get(InputJson key) {
        return Loader.instance().get(key);
    }

    private final static class Loader {
        private final EnumMap<InputJson, InputData> _dataByKey;

        private final static Loader INSTANCE = new Loader();

        public static Loader instance() {
            return INSTANCE;
        }

        private Loader() {
            EnumMap<InputJson, InputData> data = new EnumMap<>(InputJson.class);
            final ObjectMapper mapper = new JsonMapper();
            for (InputJson json : InputJson.values()) {
                try {
                    data.put(json, load(mapper, json));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to load `InputJson."+json.name()+"`: "+e.getMessage(), e);
                }
            }
            _dataByKey = data;
        }

        private static InputData load(ObjectMapper mapper, InputJson json) throws IOException
        {
            // First: load the raw input
            byte[] rawInput = json.load();

            // But do not use as-is; serialize to remove white space
            // (uses Jackson-jr to avoid initializing Jackson databind
            Object any = JSON.std.anyFrom(rawInput);
            byte[] serialized = JSON.std.asBytes(any);

            // And then deserialize into model.
            
            // Alas. Must resort to using Jackson databind after all
            // (due to Jackson-jr not supporting deser of `float[]`...
            Object deserialized = mapper.readValue(serialized, json.modelClass());
            
            return new InputData(json.modelClass(),
                    serialized, deserialized);
        }
        
        public InputData get(InputJson key) {
            return _dataByKey.get(key);
        }
    }
    
}
