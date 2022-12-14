package me.mimovnik.claimz;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class ClaimGuard implements Listener {
    private ArrayList<Claim> claims;

    public ClaimGuard(ArrayList<Claim> claims) {
        this.claims = claims;
    }

    private boolean cancelEvent(Player player, Location location){
        for (Claim claim : claims) {
            if (claim.contains(location) && player != claim.getOwner()) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that. It's " + ChatColor.ITALIC + "" + ChatColor.YELLOW + claim.getOwner().getName() + ChatColor.RED + "'s claim.");
                return true;
            }
        }
        return false;
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(cancelEvent(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event){
        event.setCancelled(cancelEvent(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onMultiPlaceBlock(BlockMultiPlaceEvent event){
        event.setCancelled(cancelEvent(event.getPlayer(), event.getBlock().getLocation()));
    }
}
