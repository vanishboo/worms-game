package ru.itis.dis403.client.ui.input;

import ru.itis.dis403.client.net.ClientConnection;
import ru.itis.dis403.common.WeaponType;
import ru.itis.dis403.common.protocol.MessageType;
import ru.itis.dis403.common.protocol.Packet;
import ru.itis.dis403.common.protocol.PacketIO;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyboardHandler implements KeyListener {

    private final ClientConnection connection;
    private final WeaponChangeCallback weaponChangeCallback;


    public KeyboardHandler(ClientConnection connection, WeaponChangeCallback weaponChangeCallback) {
        this.connection = connection;
        this.weaponChangeCallback = weaponChangeCallback;
    }


    @Override
    public void keyPressed(KeyEvent e) {
        try {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_A:
                case KeyEvent.VK_LEFT:
                    PacketIO.write(connection.out,
                            new Packet(MessageType.MOVE, "LEFT"));
                    break;

                case KeyEvent.VK_D:
                case KeyEvent.VK_RIGHT:
                    PacketIO.write(connection.out,
                            new Packet(MessageType.MOVE, "RIGHT"));
                    break;

                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_W:
                case KeyEvent.VK_UP:
                    PacketIO.write(connection.out,
                            new Packet(MessageType.JUMP, ""));
                    break;

                case KeyEvent.VK_1:
                    selectWeapon(WeaponType.PISTOL);
                    break;
                case KeyEvent.VK_2:
                    selectWeapon(WeaponType.RIFLE);
                    break;
                case KeyEvent.VK_3:
                    selectWeapon(WeaponType.BAZOOKA);
                    break;
                case KeyEvent.VK_4:
                    selectWeapon(WeaponType.GRENADE);
                    break;
                case KeyEvent.VK_5:
                    selectWeapon(WeaponType.AIRSTRIKE);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        try {
            if (e.getKeyCode() == KeyEvent.VK_A ||
                    e.getKeyCode() == KeyEvent.VK_LEFT ||
                    e.getKeyCode() == KeyEvent.VK_D ||
                    e.getKeyCode() == KeyEvent.VK_RIGHT) {

                PacketIO.write(connection.out,
                        new Packet(MessageType.MOVE_STOP, ""));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }


    private void selectWeapon(WeaponType weapon) {
        try {
            PacketIO.write(connection.out,
                    new Packet(MessageType.SELECT_WEAPON, weapon.name()));
            weaponChangeCallback.onWeaponChanged(weapon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface WeaponChangeCallback {
        void onWeaponChanged(WeaponType weapon);
    }
}
