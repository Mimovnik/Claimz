package me.mimovnik.claimz;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class ClaimGuard implements Listener {
    private ArrayList<Claim> claims;

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event){

    }
}
