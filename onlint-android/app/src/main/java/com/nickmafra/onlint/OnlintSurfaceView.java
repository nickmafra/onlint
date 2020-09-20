package com.nickmafra.onlint;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.nickmafra.onlint.io.SendUpdateThread;
import com.nickmafra.onlint.model.UpdateRequest;

public class OnlintSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "OnlintSurfaceView";

    private final OnlineActivity onlineActivity;
    private final RetanguloClientState state;
    private final SendUpdateThread updateSender;

    private boolean screenReady;
    private int offsetX;
    private int offsetY;
    private float widthRatio;
    private float heightRatio;

    private final Paint paint;

    public OnlintSurfaceView(OnlineActivity onlineActivity, RetanguloClientState state, SendUpdateThread updateThread) {
        super(onlineActivity);
        this.onlineActivity = onlineActivity;
        this.state = state;
        this.updateSender = updateThread;

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
        offsetX = 0;
        offsetY = 0;
        widthRatio = width / (float) state.getScreenWidth();
        heightRatio = height / (float) state.getScreenHeight();
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
        return new Rect((int) (offsetX + widthRatio * state.getObjX()),
                (int) (offsetY + heightRatio * state.getObjY()),
                (int) (widthRatio * state.getObjWidth()),
                (int) (heightRatio * state.getObjHeight()));
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
            UpdateRequest updateRequest = state.createUpdateRequest();
            updateSender.addUpdate(updateRequest);
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
