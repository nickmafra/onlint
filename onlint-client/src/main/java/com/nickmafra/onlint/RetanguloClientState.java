package com.nickmafra.onlint;

import com.nickmafra.onlint.model.ReadResponse;
import com.nickmafra.onlint.model.UpdateRequest;
import com.nickmafra.util.MathUtil;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.nickmafra.onlint.StateConstants.*;

@Getter
@RequiredArgsConstructor
public class RetanguloClientState {

    private volatile int screenWidth = SCREEN_WIDTH;
    private volatile int screenHeight = SCREEN_HEIGHT;
    private volatile int objWidth = OBJ_WIDTH;
    private volatile int objHeight = OBJ_HEIGHT;

    private volatile int objX = INITIAL_X;
    private volatile int objY = INITIAL_Y;

    private volatile boolean arrastando;

    public synchronized void receiveUpdate(ReadResponse readResponse) {
        objX = readResponse.getX();
        objY = readResponse.getY();
    }

    private volatile int objRelativeX;
    private volatile int objRelativeY;

    public boolean isOverObj(int x, int y) {
        return objX < x && x < objX + objWidth
                && objY < y && y < objY + objHeight;
    }

    public synchronized boolean pegaObjeto(int x, int y) {
        objRelativeX = objX - x;
        objRelativeY = objY - y;
        arrastando = isOverObj(x, y);
        return arrastando;
    }

    public synchronized boolean arrastaObjeto(int x, int y) {
        if (arrastando) {
            objX = MathUtil.limitRange(x + objRelativeX, 0, screenWidth - objWidth);
            objY = MathUtil.limitRange(y + objRelativeY, 0, screenHeight - objHeight);
        }
        return arrastando;
    }

    public synchronized void soltaObjeto() {
        arrastando = false;
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
