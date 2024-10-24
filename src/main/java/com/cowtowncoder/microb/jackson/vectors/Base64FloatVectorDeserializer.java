package com.cowtowncoder.microb.jackson.vectors;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class Base64FloatVectorDeserializer extends StdScalarDeserializer<float[]>
{
    private static final long serialVersionUID = 1L;

    public Base64FloatVectorDeserializer() {
          super(float[].class);
    }

    @Override
    public float[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final JsonToken t = p.currentToken();

        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            Object emb = p.getEmbeddedObject();
            if (emb instanceof byte[]) {
                return unpack(ctxt, (byte[]) emb);
            } else if (emb instanceof float[]) {
                return (float[]) emb;
            }
        } else if (t == JsonToken.VALUE_STRING) {
            return unpack(ctxt, p.getBinaryValue());
        } else if (t == JsonToken.START_ARRAY) {
             // !!! TODO: regular Array
        }
        return (float[]) ctxt.handleUnexpectedToken(_valueClass, p);
    }

    private final float[] unpack(DeserializationContext ctxt, byte[] bytes) throws IOException {
        final int bytesLen = bytes.length;
        if ((bytesLen & 3) != 0) {
            return (float[]) ctxt.reportInputMismatch(_valueClass,
                    "Vector length (%d) not a multiple of 4 bytes", bytesLen);
        }
        final int vectorLen = bytesLen >> 2;
        final float[] floats = new float[vectorLen];
        for (int in = 0, out = 0; in < bytesLen; ) {
            int packed = (bytes[in++] << 24)
                    | ((bytes[in++] & 0xFF) << 16)
                    | ((bytes[in++] & 0xFF) << 8)
                    | (bytes[in++] & 0xFF);
            floats[out++] = Float.intBitsToFloat(packed);
        }
        return floats;
    }
}
