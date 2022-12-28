package me.mimovnik.claimz;

import org.bukkit.Bukkit;
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

import static me.mimovnik.claimz.ClaimCubeFactory.Unit.*;

public final class Claimz extends JavaPlugin implements Listener {

    private ClaimContainer claimContainer;
    private ClaimRenderer renderer;
    private final Map<UUID, ClaimEditor> claimEditors = new HashMap<>();
    private ClaimCubeFactory factory;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        claimContainer = ClaimContainer.loadFromFile(pluginManager.getPlugin("Claimz").getDataFolder());

        renderer = new ClaimRenderer(claimContainer);
        pluginManager.registerEvents(new ClaimGuard(claimContainer), this);
        pluginManager.registerEvents(this, this);
        getCommand("deleteClaim").setExecutor(new DeleteClaim(claimContainer));
        getCommand("toggleShowClaims").setExecutor(new ClaimRenderer(claimContainer));
        factory = new ClaimCubeFactory();

        for (Recipe recipe : factory.getRecipes()) {
            getServer().addRecipe(recipe);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), factory.getCubeStack(1, EIGHTY_ONES));

        UUID uniqueId = event.getPlayer().getUniqueId();
        claimEditors.computeIfAbsent(uniqueId, (key) -> {
            ClaimEditor claimEditor = new ClaimEditor(claimContainer, renderer, key);
            getServer().getPluginManager().registerEvents(claimEditor, this);
            Bukkit.getServer().getLogger().log(Level.INFO, "Claimz: Successfully created a new claim editor for " + event.getPlayer().getName());
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
