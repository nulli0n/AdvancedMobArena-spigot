package su.nightexpress.ama.arena.board;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;

import java.util.ArrayList;
import java.util.List;

public class ArenaBoardConfig {

    private final String       id;
    private final String       title;
    private final List<String> lines;

    public ArenaBoardConfig(@NotNull String id, @NotNull String title, @NotNull List<String> lines) {
        this.id = id.toLowerCase();
        this.title = Colorizer.apply(title);
        this.lines = Colorizer.apply(lines);
    }

    @NotNull
    public static ArenaBoardConfig read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        String title = cfg.getString(path +  ".Title", "");
        List<String> lines = cfg.getStringList(path + ".List");
        return new ArenaBoardConfig(id, title, lines);
    }

    public static void write(@NotNull ArenaBoardConfig config, @NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Title", config.getTitle());
        cfg.set(path + ".List", config.getLines());
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    @NotNull
    public List<String> getLines() {
        return new ArrayList<>(lines);
    }
}
