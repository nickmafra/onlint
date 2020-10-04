package com.nickmafra.onlint.io;

import com.nickmafra.concurrent.NamedThreadFactory;
import com.nickmafra.onlint.RetanguloClientState;
import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.UpdateRequest;
import com.nickmafra.onlint.model.UpdateResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class UpdateSender {

    private final RetanguloClientState state;
    private final ServerUpdateConnection updateConnection;

    private final AtomicReference<Runnable> onError = new AtomicReference<>();

    private final ExecutorService executorService;
    private final AtomicBoolean started = new AtomicBoolean(false);

    public UpdateSender(RetanguloClientState state, ServerUpdateConnection updateConnection) {
        this.state = state;
        this.updateConnection = updateConnection;
        executorService = Executors.newSingleThreadExecutor(new NamedThreadFactory("UpdateSender"));
    }

    public void setOnError(Runnable onError) {
        this.onError.set(onError);
    }

    public synchronized void start() {
        if (started.get())
            throw new IllegalStateException("Já iniciado.");

        ClientInfo clientInfo = generateClientInfo();
        updateConnection.start(clientInfo);
        started.set(true);
    }

    private static final Random RANDOM = new Random();

    private ClientInfo generateClientInfo() {
        return new ClientInfo(String.valueOf(RANDOM.nextLong()));
    }

    public void sendUpdate() {
        if (!started.get())
            throw new IllegalStateException("Não iniciado.");

        UpdateRequest updateRequest = state.createUpdateRequest();
        Callable<UpdateResponse> callable = new SendCallable(updateRequest);
        executorService.submit(callable);
    }

    private class SendCallable implements Callable<UpdateResponse> {

        public SendCallable(UpdateRequest request) {
            this.request = request;
        }

        private final UpdateRequest request;

        @Override
        public UpdateResponse call() throws Exception {
            try {
                return updateConnection.execute(request);
            } catch (Exception e) {
                executeOnError();
                throw e;
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
