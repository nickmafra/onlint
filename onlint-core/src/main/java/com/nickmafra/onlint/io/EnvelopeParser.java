package com.nickmafra.onlint.io;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EnvelopeParser {

    public static final int ENVELOPE_MAX_SIZE = 2048;

    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTypingAsProperty(null, ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        return objectMapper;
    }
}
