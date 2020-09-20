package com.nickmafra.onlint;

import com.nickmafra.concurrent.ConcurrentUtils;
import com.nickmafra.onlint.io.ReadThread;
import com.nickmafra.onlint.io.ServerReadConnection;
import com.nickmafra.onlint.io.ServerUpdateConnection;
import com.nickmafra.onlint.io.SendUpdateThread;

public class ClientMainClass {

    public static final String HOST = "localhost";
    public static final int READ_PORT = 8029;
    public static final int UPDATE_PORT = 8030;

    public static void main(String[] args) throws InterruptedException {
        ServerReadConnection readConnection = new ServerReadConnection(HOST, READ_PORT);
        ServerUpdateConnection updateConnection = new ServerUpdateConnection(HOST, UPDATE_PORT);

        RetanguloClientState state = new RetanguloClientState();
        ReadThread readThread = new ReadThread(state, readConnection);
        SendUpdateThread updateThread = new SendUpdateThread(updateConnection);
        GraphicRetangulo graphic = new GraphicRetangulo(readThread, updateThread);

        updateThread.start();
        graphic.start();
        readThread.start();

        ConcurrentUtils.stopAllIfSomeoneStops(500, updateThread, readThread);

        graphic.close();
    }

}
