package com.cowtowncoder.microb;

import org.junit.jupiter.api.Test;

import com.cowtowncoder.microb.jackson.model.InputData;
import com.cowtowncoder.microb.jackson.model.InputJson;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InputDataTest
{
    @Test
    void testRawInput() {
        for (InputJson json : InputJson.values()) {
            // Loading should tease out problems
            InputData data = InputData.get(json);
            // but let's verify
            assertNotNull(data.modelClass());
            assertNotNull(data.deserialized());
            assertNotNull(data.serialized());
            assertTrue(data.modelClass().isAssignableFrom(data.deserialized().getClass()));
        }
    }
}