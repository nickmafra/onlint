package com.nickmafra.onlint.io;

import com.nickmafra.concurrent.NamedThreadFactory;
import com.nickmafra.onlint.RetanguloClientState;
import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.ReadRequest;
import com.nickmafra.onlint.model.ReadResponse;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UpdateReceiver {

    private final RetanguloClientState state;
    private final ServerReadConnection readConnection;
    private final int period;

    private final AtomicReference<Runnable> onError = new AtomicReference<>();

    private final ScheduledExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public UpdateReceiver(RetanguloClientState state, ServerReadConnection readConnection, int period) {
        this.state = state;
        this.readConnection = readConnection;
        this.period = period;

        executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("UpdateReceiver"));
    }

    public void setOnError(Runnable onError) {
        this.onError.set(onError);
    }

    public RetanguloClientState getClientState() {
        return state;
    }

    public synchronized void start() {
        if (started.get())
            throw new IllegalStateException("Já iniciado.");

        readConnection.start(generateClientInfo());
        executorService.scheduleAtFixedRate(new ReceiveRunnable(), 0, period, TimeUnit.MILLISECONDS);
        started.set(true);
    }

    private static final Random RANDOM = new Random();

    public ClientInfo generateClientInfo() {
        return new ClientInfo(String.valueOf(RANDOM.nextLong()));
    }

    private class ReceiveRunnable implements Runnable {

        @Override
        public void run() {
            try {
                ReadRequest request = new ReadRequest();
                ReadResponse response = readConnection.execute(request);
                state.receiveUpdate(response);
            } catch (InterruptedException e) {
                executeOnError();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                executeOnError();
            }
        }

        private void executeOnError() {
            synchronized (onError) {
                if (onError.get() != null) {
                    onError.get().run();
                }
            }
        }
    }

    public void interrupt() {
        executorService.shutdownNow();
    }
}
