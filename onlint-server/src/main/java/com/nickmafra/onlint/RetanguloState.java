package com.nickmafra.onlint;

import com.nickmafra.onlint.model.*;
import com.nickmafra.util.MathUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.nickmafra.onlint.StateConstants.*;

@Slf4j
public class RetanguloState {

    public static final float GRAVITY = 0.5F;
    public static final float MAX_VELOCITY = 50;

    public static final int ARRASTANDO_TIMEOUT = 2000;

    private volatile int x = INITIAL_X;
    private volatile int y = INITIAL_Y;
    private volatile float velocity;
    private final AtomicReference<ClientInfo> arrastandoClientInfo;
    private final AtomicLong arrastandoTime;

    public RetanguloState() {
        arrastandoClientInfo = new AtomicReference<>();
        arrastandoTime = new AtomicLong();
    }

    private boolean alguemEstaArrastando() {
        return arrastandoClientInfo.get() != null;
    }

    private boolean clientEstaArrastando(ClientInfo clientInfo) {
        return clientInfo != null && clientInfo.equals(arrastandoClientInfo.get());
    }

    private long agora() {
        return System.currentTimeMillis();
    }

    public synchronized ReadResponse read(ClientInfo clientInfo, ReadRequest request) {
        log.debug("Client lendo: {}", clientInfo.getClientId());
        return new ReadResponse(x, y, clientEstaArrastando(clientInfo));
    }

    public synchronized UpdateResponse updateByClient(ClientInfo clientInfo, UpdateRequest request) {
        log.debug("Client atualizando: {}", clientInfo.getClientId());
        updateArrastandoByClient(clientInfo, request.isArrastando());
        if (!clientEstaArrastando(clientInfo)) {
            return new UpdateResponse(false);
        }
        updatePositionByClient(clientInfo, request.getMouseX(), request.getMouseY());
        return new UpdateResponse(true);
    }

    public synchronized void updateByServer() {
        updateArrastandoByServer();
        updatePositionByServer();
    }

    private void updateArrastandoByClient(ClientInfo clientInfo, boolean arrastando) {
        if (arrastando && !alguemEstaArrastando()) {
            arrastandoClientInfo.set(clientInfo);
            arrastandoTime.set(agora());
        } else if (clientEstaArrastando(clientInfo)) {
            if (arrastando) {
                arrastandoTime.set(agora());
            } else {
                arrastandoClientInfo.set(null);
            }
        }
    }

    private void updatePositionByClient(ClientInfo clientInfo, int x, int y) {
        if (clientEstaArrastando(clientInfo)) {
            this.x = MathUtil.limitRange(x, 0, SCREEN_WIDTH - OBJ_WIDTH);
            this.y = MathUtil.limitRange(y, 0, SCREEN_HEIGHT - OBJ_HEIGHT);
        }
    }

    private void updatePositionByServer() {
        if (alguemEstaArrastando()) {
            // pára objeto
            velocity = 0;
        } else {
            // aplica gravidade
            float newVelocity = velocity + GRAVITY;
            velocity = MathUtil.limitRange(newVelocity, -MAX_VELOCITY, MAX_VELOCITY);
            int newY = Math.round(y + velocity);
            y = MathUtil.limitRange(newY, 0, SCREEN_HEIGHT - OBJ_HEIGHT, false, () -> this.velocity = 0);
        }
    }

    private void updateArrastandoByServer() {
        if (alguemEstaArrastando()) {
            long agora = agora();
            long delta = agora - arrastandoTime.get();
            if (delta > ARRASTANDO_TIMEOUT) {
                arrastandoClientInfo.set(null);
            }
        }
    }
}
