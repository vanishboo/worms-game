    package ru.itis.dis403.server.manager;

    import lombok.Getter;
    import ru.itis.dis403.common.MapType;
    import ru.itis.dis403.common.protocol.MessageType;
    import ru.itis.dis403.common.protocol.Packet;
    import ru.itis.dis403.server.model.Arena;

    import java.util.HashMap;
    import java.util.Map;
    import java.util.function.Consumer;


    public class MapVotingManager {

        private final Arena arena;
        private final Consumer<Packet> broadcastFunc;

        private final Map<Integer, MapType> votes = new HashMap<>();

        @Getter
        private boolean votingComplete = false;

        public MapVotingManager(Arena arena,
                                Consumer<Packet> broadcastFunc) {
            this.arena = arena;
            this.broadcastFunc = broadcastFunc;
        }


        public void handleVote(int playerId, String mapName) {
            if (votingComplete) return;

            try {
                MapType mapType = MapType.valueOf(mapName);
                votes.put(playerId, mapType);
                System.out.println("🗳Игрок " + playerId + " проголосовал за: " + mapType.displayName);

                // Если оба проголосовали → выбираем карту
                if (votes.size() == 2) {
                    selectMap();
                }

            } catch (IllegalArgumentException e) {
                System.err.println("Неизвестная карта: " + mapName);
            }
        }


        private void selectMap() {
            votingComplete = true;

            MapType vote1 = votes.get(1);
            MapType vote2 = votes.get(2);

            // Если оба выбрали одну - она, иначе случайная
            MapType selected = (vote1 == vote2) ? vote1 :
                    (Math.random() > 0.5 ? vote1 : vote2);

            System.out.println("Выбрана карта: " + selected.displayName);

            // Загружаем карту
            MapPresets.loadMap(arena, selected);

            // ОДНО СООБЩЕНИЕ - ФИНАЛЬНЫЙ ВЫБОР
            broadcastFunc.accept(new Packet(MessageType.MAP_SELECTED, selected.name()));
        }

        public void reset() {
            votes.clear();
            votingComplete = false;
        }

    }
