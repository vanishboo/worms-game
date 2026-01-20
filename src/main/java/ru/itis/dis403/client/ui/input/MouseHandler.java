package ru.itis.dis403.client.ui.input;

import lombok.Getter;
import lombok.Setter;
import ru.itis.dis403.client.model.GameStateView;
import ru.itis.dis403.client.net.ClientConnection;
import ru.itis.dis403.client.ui.GamePanel;
import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.common.protocol.MessageType;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


@Getter
@Setter
public class MouseHandler implements MouseListener, MouseMotionListener {
    private final ClientConnection connection;

    private final GameStateView state;

    private int myPlayerId;

    private WeaponType currentWeapon;

    private Point mouse = new Point();

    private Point lockedMouse;

    private boolean charging = false;

    private long chargeStart;

    private final MouseStateCallback stateCallback;


    public MouseHandler(ClientConnection connection, GameStateView state,
                        int myPlayerId, MouseStateCallback stateCallback) {
        this.connection = connection;
        this.state = state;
        this.myPlayerId = myPlayerId;
        this.stateCallback = stateCallback;
    }



    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        if (state.getCurrentTurn() != myPlayerId) return;

        lockedMouse = new Point(e.getX(), e.getY());
        charging = true;
        chargeStart = System.currentTimeMillis();

        stateCallback.onChargingStarted(lockedMouse, chargeStart);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        if (!charging) return;
        if (state.getCurrentTurn() != myPlayerId) return;

        charging = false;
        stateCallback.onChargingStopped();

        if (currentWeapon == WeaponType.AIRSTRIKE) {
            double targetX = lockedMouse.x;
            String payload = targetX + " 0 1.0"; // Y и power не используются
            try {
                PacketIO.write(connection.out, new Packet(MessageType.SHOOT, payload));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
        double playerX = (myPlayerId == 1) ?
                state.getP1().getX() + 20 : state.getP2().getX() + 20;
        double playerY = (myPlayerId == 1) ?
                state.getP1().getY() - 20 : state.getP2().getY() - 20;

        double dx = lockedMouse.x - playerX;
        double dy = lockedMouse.y - playerY;

        long chargeTime = System.currentTimeMillis() - chargeStart;
        double power = Math.min(1.0, chargeTime / 2000.0);

        try {
            String payload = dx + " " + dy + " " + power;
            PacketIO.write(connection.out,
                    new Packet(MessageType.SHOOT, payload));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouse.x = e.getX();
        mouse.y = e.getY();
        stateCallback.onMouseMoved(mouse);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouse.x = e.getX();
        mouse.y = e.getY();
        stateCallback.onMouseMoved(mouse);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }




    public interface MouseStateCallback {
        void onMouseMoved(Point mouse);
        void onChargingStarted(Point lockedMouse, long chargeStart);
        void onChargingStopped();
    }
}
