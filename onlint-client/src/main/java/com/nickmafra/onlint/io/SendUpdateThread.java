package com.nickmafra.onlint.io;

import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.UpdateRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class SendUpdateThread extends Thread {

    private final ServerUpdateConnection updateConnection;

    private final List<UpdateRequest> updateRequests;

    public SendUpdateThread(ServerUpdateConnection updateConnection) {
        super("UpdateThread");
        this.updateConnection = updateConnection;
        updateRequests = new ArrayList<>();
    }

    @Override
    public void run() {
        startConnection();
        try {
            while (!isInterrupted())
                sendNextUpdate();
        } catch (InterruptedException e) {
            log.error("ThreUpdateThread Interrompida.", e);
            interrupt();
        }
    }

    private void startConnection() {
        ClientInfo clientInfo = generateClientInfo();
        updateConnection.start(clientInfo);
    }

    private static final Random RANDOM = new Random();

    private ClientInfo generateClientInfo() {
        return new ClientInfo(String.valueOf(RANDOM.nextLong()));
    }

    public void sendNextUpdate() throws InterruptedException {
        synchronized (updateRequests) {
            waitHaveUpdate();
            updateConnection.execute(updateRequests.remove(0));
        }
    }

    public void waitHaveUpdate() throws InterruptedException {
        synchronized (updateRequests) {
            while (updateRequests.isEmpty()) {
                updateRequests.wait();
            }
        }
    }

    public void addUpdate(UpdateRequest updateRequest) {
        synchronized (updateRequests) {
            updateRequests.add(updateRequest);
            updateRequests.notifyAll();
        }
    }
}
