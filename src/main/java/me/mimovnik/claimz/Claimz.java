package me.mimovnik.claimz;

import org.bukkit.plugin.java.JavaPlugin;

public final class Claimz extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ClaimEditor(),this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
