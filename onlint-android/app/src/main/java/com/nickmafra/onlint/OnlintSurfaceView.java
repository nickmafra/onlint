package com.nickmafra.onlint;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.nickmafra.onlint.io.OnlintClientThread;
import com.nickmafra.onlint.io.UpdateSender;

public class OnlintSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "OnlintSurfaceView";

    private final OnlineActivity onlineActivity;
    private final RetanguloClientState state;
    private final UpdateSender updateSender;

    private boolean screenReady;
    private int offsetX;
    private int offsetY;
    private float widthRatio;
    private float heightRatio;

    private final Paint paint;

    public OnlintSurfaceView(OnlineActivity onlineActivity, OnlintClientThread clientThread) {
        super(onlineActivity);
        this.onlineActivity = onlineActivity;
        this.state = clientThread.getClientState();
        this.updateSender = clientThread.getUpdateSender();

        getHolder().addCallback(this);
        setFocusable(true);

        paint = createPaint();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // stretch
        int widthUtil = width;
        int heightUtil = height;
        offsetX = 0;
        offsetY = 0;
        widthRatio = widthUtil / (float) state.getScreenWidth();
        heightRatio = heightUtil / (float) state.getScreenHeight();
        screenReady = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (!screenReady) {
            return;
        }

        Rect rect = createRect();
        canvas.drawRect(rect, paint);
    }

    private Rect createRect() {
        int x = (int) (offsetX + state.getObjX() * widthRatio);
        int y = (int) (offsetY + state.getObjY() * heightRatio);
        int width = (int) (state.getObjWidth() * widthRatio);
        int height = (int) (state.getObjHeight() * heightRatio);
        return new Rect(x, y, x + width, y + height);
    }

    private static Paint createPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) ((event.getX() - offsetX) / widthRatio);
        int y = (int) ((event.getY() - offsetY) / heightRatio);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            Log.d(TAG, "Pressed at " + x + ", " +  y);

            if (state.pegaObjeto(x, y)) {
                sendUpdate();
            }

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (state.arrastaObjeto(x, y)) {
                sendUpdate();
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            state.soltaObjeto();
            sendUpdate();
            return true;
        }
        return false;
    }

    private void sendUpdate() {
        try {
            updateSender.sendUpdate();
        } catch (Exception e) {
            problemaConexao(e);
        }
    }

    private void problemaConexao(Exception e) {
        String message = "Erro na conexão com o servidor.";
        Log.e(TAG, message, e);
        onlineActivity.voltar(message);
    }
}
