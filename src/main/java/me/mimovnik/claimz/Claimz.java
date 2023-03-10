package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static me.mimovnik.claimz.ClaimCubeFactory.Unit.EIGHTY_ONES;

public final class Claimz extends JavaPlugin implements Listener {

    private ClaimContainer claimContainer;
    private ClaimRenderer renderer;
    private final Map<UUID, ClaimEditor> claimEditors = new HashMap<>();
    private ClaimCubeFactory factory;
    private ClaimGuard claimGuard;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        claimContainer = ClaimContainer.loadFromFile(getDataFolder());
        claimGuard = new ClaimGuard(claimContainer);

        factory = new ClaimCubeFactory();
        renderer = new ClaimRenderer(claimContainer);


        pluginManager.registerEvents(claimGuard, this);
        pluginManager.registerEvents(this, this);
        getCommand("deleteClaim").setExecutor(new DeleteClaim(claimContainer, factory));
        getCommand("toggleShowClaims").setExecutor(new ClaimRenderer(claimContainer));
        getCommand("giveCubes").setExecutor(factory);
        getCommand("toggleGuardClaims").setExecutor(claimGuard);
        getCommand("trust").setExecutor(new TrustPlayer(claimContainer));

        for (Recipe recipe : factory.getRecipes()) {
            getServer().addRecipe(recipe);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Give starting amount of cubes on first login
        if (!player.hasPlayedBefore()) {
            player.getInventory().addItem(factory.getCubeStack(32, EIGHTY_ONES));
        }

        // Give every player his own Claim Editor
        UUID uniqueId = player.getUniqueId();
        claimEditors.computeIfAbsent(uniqueId, (key) -> {
            ClaimEditor claimEditor = new ClaimEditor(claimContainer, renderer, key, factory);
            getServer().getPluginManager().registerEvents(claimEditor, this);
            Bukkit.getServer().getLogger().log(Level.INFO, "Claimz: Successfully created a new claim editor for " + player.getName());
            return claimEditor;
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (factory.isCube(item)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
