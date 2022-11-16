package su.nightexpress.ama.arena.spot;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.IEditable;
import su.nexmedia.engine.api.manager.ILoadable;
import su.nightexpress.ama.Placeholders;
import su.nightexpress.ama.api.arena.IArenaObject;
import su.nightexpress.ama.api.arena.IProblematic;
import su.nightexpress.ama.arena.config.ArenaConfig;
import su.nightexpress.ama.arena.editor.spot.EditorSpotList;

import java.util.*;
import java.util.function.UnaryOperator;

public class ArenaSpotManager implements IArenaObject, ILoadable, IEditable, IProblematic {

	private final ArenaConfig arenaConfig;
	
	private Map<String, ArenaSpot> spots;
	private EditorSpotList          editor;

	public static final String DIR_SPOTS = "/spots/";

	public ArenaSpotManager(@NotNull ArenaConfig arenaConfig) {
		this.arenaConfig = arenaConfig;
	}
	
	@Override
	public void setup() {
		this.spots = new HashMap<>();
		
		for (JYML cfg : JYML.loadAll(arenaConfig.getFile().getParentFile().getAbsolutePath() + DIR_SPOTS, false)) {
			try {
				ArenaSpot spot = new ArenaSpot(this.arenaConfig, cfg);
				this.spots.put(spot.getId(), spot);
			}
			catch (Exception ex) {
				arenaConfig.plugin().error("Could not load '" + cfg.getFile().getName() + "' spot for '" + arenaConfig.getId() + "' arena!");
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void shutdown() {
		if (this.editor != null) {
			this.editor.clear();
			this.editor = null;
		}
		this.getSpots().forEach(ArenaSpot::clear);
		this.getSpots().clear();
	}

	@Override
	@NotNull
	public UnaryOperator<String> replacePlaceholders() {
		return str -> str.replace(Placeholders.GENERIC_PROBLEMS, Placeholders.formatProblems(this.getProblems()));
	}

	@Override
	@NotNull
	public List<String> getProblems() {
		List<String> list = new ArrayList<>();
		this.getSpots().forEach(spot -> {
			if (spot.isActive() && spot.hasProblems()) {
				list.add("Problems with " + spot.getId() + " spot!");
			}
		});

		return list;
	}

	@NotNull
	@Override
	public EditorSpotList getEditor() {
		if (this.editor == null) {
			this.editor = new EditorSpotList(this);
		}
		return editor;
	}

	@Override
	@NotNull
	public ArenaConfig getArenaConfig() {
		return this.arenaConfig;
	}

	@NotNull
	public Map<String, ArenaSpot> getSpotsMap() {
		return this.spots;
	}

	@NotNull
	public Collection<ArenaSpot> getSpots() {
		return this.getSpotsMap().values();
	}

	public void addSpot(@NotNull ArenaSpot spot) {
		this.getSpotsMap().put(spot.getId(), spot);
	}

	public void removeSpot(@NotNull ArenaSpot spot) {
		if (spot.getFile().delete()) {
			spot.clear();
			this.getSpotsMap().remove(spot.getId());
		}
	}

	@Nullable
	public ArenaSpot getSpot(@NotNull String id) {
		return this.getSpotsMap().get(id.toLowerCase());
	}

	@Nullable
	public ArenaSpot getSpot(@NotNull Location location) {
		return this.getSpots().stream().filter(spot -> spot.getCuboid().contains(location)).findFirst().orElse(null);
	}
}
