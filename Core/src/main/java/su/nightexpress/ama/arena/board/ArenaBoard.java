package su.nightexpress.ama.arena.board;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.ama.arena.impl.Arena;
import su.nightexpress.ama.arena.impl.ArenaPlayer;
import su.nightexpress.ama.hook.impl.ProtocolLibHook;

import java.util.*;

public class ArenaBoard {

    private final ArenaPlayer          arenaPlayer;
    private final Player               player;
    private final String               title;
    private final List<String>         lines;
    private final String               playerId;
    private final Map<Integer, String> scores;

    public ArenaBoard(@NotNull ArenaPlayer arenaPlayer, @NotNull ArenaBoardConfig boardConfig) {
        this.arenaPlayer = arenaPlayer;
        this.player = arenaPlayer.getPlayer();
        this.title = boardConfig.getTitle();
        this.lines = boardConfig.getLines();
        this.playerId = this.player.getUniqueId().toString().replace("-", "").substring(0, 15);
        this.scores = new HashMap<>();
    }

    @NotNull
    private String getPlayerIdentifier() {
        return this.playerId;
    }

    @NotNull
    private String getScoreIdentifier(int score) {
        return ChatColor.COLOR_CHAR + String.join(String.valueOf(ChatColor.COLOR_CHAR), String.valueOf(score).split(""));
    }

    public void create() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getModifier().writeDefaults();
        packet.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        packet.getIntegers().write(0, 0); // Mode 0: Created Scoreboard
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(this.getPlayerIdentifier())); // Display Name
        ProtocolLibHook.sendServerPacket(this.player, packet);

        packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, 1); // Position 1: Sidebar
        packet.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        ProtocolLibHook.sendServerPacket(this.player, packet);
    }

    public void remove() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getModifier().writeDefaults();
        packet.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        packet.getIntegers().write(0, 1); // Mode 1: Remove Scoreboard
        ProtocolLibHook.sendServerPacket(this.player, packet);

        this.scores.forEach((score, val) -> {
            PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet2.getModifier().writeDefaults();
            packet2.getStrings().write(0, this.getScoreIdentifier(score)); // Team Name
            packet2.getIntegers().write(0, 1); // Mode - remove team
            ProtocolLibHook.sendServerPacket(this.player, packet2);
        });
    }

    public void clear() {
        this.scores.forEach((score, val) -> {
            String scoreId = this.getScoreIdentifier(score);

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Team Name
            packet.getIntegers().write(0, 1); // Mode - remove team
            ProtocolLibHook.sendServerPacket(this.player, packet);

            packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Score Name
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE); // Action
            packet.getStrings().write(1, this.getPlayerIdentifier()); // Objective Name
            ProtocolLibHook.sendServerPacket(this.player, packet);
        });
        this.scores.clear();
    }

    public void update() {
        String title = this.title;
        List<String> lines = new ArrayList<>(this.lines);
        Arena arena = this.arenaPlayer.getArena();

        lines.replaceAll(line -> {
            if (EngineUtils.hasPlaceholderAPI()) {
                line = PlaceholderAPI.setPlaceholders(player, line);
            }
            line = arena.replacePlaceholders().apply(line);
            line = arenaPlayer.replacePlaceholders().apply(line);
            return line;
        });

        Map<Integer, String> scores = new HashMap<>();
        int index = lines.size();

        for (String line : lines) {
            scores.put(index--, line);
        }

        PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet2.getModifier().writeDefaults();
        packet2.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        packet2.getIntegers().write(0, 2); // Mode 2: Update Display Name
        packet2.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(title)); // Display Name
        ProtocolLibHook.sendServerPacket(this.player, packet2);

        scores.forEach((score, text) -> {
            String scoreId = this.getScoreIdentifier(score);

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Team Name

            Optional<InternalStructure> optStruct = packet.getOptionalStructures().read(0);
            if (optStruct.isPresent()) {
                InternalStructure structure = optStruct.get();
                structure.getChatComponents().write(0, WrappedChatComponent.fromText(scoreId)); // Display Name
                structure.getChatComponents().write(1, WrappedChatComponent.fromLegacyText(text/*.first*/)); // Prefix
                //struct.getChatComponents().write(2, WrappedChatComponent.fromLegacyText(splitText/*.second*/)); // Suffix

                packet.getOptionalStructures().write(0, Optional.of(structure));
            }

            // there's no need to create the team again if this line already exists
            if (this.scores.containsKey(score)) {
                packet.getIntegers().write(0, 2); // Mode - update team info
                ProtocolLibHook.sendServerPacket(this.player, packet);
                return;
            }

            packet.getIntegers().write(0, 0); // Mode - create team
            packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(scoreId)); // Entities
            ProtocolLibHook.sendServerPacket(this.player, packet);

            packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Score Name
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE); // Action
            packet.getStrings().write(1, this.getPlayerIdentifier()); // Objective Name
            packet.getIntegers().write(0, score); // Score Value
            ProtocolLibHook.sendServerPacket(this.player, packet);
        });

        this.scores.entrySet().stream().filter(e -> !scores.containsKey(e.getKey())).forEach(e -> {
            int score = e.getKey();
            String scoreId = this.getScoreIdentifier(score);

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Team Name
            packet.getIntegers().write(0, 1); // Mode - remove team
            ProtocolLibHook.sendServerPacket(this.player, packet);

            packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Score Name
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE); // Action
            packet.getStrings().write(1, this.getPlayerIdentifier()); // Objective Name
            ProtocolLibHook.sendServerPacket(this.player, packet);
        });

        this.scores.clear();
        this.scores.putAll(scores);
    }
}
