package com.nickmafra.io;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickmafra.onlint.io.EnvelopeParser;
import com.nickmafra.onlint.model.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class EnvelopeParserTest {

    @Test
    void createObjectMapper() throws IOException {
        ObjectMapper objectMapper = EnvelopeParser.createObjectMapper();

        Envelope envelope = new Envelope(EnvelopeMock.mock1());

        byte[] bytes = objectMapper.writeValueAsBytes(envelope);
        log.info("JSON: {}", new String(bytes, StandardCharsets.UTF_8));
        Envelope envelopeLido = objectMapper.readValue(bytes, Envelope.class);

        assertThat(envelopeLido).isEqualTo(envelope);
    }
}