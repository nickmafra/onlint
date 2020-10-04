package com.nickmafra.onlint.io;

import com.nickmafra.onlint.RetanguloClientState;
import com.nickmafra.onlint.exception.ExceptionConsumer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class OnlintClientThread extends Thread {

    private static final int DEFAULT_PERIOD = 20;

    private final RetanguloClientState state;
    private final UpdateReceiver updateReceiver;
    private final UpdateSender updateSender;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final Object lock = new Object();

    private final AtomicReference<ExceptionConsumer> onStop = new AtomicReference<>();

    public OnlintClientThread(String host, int readPort, int updatePort) {
        state = new RetanguloClientState();

        ServerReadConnection readConnection = new ServerReadConnection(host, readPort);
        updateReceiver = new UpdateReceiver(state, readConnection, DEFAULT_PERIOD);
        updateReceiver.setOnError(new InterruptRunnable());

        ServerUpdateConnection updateConnection = new ServerUpdateConnection(host, updatePort);
        updateSender = new UpdateSender(state, updateConnection);
        updateSender.setOnError(new InterruptRunnable());
    }

    public RetanguloClientState getClientState() {
        return state;
    }

    public UpdateSender getUpdateSender() {
        return updateSender;
    }

    public void setOnStop(ExceptionConsumer onStop) {
        this.onStop.set(onStop);
    }

    @Override
    public void run() {
        if (started.get())
            throw new IllegalStateException("Já iniciado.");

        started.set(true);

        try {
            updateSender.start();
            updateReceiver.start();

            while (!isInterrupted())
                synchronized (lock) {
                    lock.wait(500);
                }

            selfInterrupt(null);
        } catch (InterruptedException e) {
            selfInterrupt(e);
            interrupt();
        } catch (Exception e) {
            e.printStackTrace();
            selfInterrupt(e);
        }
    }

    private void selfInterrupt(Exception e) {
        log.info("OnlintClient interrompido.");

        if (updateReceiver != null)
            updateReceiver.interrupt();

        if (updateSender != null)
            updateSender.interrupt();

        synchronized (onStop) {
            if (onStop.get() != null)
                onStop.get().consumes(null);
        }
    }
    
    private class InterruptRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                interrupt();
                lock.notifyAll();
            }
        }
    }
}
