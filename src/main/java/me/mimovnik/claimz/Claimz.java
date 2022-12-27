package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public final class Claimz extends JavaPlugin implements Listener {

    private ClaimContainer claimContainer;
    private ClaimRenderer renderer;
    private final Map<UUID, ClaimEditor> claimEditors = new HashMap<>();


    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        claimContainer = ClaimContainer.loadFromFile(pluginManager.getPlugin("Claimz").getDataFolder());

        renderer = new ClaimRenderer(claimContainer);
        pluginManager.registerEvents(new ClaimGuard(claimContainer), this);
        pluginManager.registerEvents(this, this);
        getCommand("deleteClaim").setExecutor(new DeleteClaim(claimContainer));
        getCommand("toggleShowClaims").setExecutor(new ClaimRenderer(claimContainer));

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        claimEditors.computeIfAbsent(uniqueId, (key) -> {
            ClaimEditor claimEditor = new ClaimEditor(claimContainer, renderer, key);
            getServer().getPluginManager().registerEvents(claimEditor, this);
            Bukkit.getServer().getLogger().log(Level.INFO, "Claimz: Successfully created a new claim editor for " + event.getPlayer().getName());
            return claimEditor;
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
