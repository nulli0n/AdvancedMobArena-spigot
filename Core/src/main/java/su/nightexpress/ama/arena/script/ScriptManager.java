package su.nightexpress.ama.arena.script;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractConfigHolder;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.api.arena.ArenaChild;
import su.nightexpress.ama.api.arena.Inspectable;
import su.nightexpress.ama.api.arena.Report;
import su.nightexpress.ama.arena.editor.script.ScriptsEditor;
import su.nightexpress.ama.arena.impl.ArenaConfig;
import su.nightexpress.ama.arena.script.impl.ArenaScript;
import su.nightexpress.ama.arena.script.impl.ScriptCategory;

import java.util.*;

public class ScriptManager implements ArenaChild, Inspectable {

    public static final String DIR_SCRIPTS = "/scripts/";

    private final AMA         plugin;
    private final ArenaConfig arenaConfig;
    private final Map<String, ScriptCategory> categories;

    private ScriptsEditor editor;

    public ScriptManager(@NotNull ArenaConfig arenaConfig) {
        this.plugin = arenaConfig.plugin();
        this.arenaConfig = arenaConfig;
        this.categories = new HashMap<>();
    }

    public void setup() {
        for (JYML cfg : JYML.loadAll(this.getScriptsPath(), true)) {
            ScriptCategory category = new ScriptCategory(this.getArenaConfig(), cfg);
            if (category.load()) {
                this.getCategoryMap().put(category.getId(), category);
            }
            else this.plugin.error("Script category not loaded: '" + cfg.getFile().getName() + "' in '" + this.getArenaConfig().getId() + "' arena!");
        }

        this.plugin.info("Loaded " + this.getScripts().size() + " scripts for '" + this.getArenaConfig().getId() + "' arena!");
    }

    public void shutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getCategories().forEach(ScriptCategory::clear);
        this.getCategoryMap().clear();
    }

    public void save() {
        this.getCategories().forEach(AbstractConfigHolder::save);
    }

    @NotNull
    @Override
    public Report getReport() {
        Report report = new Report();

        if (this.getCategories().isEmpty() || this.getCategories().stream().allMatch(category -> category.getScripts().isEmpty())) {
            report.addProblem("No scripts created!");
        }
        else {
            this.getCategories().forEach(category -> {
                if (category.getScripts().isEmpty()) {
                    report.addWarn("No scripts in '" + category.getId() + "' category!");
                }
            });
        }


        return report;
    }

    @NotNull
    public ScriptsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new ScriptsEditor(this.plugin, this);
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
        return this.getCategories().stream()
            .sorted(Comparator.comparing(ScriptCategory::getPriority, Comparator.reverseOrder()))
            .flatMap(map -> map.getScripts().stream()).toList();
    }

    public boolean createCategory(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);

        if (this.getCategory(id).isPresent()) return false;

        JYML cfg = new JYML(this.getScriptsPath(), id + ".yml");
        ScriptCategory category = new ScriptCategory(this.getArenaConfig(), cfg);
        this.getCategoryMap().put(category.getId(), category);
        this.save();
        return true;
    }

    public void deleteCategory(@NotNull ScriptCategory category) {
        if (category.getConfig().getFile().delete()) {
            category.clear();
            this.getCategoryMap().remove(category.getId());
        }
    }
}
