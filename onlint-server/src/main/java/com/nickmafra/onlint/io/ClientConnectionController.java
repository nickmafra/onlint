package com.nickmafra.onlint.io;

import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.exception.OnlintRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Slf4j
public class ClientConnectionController<R1, R2> extends Thread {

    public static final int COUNT_LIMIT = 10;
    private static final int WAIT_TIMEOUT = 1000;

    private final Class<R1> requestClass;
    private final BiFunction<ClientInfo, R1, R2> processor;
    private final int port;
    private final int countLimit;

    private final List<ClientConnection<ClientInfo, R1, R2>> connections;
    private ServerSocket serverSocket;

    public ClientConnectionController(String name, Class<R1> requestClass, BiFunction<ClientInfo, R1, R2> processor,
                                      int port, int countLimit) {

        super(name);
        this.requestClass = requestClass;
        this.processor = processor;
        this.port = port;
        this.countLimit = countLimit;

        connections = new ArrayList<>();
    }

    public ClientConnectionController(String name, Class<R1> requestClass, BiFunction<ClientInfo, R1, R2> processor,
                                      int port) {
        this(name, requestClass, processor, port, COUNT_LIMIT);
    }

    public ClientConnectionController(Class<R1> requestClass, BiFunction<ClientInfo, R1, R2> processor,
                                      int port) {
        this("ConnectionController", requestClass, processor, port, COUNT_LIMIT);
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            log.debug("Escutando porta: {}", port);
        } catch (IOException e) {
            throw new OnlintRuntimeException("Erro ao escutar porta " + port);
        }
        try {
            try {
                execute();
            } finally {
                closeAllConnections();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            interrupt();
        }
    }

    private void execute() throws InterruptedException {
        do {
            if (hasAvailableSpace()) {
                tryAcceptConnection();
            } else {
                waitAvailableSpace();
            }
        } while (!isInterrupted());
    }

    private synchronized boolean hasAvailableSpace() {
        removeClosedConnections();
        return connections.size() < countLimit;
    }

    private void tryAcceptConnection() {
        Socket socket;
        try {
            socket = serverSocket.accept();
            log.debug("Client connected from: {}", socket.getLocalAddress());
        } catch (InterruptedIOException e) {
            // skip this case
            return;
        } catch (IOException e) {
            throw new OnlintRuntimeException("Erro ao aceitar conexão.");
        }
        addConnection(socket);
    }

    private synchronized void addConnection(Socket socket) {
        ClientConnection<ClientInfo, R1, R2> connection = new ClientConnection<>(ClientInfo.class, requestClass, processor, socket);
        connections.add(connection);
        connection.start();
    }

    private synchronized void removeClosedConnections() {
        connections.removeIf(ClientConnection::isClosed);
    }

    private synchronized void waitAvailableSpace() throws InterruptedException {
        while (!hasAvailableSpace())
            wait(WAIT_TIMEOUT);
    }

    private synchronized void closeAllConnections() {
        connections.parallelStream().forEach(ClientConnection::interrupt);
    }
}
