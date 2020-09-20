package com.nickmafra.onlint;

import android.content.Intent;
import android.os.Bundle;

import com.nickmafra.onlint.io.ReadThread;
import com.nickmafra.onlint.io.ServerReadConnection;
import com.nickmafra.onlint.io.ServerUpdateConnection;
import com.nickmafra.onlint.io.ServerUpdateSender;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OnlineActivity extends AppCompatActivity {

    public static final String EXTRA_HOST = "onlint.OnlineActivity.host";
    public static final String EXTRA_PORT = "onlint.OnlineActivity.port";

    private String host;
    private int port;

    private RetanguloClientState state;
    private ReadThread readThread;
    private ServerUpdateSender updateSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_online);

        Intent intent = getIntent();
        host = intent.getStringExtra(EXTRA_HOST);
        port = intent.getIntExtra(EXTRA_PORT, -1);

        startClient();
    }

    private void startClient() {
        int readPort = port;
        int updatePort = port + 1;

        ServerReadConnection readConnection = new ServerReadConnection(host, readPort);
        ServerUpdateConnection updateConnection = new ServerUpdateConnection(host, updatePort);

        state = new RetanguloClientState();
        readThread = new ReadThread(state, readConnection);
        updateSender = new ServerUpdateSender(state, updateConnection);

        updateSender.start();
        readThread.start();
    }
}
