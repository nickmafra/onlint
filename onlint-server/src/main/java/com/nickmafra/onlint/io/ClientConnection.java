package com.nickmafra.onlint.io;

import com.nickmafra.concurrent.PrintStreamScanner;
import com.nickmafra.onlint.exception.OnlintRuntimeException;
import com.nickmafra.onlint.io.EnvelopePss;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.function.BiFunction;

@Slf4j
public class ClientConnection<I, R1, R2> extends Thread {

    private final Class<I> infoClass;
    private final Class<R1> requestClass;
    private final BiFunction<I, R1, R2> processor;
    private final Socket socket;

    private EnvelopePss envelopePss;
    private I info;

    public ClientConnection(Class<I> infoClass, Class<R1> requestClass,
                            BiFunction<I, R1, R2> processor, Socket socket) {

        super("ClientConnection");

        Objects.requireNonNull(socket);

        this.infoClass = infoClass;
        this.requestClass = requestClass;
        this.processor = processor;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            log.info("Estabelecendo conexão com cliente...");
            try {
                envelopePss = new EnvelopePss(new PrintStreamScanner(socket));
            } catch (IOException e) {
                throw new OnlintRuntimeException("Erro ao estabelecer conexão com cliente.");
            }
            readInfo();
            while (!isInterrupted()) {
                execute();
            }
        } finally {
            closeConnection();
        }
    }

    public void execute() {
        R1 request = readRequest();
        R2 response = processor.apply(info, request);
        writeResponse(response);
    }

    private void closeConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isClosed() {
        return socket.isClosed();
    }

    private <T> T readType(Class<T> clazz) {
        return envelopePss.readType(clazz);
    }

    private void readInfo() {
        info = readType(infoClass);
        log.info("Cliente conectado: " + info.toString());
    }

    private R1 readRequest() {
        return readType(requestClass);
    }

    private void writeResponse(R2 response) {
        envelopePss.writeType(response);
    }
}
