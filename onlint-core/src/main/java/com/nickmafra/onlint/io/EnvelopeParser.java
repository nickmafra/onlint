package com.nickmafra.onlint.io;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnvelopeParser {

    private EnvelopeParser() {}

    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        return objectMapper;
    }
}
