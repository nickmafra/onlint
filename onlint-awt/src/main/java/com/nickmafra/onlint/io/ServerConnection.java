package com.nickmafra.onlint.io;

import com.nickmafra.concurrent.PrintStreamScanner;
import com.nickmafra.onlint.exception.OnlintRuntimeException;
import com.nickmafra.onlint.io.EnvelopePss;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;

@Slf4j
public class ServerConnection<I, R1, R2> {

    private final Class<R2> responseClass;
    private final String host;
    private final int port;

    private Socket socket;
    private EnvelopePss envelopePss;

    public ServerConnection(Class<R2> responseClass, String host, int port) {

        this.responseClass = responseClass;
        this.host = host;
        this.port = port;
    }

    public void start(I clientInfo) {
        log.info("Estabelecendo conexão com servidor...");
        try {
            socket = new Socket(host, port);
            envelopePss = new EnvelopePss(new PrintStreamScanner(socket));
        } catch (IOException e) {
            closeConnection();
            throw new OnlintRuntimeException("Erro ao estabelecer conexão com servidor.", e);
        }
        sendInfo(clientInfo);
    }

    public boolean started() {
        return socket != null;
    }

    public R2 execute(R1 request) {
        if (!started()) {
            throw new IllegalStateException("Não iniciado.");
        }
        writeType(request);
        return readResponse();
    }

    private void closeConnection() {
        try {
            if (socket != null) {
                socket.close();
            }
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

    private void sendInfo(I info) {
        writeType(info);
        log.info("Conectado com servidor.");
    }

    private void writeType(Object request) {
        envelopePss.writeType(request);
    }

    private R2 readResponse() {
        return readType(responseClass);
    }
}
