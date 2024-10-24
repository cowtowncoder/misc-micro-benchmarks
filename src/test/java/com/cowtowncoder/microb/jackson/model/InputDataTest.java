package com.cowtowncoder.microb.jackson.model;

import org.junit.jupiter.api.Test;

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