package com.nickmafra.onlint.io;

import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.ReadRequest;
import com.nickmafra.onlint.model.ReadResponse;

public class ServerReadConnection extends ServerConnection<ClientInfo, ReadRequest, ReadResponse> {

    public ServerReadConnection(String host, int port) {
        super(ReadResponse.class, host, port);
    }
}
