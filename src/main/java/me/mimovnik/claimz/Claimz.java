package me.mimovnik.claimz;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Claimz extends JavaPlugin {

    private ArrayList<Claim> claims;

    public Claimz(){
       claims = new ArrayList<>();
    }

    public void addClaim(Claim newClaim){
        claims.add(newClaim);
    }
    public ArrayList<Claim> getClaims(){
        return claims;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new ClaimEditor(claims),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
