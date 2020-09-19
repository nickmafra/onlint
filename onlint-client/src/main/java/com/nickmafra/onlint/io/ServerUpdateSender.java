package com.nickmafra.onlint.io;

import com.nickmafra.onlint.RetanguloClientState;
import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.UpdateRequest;

import java.util.Random;

public class ServerUpdateSender {

    private final RetanguloClientState state;
    private final ServerUpdateConnection updateConnection;
    private ClientInfo clientInfo;

    public ServerUpdateSender(RetanguloClientState state, ServerUpdateConnection updateConnection) {
        this.state = state;
        this.updateConnection = updateConnection;
    }

    public void start() {
        if (clientInfo != null) {
            throw new IllegalStateException("Já iniciado.");
        }
        clientInfo = generateClientInfo();
        updateConnection.start(clientInfo);
    }

    private static final Random RANDOM = new Random();

    private ClientInfo generateClientInfo() {
        return new ClientInfo(String.valueOf(RANDOM.nextLong()));
    }

    public void sendUpdate() {
        UpdateRequest updateRequest = state.createUpdateRequest();
        updateConnection.execute(updateRequest);
    }
}
