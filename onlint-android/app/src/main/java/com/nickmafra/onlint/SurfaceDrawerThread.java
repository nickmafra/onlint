package com.nickmafra.onlint;

import android.graphics.Canvas;
import android.view.SurfaceView;

import com.nickmafra.concurrent.LimitedRateThread;
import com.nickmafra.exception.CheckedRunnable;

public class SurfaceDrawerThread extends LimitedRateThread {

    private static final int PERIOD = 16;

    private final SurfaceView surfaceView;

    public SurfaceDrawerThread(SurfaceView surfaceView) {
        super("SurfaceDrawerThread", PERIOD, null);
        setRunnable(new SurfaceDrawer());
        this.surfaceView = surfaceView;
    }

    private class SurfaceDrawer implements CheckedRunnable<InterruptedException> {

        public void run() {
            Canvas canvas = null;
            try {
                canvas = surfaceView.getHolder().lockCanvas();
                synchronized (canvas) {
                    surfaceView.draw(canvas);
                }
            } catch (Exception e) {
                // do nothing
            } finally {
                if (canvas != null) {
                    surfaceView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
