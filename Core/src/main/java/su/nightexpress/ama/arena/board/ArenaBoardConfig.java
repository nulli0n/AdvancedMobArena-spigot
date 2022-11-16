package su.nightexpress.ama.arena.board;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JWriter;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ArenaBoardConfig implements JWriter {

    private final String       id;
    private final String       title;
    private final List<String> lines;

    public ArenaBoardConfig(@NotNull String id, @NotNull String title, @NotNull List<String> lines) {
        this.id = id.toLowerCase();
        this.title = StringUtil.color(title);
        this.lines = StringUtil.color(lines);
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

    @Override
    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Title", this.getTitle());
        cfg.set(path + ".List", this.getLines());
    }
}
