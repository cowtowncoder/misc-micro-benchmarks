package com.cowtowncoder.microb.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CustomJsonNodeReader
{
    private final JsonNodeFactory _nodes;
    private final JsonParser _parser;

    private final boolean _cfgFloatsAsBigDecimal;
    
    public CustomJsonNodeReader(ObjectMapper m, JsonParser p) {
        _nodes = m.getNodeFactory();
        _parser = p;
        _cfgFloatsAsBigDecimal = m.isEnabled(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
    }

    public JsonNode readTree() {
        try {
            _parser.nextToken();
            return _readFromRoot();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JsonNode _readFromRoot() throws IOException
    {
        switch (_parser.currentTokenId()) {
        case JsonTokenId.ID_START_OBJECT:
            return _readObject(null);
        case JsonTokenId.ID_END_OBJECT:
            // Is this even allowed?
            return _nodes.objectNode();
        case JsonTokenId.ID_START_ARRAY:
            return _readArray(null);
        }
        return _readAny(null);
    }

    private JsonNode _readObject(String parentProp) throws IOException
    {
        ObjectNode ob = _nodes.objectNode();
        String propName;

        while ((propName = _parser.nextFieldName()) != null) {
            _parser.nextToken();
            ob.set(propName, _readAny(propName));
        }

        if (_parser.currentTokenId() != JsonTokenId.ID_END_OBJECT) {
            throw _readError("Internal state error: current token should be END_OBJECT, was: "
                    +_parser.currentToken());
        }
        return ob;
    }

    private JsonNode _readArray(String parentProp) throws IOException
    {
        ArrayNode arr = _nodes.arrayNode();
        while (_parser.nextToken() != JsonToken.END_ARRAY) {
            arr.add(_readAny(null));
        }
        return arr;
    }

    private JsonNode _readAny(String parentProp) throws IOException
    {
        switch (_parser.currentTokenId()) {
        case JsonTokenId.ID_START_OBJECT:
            return _readObject(parentProp);
        case JsonTokenId.ID_START_ARRAY:
            return _readArray(parentProp);
        case JsonTokenId.ID_STRING:
            return _nodes.textNode(_parser.getText());
        case JsonTokenId.ID_NUMBER_INT:
            JsonParser.NumberType nt = _parser.getNumberType();
            if (nt == JsonParser.NumberType.INT) {
                return _nodes.numberNode(_parser.getIntValue());
            }
            if (nt == JsonParser.NumberType.LONG) {
                return _nodes.numberNode(_parser.getLongValue());
            }
            return _nodes.numberNode(_parser.getBigIntegerValue());
        case JsonTokenId.ID_NUMBER_FLOAT:
            if (_cfgFloatsAsBigDecimal) {
                return _nodes.numberNode(_parser.getDecimalValue());
            }
            return _nodes.numberNode(_parser.getDoubleValue());
        case JsonTokenId.ID_TRUE:
            return _nodes.booleanNode(true);
        case JsonTokenId.ID_FALSE:
            return _nodes.booleanNode(false);
        case JsonTokenId.ID_NULL:
            return _nodes.nullNode();
        default:
        }
        throw _readError("Internal state error: current token type unsupported: "
                +_parser.currentToken());
    }

    private StreamReadException _readError(String msg) {
        return new JsonParseException(_parser, msg);
    }
}