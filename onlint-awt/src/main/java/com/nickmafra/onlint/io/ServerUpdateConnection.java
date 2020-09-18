package com.nickmafra.onlint.io;

import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.UpdateRequest;
import com.nickmafra.onlint.model.UpdateResponse;

public class ServerUpdateConnection extends ServerConnection<ClientInfo, UpdateRequest, UpdateResponse> {

    public ServerUpdateConnection(String host, int port) {
        super(UpdateResponse.class, host, port);
    }
}
