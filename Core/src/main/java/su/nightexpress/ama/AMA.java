package su.nightexpress.ama;

import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.Version;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.command.list.ReloadSubCommand;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.ama.api.arena.type.ArenaTargetType;
import su.nightexpress.ama.api.hologram.HologramType;
import su.nightexpress.ama.api.hologram.IHologramHandler;
import su.nightexpress.ama.api.type.GameEventType;
import su.nightexpress.ama.api.type.GameState;
import su.nightexpress.ama.arena.ArenaManager;
import su.nightexpress.ama.arena.lock.LockState;
import su.nightexpress.ama.arena.setup.ArenaSetupManager;
import su.nightexpress.ama.command.*;
import su.nightexpress.ama.command.kit.KitCommand;
import su.nightexpress.ama.command.score.ScoreCommand;
import su.nightexpress.ama.config.Config;
import su.nightexpress.ama.config.Lang;
import su.nightexpress.ama.currency.CurrencyManager;
import su.nightexpress.ama.data.DataHandler;
import su.nightexpress.ama.data.UserManager;
import su.nightexpress.ama.data.impl.ArenaUser;
import su.nightexpress.ama.editor.EditorHub;
import su.nightexpress.ama.editor.EditorLocales;
import su.nightexpress.ama.hologram.HologramManager;
import su.nightexpress.ama.hologram.handler.HologramDecentHandler;
import su.nightexpress.ama.hologram.handler.HologramDisplaysHandler;
import su.nightexpress.ama.hook.HookId;
import su.nightexpress.ama.hook.impl.McMMOHook;
import su.nightexpress.ama.hook.impl.PlaceholderHook;
import su.nightexpress.ama.hook.level.PluginLevelProvider;
import su.nightexpress.ama.hook.level.impl.MMOCorePlayerLevelProvider;
import su.nightexpress.ama.hook.mob.PluginMobProvider;
import su.nightexpress.ama.hook.mob.impl.BossManiaProvider;
import su.nightexpress.ama.hook.mob.impl.EliteMobsProvider;
import su.nightexpress.ama.hook.mob.impl.MythicMobProvider;
import su.nightexpress.ama.hook.pet.PluginPetProvider;
import su.nightexpress.ama.hook.pet.impl.AdvancedPetsProvider;
import su.nightexpress.ama.hook.pet.impl.CombatPetsProvider;
import su.nightexpress.ama.hook.pet.impl.MCPetsProvider;
import su.nightexpress.ama.kit.KitManager;
import su.nightexpress.ama.mob.MobManager;
import su.nightexpress.ama.mob.style.MobStyleType;
import su.nightexpress.ama.nms.ArenaNMS;
import su.nightexpress.ama.nms.v1_19_R3.V1_19_R3;
import su.nightexpress.ama.nms.v1_20_R2.V1_20_R2;
import su.nightexpress.ama.nms.v1_20_R3.V1_20_R3;
import su.nightexpress.ama.sign.SignManager;
import su.nightexpress.ama.stats.StatsManager;
import su.nightexpress.ama.stats.object.StatType;

public class AMA extends NexPlugin<AMA> implements UserDataHolder<AMA, ArenaUser> {

    private DataHandler userData;
    private UserManager userManager;

    private CurrencyManager   currencyManager;
    private ArenaManager      arenaManager;
    private ArenaSetupManager arenaSetupManager;
    private MobManager        mobManager;
    private KitManager        kitManager;
    private StatsManager      statsManager;
    private EditorHub         editorHub;
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
        switch (Version.getCurrent()) {
            case V1_19_R3 -> this.arenaNMS = new V1_19_R3();
            case V1_20_R2 -> this.arenaNMS = new V1_20_R2();
            case V1_20_R3 -> this.arenaNMS = new V1_20_R3();
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
            this.warn("No currencies are enabled/available! Cost features will not work.");
        }

        if (Config.HOLOGRAMS_ENABLED.get()) {
            IHologramHandler<?> hologramHandler = null;
            if (EngineUtils.hasPlugin(HookId.HOLOGRAPHIC_DISPLAYS)) {
                hologramHandler = new HologramDisplaysHandler();
            }
            else if (EngineUtils.hasPlugin(HookId.DECENT_HOLOGRAMS)) {
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
        PluginLevelProvider.getProvidersMap().clear();
        PluginMobProvider.getProvidersMap().clear();
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadEditor(EditorLocales.class);
        this.getLangManager().loadEnum(GameState.class);
        this.getLangManager().loadEnum(LockState.class);
        this.getLangManager().loadEnum(GameEventType.class);
        this.getLangManager().loadEnum(ArenaTargetType.class);
        this.getLangManager().loadEnum(StatType.class);
        this.getLangManager().loadEnum(HologramType.class);
        this.getLangManager().loadEnum(MobStyleType.class);

        // TODO Stupid workaround for attribute 'GENERIC_' prefix.
        for (Attribute attribute : Attribute.values()) {
            this.getLang().addMissing("Attribute." + attribute.name(), StringUtil.capitalizeUnderscored(attribute.name().replace("GENERIC_", "")));
        }

        this.getLang().saveChanges();
    }

    @Override
    public void registerHooks() {
        if (EngineUtils.hasPlugin(HookId.MCMMO)) {
            McMMOHook.setup();
        }
        if (EngineUtils.hasPlaceholderAPI()) {
            PlaceholderHook.setup(this);
        }
        if (EngineUtils.hasPlugin(HookId.MMOCORE)) {
            PluginLevelProvider.registerProvider(new MMOCorePlayerLevelProvider());
        }
        if (EngineUtils.hasPlugin(HookId.MYTHIC_MOBS)) {
            PluginMobProvider.registerProvider(new MythicMobProvider());
        }
        if (EngineUtils.hasPlugin(HookId.ELITE_MOBS)) {
            PluginMobProvider.registerProvider(new EliteMobsProvider());
        }
        if (EngineUtils.hasPlugin(HookId.BOSS_MANIA)) {
            PluginMobProvider.registerProvider(new BossManiaProvider());
        }
        if (EngineUtils.hasPlugin(HookId.ADVANCED_PETS)) {
            PluginPetProvider.registerProvider(new AdvancedPetsProvider());
        }
        if (EngineUtils.hasPlugin(HookId.MC_PETS)) {
            PluginPetProvider.registerProvider(new MCPetsProvider());
        }
        if (EngineUtils.hasPlugin(HookId.COMBAT_PETS)) {
            PluginPetProvider.registerProvider(new CombatPetsProvider());
        }
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<AMA> mainCommand) {
        mainCommand.addChildren(new EditorCommand(this));
        mainCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
        mainCommand.addChildren(new ForceEndCommand(this));
        mainCommand.addChildren(new ForceStartCommand(this));
        mainCommand.addChildren(new JoinCommand(this));
        mainCommand.addChildren(new KitCommand(this));
        mainCommand.addChildren(new LeaveCommand(this));
        mainCommand.addChildren(new ListCmd(this));
        mainCommand.addChildren(new RegionCommand(this));
        mainCommand.addChildren(new SetActiveCommand(this));
        mainCommand.addChildren(new ScoreCommand(this));
        mainCommand.addChildren(new ShopCommand(this));
        mainCommand.addChildren(new SkipRoundCommand(this));
        mainCommand.addChildren(new SpectateCommand(this));
        mainCommand.addChildren(new SpotCommand(this));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @Override
    public boolean setupDataHandlers() {
        this.userData = DataHandler.getInstance(this);
        this.userData.setup();

        this.userManager = new UserManager(this);
        this.userManager.setup();
        return true;
    }

    @Override
    @NotNull
    public DataHandler getData() {
        return this.userData;
    }

    @NotNull
    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @NotNull
    public EditorHub getEditor() {
        if (this.editorHub == null) {
            this.editorHub = new EditorHub(this);
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
