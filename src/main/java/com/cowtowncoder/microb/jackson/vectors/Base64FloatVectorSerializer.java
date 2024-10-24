package com.cowtowncoder.microb.jackson.vectors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

public class Base64FloatVectorSerializer extends StdScalarSerializer<float[]>
{
    private static final long serialVersionUID = 1L;

    protected Base64FloatVectorSerializer() {
        super(float[].class);
    }

    @Override
    public void serialize(float[] value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        // First: "pack" the floats into bytes
        final int vectorLen = value.length;
        final byte[] b = new byte[vectorLen << 2];
        for (int i = 0, out = 0; i < vectorLen; i++) {
            final int floatBits = Float.floatToIntBits(value[i]);
            b[out++] = (byte) (floatBits >> 24);
            b[out++] = (byte) (floatBits >> 16);
            b[out++] = (byte) (floatBits >> 8);
            b[out++] = (byte) (floatBits);
        }

        // Second: write packed bytes (for JSON, Base64 encoded)
        gen.writeBinary(b);
    }
}
