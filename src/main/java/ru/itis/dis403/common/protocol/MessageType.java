package ru.itis.dis403.common.protocol;

public class MessageType {

    // подключение
    public static final byte ASSIGN_ID = 1;
    public static final byte LOBBY_WAIT = 12;
    public static final byte REGISTER_PLAYER = 11;

    // управление игрой
    public static final byte PLAYER_READY = 14;
    public static final byte START_TURN = 8;
    public static final byte END_GAME = 7;

    // синхронизация
    public static final byte UPDATE = 3;
    public static final byte OBJECTS_UPDATE = 16;

    // карта
    public static final byte INIT_MAP = 2;
    public static final byte INIT_OBJECTS = 15;
    public static final byte VOTE_MAP = 17;
    public static final byte MAP_SELECTED = 18;
    public static final byte MAP_UPDATE = 13;

    // действия игрока
    public static final byte SELECT_WEAPON = 10;
    public static final byte MOVE = 4;
    public static final byte JUMP = 5;
    public static final byte SHOOT = 6;
    public static final byte MOVE_STOP = 9;

    // статистика
    public static final byte LEADERBOARD_REQUEST = 19;
    public static final byte LEADERBOARD_RESPONSE = 20;

}
