package com.nickmafra.onlint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MENSAGEM = "onlint.MainActivity.mensagem";

    private String host;
    private int port = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {

            host = intent.getStringExtra(OnlineActivity.EXTRA_HOST);
            port = intent.getIntExtra(OnlineActivity.EXTRA_PORT, -1);
            String mensagem = intent.getStringExtra(EXTRA_MENSAGEM);
            if (mensagem != null && !mensagem.isEmpty()) {
                showMessage(mensagem);
            }

        }
    }

    public void iniciarInteracao(View view) {
        if (!validateHost() | !validatePort())
            return;

        Intent intent = new Intent(this, OnlineActivity.class);
        intent.putExtra(OnlineActivity.EXTRA_HOST, host);
        intent.putExtra(OnlineActivity.EXTRA_PORT, port);
        startActivity(intent);
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
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }
}
