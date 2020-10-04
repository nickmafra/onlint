package com.nickmafra.onlint;

import com.nickmafra.concurrent.ConcurrentUtils;
import com.nickmafra.onlint.io.ReadCcc;
import com.nickmafra.onlint.io.ServerUpdateThread;
import com.nickmafra.onlint.io.UpdateCcc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerMainClass {

    public static final int READ_PORT = 8029;
    public static final int UPDATE_PORT = 8030;

    public static void main(String[] args) throws InterruptedException {
        log.info("Iniciando servidor.");
        RetanguloState state = new RetanguloState();
        ReadCcc readCcc = new ReadCcc(state, READ_PORT);
        ServerUpdateThread serverUpdateThread =  new ServerUpdateThread(state);
        UpdateCcc updateCcc = new UpdateCcc(state, UPDATE_PORT);

        readCcc.start();
        serverUpdateThread.start();
        updateCcc.start();

        log.info("Servidor iniciado.");

        ConcurrentUtils.stopAllIfSomeoneStops(500, readCcc, serverUpdateThread, updateCcc);
    }
}
