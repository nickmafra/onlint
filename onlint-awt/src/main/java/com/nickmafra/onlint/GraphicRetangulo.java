package com.nickmafra.onlint;

import com.nickmafra.gfx.MouseActionListenerAdapter;
import com.nickmafra.gfx.SimpleGraphic;
import com.nickmafra.onlint.io.ReadThread;
import com.nickmafra.onlint.io.ServerUpdateSender;
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
        addMouseActionListener(new GraphicRetangulo.MouseActionListenerImpl(this, state, updateSender));
        addOnClosingListener(e -> readThread.interrupt());
    }

    private void draw(Graphics2D g) {
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, getDimension().width, getDimension().height);

        g.setColor(Color.RED);
        g.fillRect(state.getObjX(), state.getObjY(), state.getObjWidth(), state.getObjHeight());
    }


    public static class MouseActionListenerImpl extends MouseActionListenerAdapter {

        private final GraphicRetangulo graphicRetangulo;
        private final RetanguloClientState state;
        private final ServerUpdateSender updateSender;

        public MouseActionListenerImpl(GraphicRetangulo graphicRetangulo, RetanguloClientState state, ServerUpdateSender updateSender) {
            this.graphicRetangulo = graphicRetangulo;
            this.state = state;
            this.updateSender = updateSender;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            log.debug("Pressed at {}, {}", x, y);

            if (state.pegaObjeto(x, y)) {
                try {
                    this.updateSender.sendUpdate();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    graphicRetangulo.close();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (state.arrastaObjeto(x, y)) {
                try {
                    this.updateSender.sendUpdate();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    graphicRetangulo.close();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            state.soltaObjeto();
        }
    }
}
