package ru.itis.dis403.client.ui;

import ru.itis.dis403.client.GameClient;
import ru.itis.dis403.common.Constants;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel(MainFrame frame, GameClient client) {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 50, 80));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 20, 20, 20);

        JLabel title = new JLabel("WORMS-LIKE");
        title.setFont(new Font("Arial", Font.BOLD, 42));
        title.setForeground(new Color(255, 200, 0));

        JLabel nickLabel = new JLabel("Ваш ник:");
        nickLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nickLabel.setForeground(Color.WHITE);

        JLabel ipLabel = new JLabel("IP:");
        ipLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        ipLabel.setForeground(Color.WHITE);


        JTextField nickField = new JTextField(15);
        nickField.setFont(new Font("Arial", Font.PLAIN, 16));
        nickField.setBackground(new Color(200, 220, 255));
        nickField.setForeground(Color.BLACK);


        JTextField ipField = new JTextField(15);
        ipField.setFont(new Font("Arial", Font.PLAIN, 16));
        ipField.setBackground(new Color(200, 220, 255));
        ipField.setForeground(Color.BLACK);
        ipField.setText(Constants.SERVER_HOST);




        JButton readyBtn = createStyledButton("ГОТОВ", new Color(50, 200, 50));


        //РАСПОЛОЖЕНИЕ ЭЛЕМЕНТОВ


        // Заголовок (строка 0, на 2 столбца)
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        add(title, c);

        // НИК (строка 1)
        c.gridy = 1;
        c.gridwidth = 1;
        add(nickLabel, c);

        c.gridx = 1;
        add(nickField, c);

        // IP (строка 2)
        c.gridy = 2;
        c.gridx = 0;
        add(ipLabel, c);

        c.gridx = 1;
        add(ipField, c);

        // Кнопка ГОТОВ (строка 3)
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        add(readyBtn, c);

        readyBtn.addActionListener(e -> {
            String nick = nickField.getText().trim();
            String ip = ipField.getText().trim();

            if (nick.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите ник!",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (ip.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите ip!",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
            Constants.SERVER_HOST = ip;
            client.setPlayerNickname(nick);
            client.sendReady();

            frame.showGame();
        });
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setBorder(BorderFactory.createRaisedBevelBorder());

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }
}
