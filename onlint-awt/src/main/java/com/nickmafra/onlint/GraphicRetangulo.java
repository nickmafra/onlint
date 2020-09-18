package com.nickmafra.onlint;

import com.nickmafra.gfx.MouseActionListenerAdapter;
import com.nickmafra.gfx.SimpleGraphic;
import com.nickmafra.onlint.io.ReadThread;
import com.nickmafra.onlint.io.ServerUpdateSender;
import com.nickmafra.util.MathUtil;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.event.MouseEvent;

@Slf4j
public class GraphicRetangulo extends SimpleGraphic {

    public static final int WIDTH = StateConstants.SCREEN_WIDTH;
    public static final int HEIGHT = StateConstants.SCREEN_HEIGHT;
    public static final double FPS = 60;

    private final RetanguloClientState state;

    public GraphicRetangulo(ReadThread readThread, ServerUpdateSender updateSender) {
        super("GraphicRetangulo", WIDTH, HEIGHT, FPS);
        this.state = readThread.getClientState();

        setDrawer(this::draw);
        addMouseActionListener(new GraphicRetangulo.MouseActionListenerImpl(state, updateSender));
        addOnClosingListener(e -> readThread.interrupt());
    }

    private void draw(Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, getDimension().width, getDimension().height);

        g.setColor(Color.RED);
        g.fillRect(state.getObjX(), state.getObjY(), state.getObjWidth(), state.getObjHeight());
    }


    public static class MouseActionListenerImpl extends MouseActionListenerAdapter {

        private final RetanguloClientState state;
        private final ServerUpdateSender updateSender;

        private volatile int objRelativeX;
        private volatile int objRelativeY;

        public MouseActionListenerImpl(RetanguloClientState state, ServerUpdateSender updateSender) {
            this.state = state;
            this.updateSender = updateSender;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            log.debug("Pressed at {}, {}", x, y);

            if (mouseIsOverObj(x, y)) {
                synchronized (state) {
                    objRelativeX = state.getObjX() - x;
                    objRelativeY = state.getObjY() - y;
                    state.setArrastando(true);
                }
                this.updateSender.sendUpdate();
            }
        }

        private boolean mouseIsOverObj(int x, int y) {
            return state.getObjX() < x && x < state.getObjX() + state.getObjWidth()
                    && state.getObjY() < y && y < state.getObjY() + state.getObjHeight();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (state.isArrastando()) {
                state.setObjX(MathUtil.limitRange(x + objRelativeX, 0, state.getScreenWidth() - state.getObjWidth()));
                state.setObjY(MathUtil.limitRange(y + objRelativeY, 0, state.getScreenHeight() - state.getObjHeight()));
                this.updateSender.sendUpdate();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            synchronized (state) {
                state.setArrastando(false);
            }
        }
    }
}
