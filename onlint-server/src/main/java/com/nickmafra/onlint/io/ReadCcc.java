package com.nickmafra.onlint.io;

import com.nickmafra.onlint.model.ReadRequest;
import com.nickmafra.onlint.model.ReadResponse;
import com.nickmafra.onlint.RetanguloState;

public class ReadCcc extends ClientConnectionController<ReadRequest, ReadResponse> {

    public ReadCcc(RetanguloState state, int port) {
        super("ReadClientConnectionController", ReadRequest.class, state::read, port);
    }
}
