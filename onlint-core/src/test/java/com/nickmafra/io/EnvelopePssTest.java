package com.nickmafra.io;

import com.nickmafra.concurrent.Pipe;
import com.nickmafra.concurrent.PrintStreamScanner;
import com.nickmafra.onlint.io.EnvelopePss;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class EnvelopePssTest {

    private EnvelopePss createEnvelopePss() {
        try {
            return new EnvelopePss(new PrintStreamScanner(new Pipe()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void teste() {
        EnvelopePss envelopePss = createEnvelopePss();

        Object obj = EnvelopeMock.mock2();
        envelopePss.writeType(obj);
        Object objLido = envelopePss.readType(Object.class);

        assertThat(objLido).isEqualTo(obj);
    }
}