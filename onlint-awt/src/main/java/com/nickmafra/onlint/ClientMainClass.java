package com.nickmafra.onlint;

import com.nickmafra.concurrent.ConcurrentUtils;
import com.nickmafra.onlint.io.ReadThread;
import com.nickmafra.onlint.io.ServerReadConnection;
import com.nickmafra.onlint.io.ServerUpdateConnection;
import com.nickmafra.onlint.io.ServerUpdateSender;

public class ClientMainClass {

    public static final String HOST = "localhost";
    public static final int READ_PORT = 8029;
    public static final int UPDATE_PORT = 8030;

    public static void main(String[] args) throws InterruptedException {
        ServerReadConnection readConnection = new ServerReadConnection(HOST, READ_PORT);
        ServerUpdateConnection updateConnection = new ServerUpdateConnection(HOST, UPDATE_PORT);

        RetanguloClientState state = new RetanguloClientState();
        ReadThread readThread = new ReadThread(state, readConnection);
        ServerUpdateSender updateSender = new ServerUpdateSender(state, updateConnection);
        GraphicRetangulo graphic = new GraphicRetangulo(readThread, updateSender);

        updateSender.start();
        graphic.start();
        readThread.start();

        ConcurrentUtils.stopAllIfSomeoneStops(500, readThread);

        graphic.close();
    }

}
