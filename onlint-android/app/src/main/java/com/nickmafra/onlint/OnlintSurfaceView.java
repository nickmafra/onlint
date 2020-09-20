package com.nickmafra.onlint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nickmafra.onlint.io.ServerUpdateSender;
import com.nickmafra.util.MathUtil;

public class OnlintSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "OnlintSurfaceView";
    private final RetanguloClientState state;
    private final ServerUpdateSender updateSender;

    private boolean screenReady;
    private int offsetX;
    private int offsetY;
    private float widthRatio;
    private float heightRatio;

    private final Paint paint;

    public OnlintSurfaceView(Context context, RetanguloClientState state, ServerUpdateSender updateSender) {
        super(context);
        this.state = state;
        this.updateSender = updateSender;

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

    private volatile int objRelativeX;
    private volatile int objRelativeY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) ((event.getX() - offsetX) / widthRatio);
        int y = (int) ((event.getY() - offsetY) / heightRatio);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            Log.d(TAG, "Pressed at " + x + ", " +  y);

            if (touchIsOverObj(x, y)) {
                synchronized (state) {
                    objRelativeX = state.getObjX() - x;
                    objRelativeY = state.getObjY() - y;
                    state.setArrastando(true);
                }
                this.updateSender.sendUpdate();
            }

            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (state.isArrastando()) {
                state.setObjX(MathUtil.limitRange(x + objRelativeX, 0, state.getScreenWidth() - state.getObjWidth()));
                state.setObjY(MathUtil.limitRange(y + objRelativeY, 0, state.getScreenHeight() - state.getObjHeight()));
                this.updateSender.sendUpdate();
            }
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            state.setArrastando(false);
            return true;
        }
        return false;
    }

    private boolean touchIsOverObj(int x, int y) {
        return state.getObjX() < x && x < state.getObjX() + state.getObjWidth()
                && state.getObjY() < y && y < state.getObjY() + state.getObjHeight();
    }
}
