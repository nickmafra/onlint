package com.nickmafra.onlint;

import com.nickmafra.onlint.model.ReadResponse;
import com.nickmafra.onlint.model.UpdateRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static com.nickmafra.onlint.StateConstants.INITIAL_X;
import static com.nickmafra.onlint.StateConstants.INITIAL_Y;

@Data
@RequiredArgsConstructor
public class RetanguloClientState {

    private String clientId;

    private volatile int screenWidth = 800;
    private volatile int screenHeight = 600;
    private volatile int objWidth = StateConstants.OBJ_WIDTH;
    private volatile int objHeight = StateConstants.OBJ_HEIGHT;

    private volatile int objX = INITIAL_X;
    private volatile int objY = INITIAL_Y;

    private volatile boolean arrastando;

    public synchronized void receiveUpdate(ReadResponse readResponse) {
        objX = readResponse.getX();
        objY = readResponse.getY();
    }

    public synchronized UpdateRequest createUpdateRequest() {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setArrastando(arrastando);
        if (arrastando) {
            updateRequest.setMouseX(objX);
            updateRequest.setMouseY(objY);
        }
        return updateRequest;
    }
}
