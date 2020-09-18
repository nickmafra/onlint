package com.nickmafra.onlint.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nickmafra.onlint.model.Envelope;
import com.nickmafra.concurrent.PrintStreamScanner;
import com.nickmafra.onlint.exception.OnlintClientException;
import com.nickmafra.onlint.exception.OnlintRuntimeException;

import java.util.NoSuchElementException;

public class EnvelopePss {

    private final PrintStreamScanner pss;
    private final ObjectMapper objectMapper;

    public EnvelopePss(PrintStreamScanner pss) {
        this.pss = pss;
        objectMapper = EnvelopeParser.createObjectMapper();
    }

    public <T> T readType(Class<T> clazz) {
        String json;
        try {
            json = pss.nextLine();
        } catch (NoSuchElementException e) {
            throw new OnlintClientException("Outra ponta encerrou a conexão.");
        }
        Envelope envelope;
        try {
            envelope = objectMapper.readValue(json, Envelope.class);
        } catch (JsonProcessingException e) {
            throw new OnlintClientException("Não foi possível ler/converter envelope recebido.", e);
        }
        if (envelope == null || envelope.getVal() == null) {
            throw new OnlintClientException("Recebido envelope vazio.");
        } else if (clazz.isAssignableFrom(envelope.getVal().getClass())) {
            return clazz.cast(envelope.getVal());
        } else {
            throw new OnlintClientException("Recebido tipo inesperado: " + envelope.getVal().getClass().getSimpleName() + ". Esperado: " + clazz.getSimpleName());
        }
    }

    public void writeType(Object obj) {
        String json;
        try {
            json = objectMapper.writeValueAsString(new Envelope(obj));
        } catch (JsonProcessingException e) {
            throw new OnlintRuntimeException("Erro ao converter requisição para JSON.", e);
        }
        pss.println(json);
    }
}
