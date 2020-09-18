package com.nickmafra.io;

import com.nickmafra.onlint.model.ClientInfo;
import com.nickmafra.onlint.model.UpdateRequest;

public class EnvelopeMock {

    private EnvelopeMock() {}

    public static UpdateRequest mock1() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setMouseX(289);
        return updateRequest;
    }

    public static ClientInfo mock2() {
        return new ClientInfo("asd90823h");
    }
}
