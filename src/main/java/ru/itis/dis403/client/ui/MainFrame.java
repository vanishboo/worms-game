package ru.itis.dis403.client.ui;

import ru.itis.dis403.client.GameClient;
import ru.itis.dis403.client.net.ServerListener;
import ru.itis.dis403.common.Constants;

import javax.swing.*;
import java.awt.*;


public class MainFrame extends JFrame {

    // ============ UI КОМПОНЕНТЫ ============
    private CardLayout layout;
    private JPanel root;

    // ============ ПАНЕЛИ ============
    private MainMenuPanel menuPanel;
    private GamePanel gamePanel;


    // ============ КНОПКА РЕСТАРТА ============
    private JButton restartBtn;

    // ============ ДАННЫЕ ============
    private GameClient client;

    public MainFrame(GameClient client) {
        this.client = client;

        setTitle("Game");
        setSize(Constants.WIDTH, Constants.HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layout = new CardLayout();
        root = new JPanel(layout);

        // Создаём панели
        menuPanel = new MainMenuPanel(this, client);
        gamePanel = new GamePanel(client, this);

        // Запускаем слушатель сервера
        ServerListener listener = new ServerListener(client.getConnection().in, gamePanel);
        listener.start();

        // Добавляем панели в CardLayout
        root.add(menuPanel, "MENU");
        root.add(gamePanel, "GAME");

        add(root);

        // Создаём кнопку рестарта (поверх всех панелей)
        createRestartButton();

        showMenu();
    }


    private void createRestartButton() {
        int btnWidth = 250;
        int btnHeight = 60;
        int btnX = (Constants.WIDTH - btnWidth) / 2;
        int btnY = Constants.HEIGHT / 2 + 100;

        restartBtn = new JButton("ИГРАТЬ СНОВА");
        restartBtn.setBounds(btnX, btnY, btnWidth, btnHeight);

        // Стиль кнопки
        restartBtn.setBackground(new Color(50, 150, 50));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setFont(new Font("Arial", Font.BOLD, 22));
        restartBtn.setFocusPainted(false);
        restartBtn.setBorderPainted(false);

        // Hover эффект
        restartBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                restartBtn.setBackground(new Color(70, 200, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                restartBtn.setBackground(new Color(50, 150, 50));
            }
        });

        // Обработчик клика - перезапуск игры
        restartBtn.addActionListener(e -> restart());

        restartBtn.setVisible(false);

        // Добавляем в LayeredPane поверх всего
        getLayeredPane().add(restartBtn, JLayeredPane.POPUP_LAYER);
    }

    /**
     * Показывает главное меню.
     */
    public void showMenu() {
        restartBtn.setVisible(false); // Прячем кнопку рестарта
        layout.show(root, "MENU");
    }

    /**
     * Показывает игровой экран.
     */
    public void showGame() {
        restartBtn.setVisible(false); // Прячем кнопку рестарта
        layout.show(root, "GAME");
        gamePanel.requestFocusInWindow();
    }


    /**
     * Перезапускает игру (возвращает в главное меню).
     * Закрывает старое соединение и пересоздаёт клиента.
     */
    public void restart() {
        System.out.println("Перезапуск игры");

        try {
            // Закрываем старое соединение
            if (client != null && client.getConnection() != null) {
                client.getConnection().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            client.reconnect();

            menuPanel = new MainMenuPanel(this, client);
            gamePanel = new GamePanel(client, this);
            ServerListener listener = new ServerListener(client.getConnection().in, gamePanel);
            listener.start();
            root.removeAll();
            root.add(gamePanel, "GAME");
            root.add(menuPanel, "MENU");

            showMenu();
        }  catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка подключения к серверу");
        }
        // Возвращаемся в меню
        showMenu();

    }

    /**
     * от GamePanel при завершении игры.
     * Показывает кнопку "ИГРАТЬ СНОВА".
     */
    public void onGameOver(String message) {
        System.out.println("Игра окончена: " + message);
        restartBtn.setVisible(true);
    }
}
