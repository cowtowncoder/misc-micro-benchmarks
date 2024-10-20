package com.cowtowncoder.microb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.cowtowncoder.microb.util.InputJson;

public class InputDataTest
{
    @Test
    void testRawInput() {
        for (InputJson json : InputJson.values()) {
            assertNotNull(json.bytes());
        }
    }
}