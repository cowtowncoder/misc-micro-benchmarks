package com.cowtowncoder.microb.jackson.model;

import java.util.List;

public class HuggingFaceCohereScidocsQueries
{
    public List<Data> data;

    public static class Data {
        public String _id;
        public String text;
        public float[] emb;
    }
}
