package com.nickmafra.onlint;

import com.nickmafra.onlint.io.OnlintClientThread;

public class ClientMainClass {

    public static final String HOST = "localhost";
    public static final int READ_PORT = 8029;
    public static final int UPDATE_PORT = READ_PORT + 1;

    public static void main(String[] args) {
        OnlintClientThread client = new OnlintClientThread(HOST, READ_PORT, UPDATE_PORT);
        GraphicRetangulo graphic = new GraphicRetangulo(client);

        client.setOnStop(e -> graphic.close());

        client.start();
        graphic.start();
    }

}
