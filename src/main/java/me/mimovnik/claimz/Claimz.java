package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public final class Claimz extends JavaPlugin implements Listener {

    private ArrayList<Claim> claims;
    private ArrayList<ClaimEditor> claimEditors;

    public Claimz() {
        claims = new ArrayList<>();
        Claim.setClaims(claims);
        claimEditors = new ArrayList<>();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ClaimGuard(claims), this);
        getServer().getPluginManager().registerEvents(this, this);
//        Bukkit.getServer().getLogger().log(Level.INFO, "FOLDER=======" +));
        Claim.loadFromFile();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        for (ClaimEditor claimEditor : claimEditors) {
            // If the player already has them own editor
            if (claimEditor.getOwnerID().equals(uniqueId)){
                return;
            }
        }
        ClaimEditor claimEditor = new ClaimEditor(claims, uniqueId);
        claimEditors.add(claimEditor);
        getServer().getPluginManager().registerEvents(claimEditor, this);
        Bukkit.getServer().getLogger().log(Level.INFO, "Claimz: Successfully created a new claim editor for" + event.getPlayer().getName());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
