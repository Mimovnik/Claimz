package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;

public final class Claimz extends JavaPlugin {

    private ArrayList<Claim> claims;

    public Claimz(){
       claims = new ArrayList<>();
       Claim.setClaims(claims);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ClaimEditor(claims),this);
        getServer().getPluginManager().registerEvents(new ClaimGuard(claims), this);
//        Bukkit.getServer().getLogger().log(Level.INFO, "FOLDER=======" +));
        Claim.loadFromFile();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
