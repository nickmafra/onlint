package com.nickmafra.onlint.io;

import com.nickmafra.onlint.model.UpdateRequest;
import com.nickmafra.onlint.model.UpdateResponse;
import com.nickmafra.onlint.RetanguloState;

public class UpdateCcc extends ClientConnectionController<UpdateRequest, UpdateResponse> {

    public UpdateCcc(RetanguloState state, int port) {
        super("UpdateClientConnectionController", UpdateRequest.class, state::updateByClient, port);
    }
}
