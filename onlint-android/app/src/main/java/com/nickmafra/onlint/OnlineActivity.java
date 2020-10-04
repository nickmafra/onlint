package com.nickmafra.onlint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.nickmafra.onlint.exception.ExceptionConsumer;
import com.nickmafra.onlint.io.OnlintClientThread;

public class OnlineActivity extends AppCompatActivity {

    private static final String TAG = "onlint.OnlineActivity";

    public static final String EXTRA_HOST = "onlint.OnlineActivity.host";
    public static final String EXTRA_PORT = "onlint.OnlineActivity.port";

    private String host;
    private int port;

    private OnlintClientThread clientThread;

    private OnlintSurfaceView surfaceView;
    private SurfaceDrawerThread drawerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        host = intent.getStringExtra(EXTRA_HOST);
        port = intent.getIntExtra(EXTRA_PORT, -1);

        clientThread = new OnlintClientThread(host, port, port + 1);
        clientThread.setOnStop(new VoltarExceptionConsumer());

        surfaceView = new OnlintSurfaceView(this, clientThread);
        drawerThread = new SurfaceDrawerThread(surfaceView);

        surfaceView.setDrawerThread(drawerThread);
        setContentView(surfaceView);

        clientThread.start();
        drawerThread.start();
    }

    public void voltar(String mensagem) {
        interruptThreads();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(OnlineActivity.EXTRA_HOST, host);
        intent.putExtra(OnlineActivity.EXTRA_PORT, port);
        if (mensagem != null) {
            intent.putExtra(MainActivity.EXTRA_MENSAGEM, mensagem);
        }
        startActivity(intent);
    }

    public void voltar(Exception e) {
        Log.e(TAG, "voltar: Erro na OnlintClientThread", e);
        voltar(e == null ? "Erro desconhecido" : e.getMessage());
    }

    private class VoltarExceptionConsumer implements ExceptionConsumer {

        @Override
        public void consumes(Exception e) {
            voltar(e);
        }
    }

    @Override
    protected void onDestroy() {
        interruptThreads();
        super.onDestroy();
    }

    public void interruptThreads() {
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (drawerThread != null) {
            drawerThread.interrupt();
        }
    }
}
