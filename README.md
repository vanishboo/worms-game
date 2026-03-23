<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&height=220&color=0:4facfe,100:00f2fe&text=WORMS-LIKE&fontSize=54&fontAlignY=40&desc=Java%20Client%2FServer%20Turn-Based%20Artillery%20Game&descAlignY=60&animation=fadeIn" alt="WORMS-LIKE banner" />

<img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=20&duration=2200&pause=700&center=true&vCenter=true&repeat=true&width=900&lines=%F0%9F%8E%AE+2D+Worms-style+PvP;%F0%9F%8C%8D+TCP+Client%2FServer;%F0%9F%A7%A8+Destructible+Terrain+%26+Platforms;%F0%9F%92%A5+Bazooka%2C+Grenade%2C+Airstrike;%F0%9F%8F%86+Persistent+Leaderboard" alt="Typing animation" />

<br/>

![Java](https://img.shields.io/badge/Java-23-ff6b6b?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-1565c0?style=for-the-badge&logo=apachemaven&logoColor=white)
![UI](https://img.shields.io/badge/UI-Swing%2FAWT-2ecc71?style=for-the-badge)
![Network](https://img.shields.io/badge/Transport-TCP-f39c12?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Playable-8e44ad?style=for-the-badge)

</div>

---

## ✨ Что это

**WORMS-GAME** — это пошаговая PvP-игра на 2 игроков в духе Worms:

- подключение клиентов к серверу по TCP;
- выбор карты голосованием перед матчем;
- смена ходов с ограничением времени;
- разрушаемый рельеф + платформы;
- несколько видов оружия и кулдауны;
- таблица лидеров, которая сохраняется между матчами.

---



## 🧠 Core gameplay loop

```mermaid
flowchart LR
  A[Server стартует] --> B[Подключение 2 игроков]
  B --> C[Ник + READY]
  C --> D[Голосование за карту]
  D --> E[Инициализация арены]
  E --> F[Пошаговый бой]
  F --> G[Победитель определён]
  G --> H[Сохранение статистики]
  H --> B
```

---

## 🎮 Управление

| Действие | Клавиши / мышь |
|---|---|
| Движение | `A/D` или `←/→` |
| Прыжок | `W`, `↑` или `Space` |
| Смена оружия | `1..5` |
| Выстрел | Зажать ЛКМ (заряд) → отпустить ЛКМ |

Для `AIRSTRIKE` при выстреле используется целевая позиция по X.

---

## 🛠️ Оружие

| Weapon | Название |
|---|---|
| `PISTOL` | Пистолет |
| `RIFLE` | Автомат |
| `BAZOOKA` | Базука | 
| `GRENADE` | Граната | 
| `AIRSTRIKE` | Воздушный удар | 

> Параметры урона/скорости/кд задаются на стороне сервера в enum оружия.

---

## 🗺️ Карты

Игроки голосуют за одну из трёх карт:

- `CLASSIC_ARENA`
- `DESERT_CANYON`
- `ICE_FORTRESS`

Если голоса разные — сервер выбирает одну из двух случайно.

---

## 🧱 Архитектура

```text
src/main/java/ru/itis/dis403
├── client
│   ├── GameClient                 # входная точка клиента
│   ├── net/                       # сокеты + серверный listener
│   ├── model/                     # view-модели
│   └── ui/                        # Swing: меню, панель игры, рендеры, input
├── common
│   ├── protocol/                  # Packet, PacketIO, MessageType
│   ├── WeaponType / MapType       # enum игрового домена
│   └── Constants                  # host/port/размеры/физика
└── server
    ├── GameServer                 # входная точка сервера
    ├── GameEngine                 # главный цикл + обработка пакетов
    ├── manager/                   # ходы, голосование, сериализация
    ├── physics/                   # физика игроков/пуль/снарядов
    ├── model/                     # сущности сервера
    └── storage/                   # персистентная статистика игроков
```

---

## 🔌 Сетевой протокол

### Формат пакета

- `byte type`
- `int payloadLength`
- `byte[] payload (UTF-8)`

### Группы сообщений

- **Лобби/подключение**: `ASSIGN_ID`, `LOBBY_WAIT`, `REGISTER_PLAYER`, `PLAYER_READY`
- **Матч**: `START_TURN`, `UPDATE`, `END_GAME`
- **Карта**: `VOTE_MAP`, `MAP_SELECTED`, `INIT_MAP`, `MAP_UPDATE`, `INIT_OBJECTS`, `OBJECTS_UPDATE`
- **Действия**: `MOVE`, `MOVE_STOP`, `JUMP`, `SELECT_WEAPON`, `SHOOT`
- **Статистика**: `LEADERBOARD_REQUEST`, `LEADERBOARD_RESPONSE`

---


## 🏆 Статистика и лидерборд

Сервер сохраняет статы в файл:

```text
server_players_stats.json
```

Сохраняются:

- победы / поражения;
- общее число выстрелов;
- попадания и точность;
- сортировка игроков по победам.

---


## 🛣️ Roadmap

- [ ] Матчмейкинг / комнаты
- [ ] Боты
- [ ] Replay матчей
- [ ] Больше оружия и модификаторов
- [ ] Docker + CI

---

<div align="center">



</div>
