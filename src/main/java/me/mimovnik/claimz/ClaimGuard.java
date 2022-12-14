package me.mimovnik.claimz;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class ClaimGuard implements Listener {
    private ArrayList<Claim> claims;

    public ClaimGuard(ArrayList<Claim> claims) {
        this.claims = claims;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        for (Claim claim : claims) {
            if (claim.contains(location) && player != claim.getOwner()) {
                event.setCancelled(true);
                player.sendMessage("You can't break a block here. It's " + ChatColor.ITALIC + claim.getOwner().getName() + ChatColor.RESET + "'s land.");
            }
        }
    }
}
