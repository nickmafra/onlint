package com.nickmafra.onlint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "onlint.MainActivity";

    private String host;
    private int port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void iniciarInteracao(View view) {
        if (!validateHost() | !validatePort())
            return;

        try {
            Intent intent = new Intent(this, OnlineActivity.class);
            intent.putExtra(OnlineActivity.EXTRA_HOST, host);
            intent.putExtra(OnlineActivity.EXTRA_PORT, port);
            startActivity(intent);

        } catch (Exception e) {
            String message = "Erro ao iniciar interação.";
            showMessage(message);
            Log.d(TAG, message, e);
        }
    }

    private boolean validateHost() {
        EditText hostEditText = findViewById(R.id.hostEditText);
        host = hostEditText.getText().toString();
        if (host.isEmpty()) {
            showMessage("Preencha o host.");
            return false;
        }
        return true;
    }

    private boolean validatePort() {
        EditText portEditText = findViewById(R.id.portEditText);
        String strPort = portEditText.getText().toString();
        if (strPort.isEmpty()) {
            portEditText.setError("Preencha a porta.");
            return false;
        }
        try {
            port = Integer.parseInt(strPort);
        } catch (NumberFormatException e) {
            portEditText.setError("Digite um número válido para a porta.");
            return false;
        }
        if (port <= 0) {
            portEditText.setError("A porta deve ser positiva.");
        }
        return true;
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
