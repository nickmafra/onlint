package com.nickmafra.onlint;

import android.content.Intent;
import android.os.Bundle;

import com.nickmafra.onlint.io.ReadThread;
import com.nickmafra.onlint.io.ServerReadConnection;
import com.nickmafra.onlint.io.ServerUpdateConnection;
import com.nickmafra.onlint.io.SendUpdateThread;

import androidx.appcompat.app.AppCompatActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class OnlineActivity extends AppCompatActivity {

    private static final String TAG = "onlint.OnlineActivity";

    public static final String EXTRA_HOST = "onlint.OnlineActivity.host";
    public static final String EXTRA_PORT = "onlint.OnlineActivity.port";

    private String host;
    private int port;

    private RetanguloClientState state;
    private ReadThread readThread;
    private SendUpdateThread updateThread;

    private OnlintSurfaceView surfaceView;
    private SurfaceDrawerThread drawerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        host = intent.getStringExtra(EXTRA_HOST);
        port = intent.getIntExtra(EXTRA_PORT, -1);

        createClientThreads();
        surfaceView = new OnlintSurfaceView(this, state, updateThread);
        drawerThread = new SurfaceDrawerThread(surfaceView);

        setContentView(surfaceView);
        updateThread.start();
        drawerThread.start();
        readThread.start();
    }

    private void createClientThreads() {
        int readPort = port;
        int updatePort = port + 1;

        ServerReadConnection readConnection = new ServerReadConnection(host, readPort);
        ServerUpdateConnection updateConnection = new ServerUpdateConnection(host, updatePort);

        state = new RetanguloClientState();
        readThread = new ReadThread(state, readConnection);
        updateThread = new SendUpdateThread(updateConnection);
    }

    public void voltar(String mensagem) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(OnlineActivity.EXTRA_HOST, host);
        intent.putExtra(OnlineActivity.EXTRA_PORT, port);
        if (mensagem != null) {
            intent.putExtra(MainActivity.EXTRA_MENSAGEM, mensagem);
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (readThread != null) {
            readThread.interrupt();
        }
        if (drawerThread != null) {
            drawerThread.interrupt();
        }
    }
}
