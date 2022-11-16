package su.nightexpress.ama.stats;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.manager.ConfigHolder;
import su.nexmedia.engine.api.task.AbstractTask;
import su.nexmedia.engine.hooks.Hooks;
import su.nightexpress.ama.AMA;
import su.nightexpress.ama.arena.AbstractArena;
import su.nightexpress.ama.data.ArenaUser;
import su.nightexpress.ama.sign.SignManager;
import su.nightexpress.ama.sign.type.SignType;
import su.nightexpress.ama.stats.command.StatsCommand;
import su.nightexpress.ama.stats.config.StatsConfig;
import su.nightexpress.ama.stats.menu.StatsMenu;
import su.nightexpress.ama.stats.object.StatType;
import su.nightexpress.ama.stats.object.StatsScore;

import java.util.*;
import java.util.stream.Stream;

public class StatsManager extends AbstractManager<AMA> implements ConfigHolder {

    private JYML config;

    private Map<StatType, Set<StatsScore>> statsScores;
    private StatsMenu                      statsMenu;
    private StatsTask                      statsTask;

    public StatsManager(@NotNull AMA plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        this.config = JYML.loadOrExtract(plugin, "/stats/settings.yml");
        this.config.initializeOptions(StatsConfig.class);

        this.statsMenu = new StatsMenu(plugin, JYML.loadOrExtract(plugin, "/stats/gui.stats.yml"));
        this.statsScores = new HashMap<>();

        this.plugin.getMainCommand().addChildren(new StatsCommand(this));

        this.statsTask = new StatsTask();
        this.statsTask.start();
    }

    @Override
    public void onShutdown() {
        this.getConfig().reload();
        if (this.statsTask != null) {
            this.statsTask.stop();
            this.statsTask = null;
        }
        if (this.statsMenu != null) {
            this.statsMenu.clear();
            this.statsMenu = null;
        }
        this.statsScores.clear();
    }

    @Override
    @NotNull
    public JYML getConfig() {
        return config;
    }

    @Override
    public void onSave() {

    }

    @NotNull
    public StatsMenu getStatsMenu() {
        return statsMenu;
    }

    public void update() {
        this.statsScores.clear();

        List<ArenaUser> users = plugin.getData().getUsers();
        List<String> arenaIds = plugin.getArenaManager().getArenaIds();
        //arenaIds.add(null);

        // это всё ради скриншотиков (ну и тестов конечно же!!)
		/*String[] userNames = {"Jinx","_Turbo_","BigDaddy72","red_duck","SunThunder","The_Clown","TRexxx","iZoTope",
			"Chocoboy","Katushka","VitorKhr","PURGEN","YaZanoZa","I_SEE_YOU", "EarthDragon"};

		Stream.of(userNames).sorted((c1,c2) -> Rnd.nextInt(1)).forEach(user -> {
				Stream.of(StatType.values()).forEach(statType -> {
					arenaIds.forEach(arenaId -> {
						StatsScore statsScore = new StatsScore(Rnd.get(userNames), statType, Rnd.get(1000), arenaId);
						this.getScores(statType).add(statsScore);
					});
				});
			});*/

        users.forEach(user -> {
            Stream.of(StatType.values()).forEach(statType -> {
                arenaIds.forEach(arenaId -> {
                    int score = user.getStats(statType, arenaId);
                    StatsScore statsScore = new StatsScore(user.getName(), statType, score, arenaId);
                    this.getScores(statType).add(statsScore);
                });
            });
        });

        this.plugin.getScheduler().runTask(this.plugin, () -> {
            SignManager signManager = this.plugin.getSignManager();
            if (signManager != null) {
                signManager.update(SignType.STATS);
            }

            this.plugin.getArenaManager().getArenas().stream().map(AbstractArena::getConfig).forEach(arenaConfig -> {
                for (StatType statType : StatType.values()) {
                    arenaConfig.getStatsHologram(statType).updateHolograms();
                }
            });
        });
    }

    @NotNull
    private Set<StatsScore> getScores(@NotNull StatType statType) {
        return this.statsScores.computeIfAbsent(statType, k -> new HashSet<>());
    }

	/*@NotNull
	public StatsScore getScore(@NotNull StatType statType, int pos) {
		return this.getScore(statType, pos, null);
	}*/

    @NotNull
    public StatsScore getScore(@NotNull StatType statType, int pos, @NotNull String arena) {
        List<StatsScore> scores = this.getScores(statType, pos, arena);
        return scores.size() < pos || pos < 0 ? StatsScore.empty(statType) : scores.get(pos - 1);
    }

	/*@NotNull
	public List<StatsScore> getScores(@NotNull StatType statType, int amount) {
		return this.getScores(statType, amount, null);
	}*/

    @NotNull
    public List<StatsScore> getScores(@NotNull StatType statType, int amount, @NotNull String arena) {
        return this.getScores(statType).stream()
            .filter(score -> /*arena == null ||*/ arena.equalsIgnoreCase(score.getArenaId()))
            .sorted(Comparator.comparingInt(StatsScore::getScore).reversed()).limit(amount).toList();
    }

    public void setLeaderSkin(@NotNull Block block, @NotNull String name) {
        if (!Hooks.hasPlugin(Hooks.CITIZENS)) return;

        Location location = block.getLocation();
        World world = block.getWorld();

        if (name.length() > 16) name = name.substring(0, 16);

        Entity entity = world.getNearbyEntities(location, 0.25D, 1D, 0.25D, e -> {
            return e.getType() == EntityType.PLAYER && Hooks.isCitizensNPC(e);
        }).stream().findFirst().orElse(null);

        NPC npc = entity == null ? null : CitizensAPI.getNPCRegistry().getNPC(entity);
        if (npc != null) {
            if (!npc.getName().equalsIgnoreCase(name)) {
                npc.setName(name);
            }

            SkinTrait skinTrait = npc.getTraitNullable(SkinTrait.class);
            if (skinTrait != null && !name.equalsIgnoreCase(skinTrait.getSkinName())) {
                skinTrait.setSkinName(name);
            }
        }
    }

    class StatsTask extends AbstractTask<AMA> {

        public StatsTask() {
            super(StatsManager.this.plugin, StatsConfig.UPDATE_INTERVAL.get(), true);
        }

        @Override
        public void action() {
            update();
        }
    }
}
