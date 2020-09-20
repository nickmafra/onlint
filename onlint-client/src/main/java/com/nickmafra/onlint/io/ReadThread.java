package com.nickmafra.onlint.io;

import com.nickmafra.exception.CheckedRunnable;
import com.nickmafra.onlint.RetanguloClientState;
import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.ReadRequest;
import com.nickmafra.onlint.model.ReadResponse;
import com.nickmafra.concurrent.LimitedRateThread;

import java.util.Random;

public class ReadThread extends LimitedRateThread {

    public static final int PERIOD = 20;

    private final RetanguloClientState state;
    private final ServerReadConnection readConnection;

    public ReadThread(RetanguloClientState state, ServerReadConnection readConnection) {
        super("ReadThread", PERIOD, null);
        this.state = state;
        this.readConnection = readConnection;

        setRunnable(new CheckedRunnable<InterruptedException>() {
            @Override
            public void run() throws InterruptedException {
                ReadThread.this.sendReadRequest();
            }
        });
    }

    public RetanguloClientState getClientState() {
        return state;
    }

    @Override
    public void run() {
        readConnection.start(generateClientInfo());
        super.run();
    }

    private static final Random RANDOM = new Random();

    public ClientInfo generateClientInfo() {
        return new ClientInfo(String.valueOf(RANDOM.nextLong()));
    }

    public void sendReadRequest() throws InterruptedException {
        ReadRequest request = new ReadRequest();
        ReadResponse response = readConnection.execute(request);
        state.receiveUpdate(response);
    }
}
