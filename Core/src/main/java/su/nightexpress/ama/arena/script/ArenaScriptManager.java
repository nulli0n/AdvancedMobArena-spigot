package su.nightexpress.ama.arena.script;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.arena.editor.script.ScriptsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;

import java.util.*;

public class ArenaScriptManager implements ArenaChild, ILoadable, IEditable {

    public static final String DIR_SCRIPTS = "/scripts/";

    private final ArenaConfig                 arenaConfig;
    private final Map<String, ScriptCategory> categories;

    private ScriptsEditor editor;

    public ArenaScriptManager(@NotNull ArenaConfig arenaConfig) {
        this.arenaConfig = arenaConfig;
        this.categories = new HashMap<>();
    }

    @Override
    public void setup() {
        for (JYML cfg : JYML.loadAll(this.getScriptsPath(), true)) {
            ScriptCategory category = new ScriptCategory(this.getArenaConfig(), cfg);
            if (category.load()) {
                this.getCategoryMap().put(category.getId(), category);
            }
            else this.plugin().error("Script category not loaded: '" + cfg.getFile().getName() + "' in '" + this.getArenaConfig().getId() + "' arena!");
        }

        this.plugin().info("Loaded " + this.getCategories().size() + " scripts for '" + this.getArenaConfig().getId() + "' arena!");
    }

    @Override
    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getCategories().forEach(ScriptCategory::clear);
        this.getCategoryMap().clear();
    }

    public void save() {
        this.getCategories().forEach(ConfigHolder::save);
    }

    @NotNull
    @Override
    public ScriptsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new ScriptsEditor(this);
        }
        return editor;
    }

    @NotNull
    public String getScriptsPath() {
        return this.arenaConfig.getFile().getParentFile().getAbsolutePath() + DIR_SCRIPTS;
    }

    @NotNull
    @Override
    public ArenaConfig getArenaConfig() {
        return arenaConfig;
    }

    @NotNull
    public Map<String, ScriptCategory> getCategoryMap() {
        return categories;
    }

    @NotNull
    public Collection<ScriptCategory> getCategories() {
        return this.getCategoryMap().values();
    }

    @NotNull
    public Optional<ScriptCategory> getCategory(@NotNull String id) {
        return Optional.ofNullable(this.getCategoryMap().get(id.toLowerCase()));
    }

    @NotNull
    public List<ArenaScript> getScripts() {
        return this.getCategories().stream().flatMap(map -> map.getScripts().stream()).toList();
    }

    public void addConverted(@NotNull ArenaScript script) {
        this.createCategory("from_game_triggers");

        this.getCategory("from_game_triggers").ifPresent(category -> {
            category.getScriptsMap().put(script.getId(), script);
            category.save();
        });
    }

    public boolean createCategory(@NotNull String id) {
        if (this.getCategory(id).isPresent()) return false;

        JYML cfg = new JYML(this.getScriptsPath(), id + ".yml");
        ScriptCategory category = new ScriptCategory(this.getArenaConfig(), cfg);
        this.getCategoryMap().put(category.getId(), category);
        return true;
    }

    public void deleteCategory(@NotNull ScriptCategory category) {
        if (category.getConfig().getFile().delete()) {
            category.clear();
            this.getCategoryMap().remove(category.getId());
        }
    }
}
