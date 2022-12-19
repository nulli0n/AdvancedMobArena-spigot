package su.nightexpress.ama;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.api.editor.EditorHolder;
import su.nexmedia.engine.command.list.EditorSubCommand;
import su.nexmedia.engine.command.list.ReloadSubCommand;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.external.citizens.CitizensHook;
import su.nightexpress.ama.api.arena.type.ArenaGameEventType;
import su.nightexpress.ama.api.arena.type.ArenaLockState;
import su.nightexpress.ama.api.arena.type.ArenaState;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.api.hologram.IHologramHandler;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.setup.ArenaSetupManager;
import su.nightexpress.ama.arena.setup.SetupItemType;
import su.nightexpress.ama.command.*;
import su.nightexpress.ama.command.currency.CurrencyMainCommand;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.currency.CurrencyManager;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.data.ArenaUserData;
import su.nightexpress.ama.data.ArenaUserManager;
import su.nightexpress.ama.editor.ArenaEditorHub;
import su.nightexpress.ama.editor.ArenaEditorType;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.hologram.handler.HologramDecentHandler;
import su.nightexpress.ama.hologram.handler.HologramDisplaysHandler;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.external.*;
import su.nightexpress.ama.hook.external.traits.*;
import su.nightexpress.ama.kit.KitManager;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.style.MobStyleType;
import su.nightexpress.ama.nms.ArenaNMS;
import su.nightexpress.ama.nms.v1_17_R1.V1_17_R1;
import su.nightexpress.ama.nms.v1_18_R2.V1_18_R2;
import su.nightexpress.ama.nms.v1_19_R1.V1_19_R1;
import su.nightexpress.ama.nms.v1_19_R2.V1_19_R2;
import su.nightexpress.ama.sign.SignManager;
import su.nightexpress.ama.stats.StatsManager;
import su.nightexpress.ama.stats.object.StatType;

import java.sql.SQLException;

public class AMA extends NexPlugin<AMA> implements UserDataHolder<AMA, ArenaUser>, EditorHolder<AMA, ArenaEditorType> {

    private ArenaUserData    userData;
    private ArenaUserManager userManager;

    private CurrencyManager   currencyManager;
    private ArenaManager      arenaManager;
    private ArenaSetupManager arenaSetupManager;
    private MobManager        mobManager;
    private KitManager        kitManager;
    private StatsManager      statsManager;
    private ArenaEditorHub    editorHub;
    private SignManager       signManager;
    private HologramManager   hologramManager;

    private ArenaNMS arenaNMS;

    @Override
    @NotNull
    protected AMA getSelf() {
        return this;
    }

    @Override
    public void enable() {
        switch (Version.CURRENT) {
            case V1_17_R1 -> this.arenaNMS = new V1_17_R1();
            case V1_18_R2 -> this.arenaNMS = new V1_18_R2();
            case V1_19_R1 -> this.arenaNMS = new V1_19_R1();
            case V1_19_R2 -> this.arenaNMS = new V1_19_R2();
            default -> { }
        }
        if (this.arenaNMS == null) {
            this.error("Could not setup NMS interface! (Unsupported server version)");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        this.currencyManager = new CurrencyManager(this);
        this.currencyManager.setup();
        if (!this.getCurrencyManager().hasCurrency()) {
            this.error("No currencies are enabled/available! Plugin will be disabled.");
            this.getPluginManager().disablePlugin(this);
            return;
        }

        if (Config.HOLOGRAMS_ENABLED.get()) {
            IHologramHandler<?> hologramHandler = null;
            if (Hooks.hasPlugin(HookId.HOLOGRAPHIC_DISPLAYS)) {
                hologramHandler = new HologramDisplaysHandler();
            }
            else if (Hooks.hasPlugin(HookId.DECENT_HOLOGRAMS)) {
                hologramHandler = new HologramDecentHandler();
            }
            if (hologramHandler != null) {
                this.info("Found compatible holograms plugin! Let's use hologram features.");
                this.hologramManager = new HologramManager(this, hologramHandler);
                this.hologramManager.setup();
            }
            else {
                this.warn("Can not found a compatible holograms plugin. Holograms feature will be disabled.");
            }
        }

        this.mobManager = new MobManager(this);
        this.mobManager.setup();

        this.kitManager = new KitManager(this);
        this.kitManager.setup();

        this.arenaManager = new ArenaManager(this);
        this.arenaManager.setup();

        this.arenaSetupManager = new ArenaSetupManager(this);
        this.arenaSetupManager.setup();

        this.statsManager = new StatsManager(this);
        this.statsManager.setup();

        if (Config.SIGNS_ENABLED.get()) {
            this.signManager = new SignManager(this);
            this.signManager.setup();
        }
    }

    @Override
    public void disable() {
        if (this.hologramManager != null) {
            this.hologramManager.shutdown();
            this.hologramManager = null;
        }
        if (this.editorHub != null) {
            this.editorHub.clear();
            this.editorHub = null;
        }
        if (this.arenaSetupManager != null) {
            this.arenaSetupManager.shutdown();
            this.arenaSetupManager = null;
        }
        if (this.arenaManager != null) {
            this.arenaManager.shutdown();
            this.arenaManager = null;
        }
        if (this.mobManager != null) {
            this.mobManager.shutdown();
            this.mobManager = null;
        }
        if (this.kitManager != null) {
            this.kitManager.shutdown();
            this.kitManager = null;
        }
        if (this.statsManager != null) {
            this.statsManager.shutdown();
            this.statsManager = null;
        }
        if (this.currencyManager != null) {
            this.currencyManager.shutdown();
            this.currencyManager = null;
        }
        if (this.signManager != null) {
            this.signManager.shutdown();
            this.signManager = null;
        }
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().setupEnum(ArenaState.class);
        this.getLangManager().setupEnum(ArenaLockState.class);
        this.getLangManager().setupEnum(ArenaGameEventType.class);
        this.getLangManager().setupEnum(ArenaTargetType.class);
        this.getLangManager().setupEnum(StatType.class);
        this.getLangManager().setupEnum(HologramType.class);
        this.getLangManager().setupEnum(MobStyleType.class);
        this.getLangManager().setupEditorEnum(ArenaEditorType.class);
        this.getLangManager().setupEditorEnum(SetupItemType.class);
        this.getLang().saveChanges();
    }

    @Override
    public void registerHooks() {
        this.registerHook(HookId.ESSENTIALS, EssentialsHook.class);
        this.registerHook(HookId.MCMMO, McMMOHook.class);
        if (Hooks.hasPlaceholderAPI()) {
            this.registerHook(Hooks.PLACEHOLDER_API, PlaceholderHook.class);
        }
        this.registerHook(HookId.SUNLIGHT, SunLightHook.class);
        this.registerHook(HookId.MAGIC, MagicHK.class);

        if (Hooks.hasCitizens()) {
            CitizensHook.registerTrait(this, ArenasTrait.class);
            CitizensHook.registerTrait(this, KitSelectorTrait.class);
            CitizensHook.registerTrait(this, KitShopTrait.class);
            CitizensHook.registerTrait(this, ShopTrait.class);
            CitizensHook.registerTrait(this, StatsTrait.class);
        }
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<AMA> mainCommand) {
        mainCommand.addChildren(new CurrencyMainCommand(this));
        mainCommand.addChildren(new EditorSubCommand<>(this, this, Perms.COMMAND_EDITOR));
        mainCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
        mainCommand.addChildren(new ForceEndCommand(this));
        mainCommand.addChildren(new ForceStartCommand(this));
        mainCommand.addChildren(new JoinCommand(this));
        mainCommand.addChildren(new LeaveCommand(this));
        mainCommand.addChildren(new ListCmd(this));
        mainCommand.addChildren(new RegionCommand(this));
        mainCommand.addChildren(new ScoreCmd(this));
        mainCommand.addChildren(new ShopCommand(this));
        mainCommand.addChildren(new SkipwaveCmd(this));
        mainCommand.addChildren(new SpectateCmd(this));
        mainCommand.addChildren(new SpotCmd(this));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @Override
    public boolean setupDataHandlers() {
        try {
            this.userData = ArenaUserData.getInstance(this);
            this.userData.setup();
        }
        catch (SQLException e) {
            this.error("Could not setup database handler!");
            e.printStackTrace();
            return false;
        }

        this.userManager = new ArenaUserManager(this);
        this.userManager.setup();

        return true;
    }

    @Override
    @NotNull
    public ArenaUserData getData() {
        return this.userData;
    }

    @NotNull
    @Override
    public ArenaUserManager getUserManager() {
        return userManager;
    }

    @Override
    @NotNull
    public ArenaEditorHub getEditor() {
        if (this.editorHub == null) {
            this.editorHub = new ArenaEditorHub(this);
        }
        return this.editorHub;
    }

    @NotNull
    public CurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }

    @NotNull
    public ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    @NotNull
    public ArenaSetupManager getArenaSetupManager() {
        return arenaSetupManager;
    }

    @NotNull
    public MobManager getMobManager() {
        return this.mobManager;
    }

    @NotNull
    public KitManager getKitManager() {
        return this.kitManager;
    }

    @NotNull
    public StatsManager getStatsManager() {
        return this.statsManager;
    }

    @Nullable
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    @Nullable
    public SignManager getSignManager() {
        return signManager;
    }

    @NotNull
    public ArenaNMS getArenaNMS() {
        return this.arenaNMS;
    }
}
