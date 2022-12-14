package me.mimovnik.claimz;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

public class ClaimGuard implements Listener {
    private ArrayList<Claim> claims;

    public ClaimGuard(ArrayList<Claim> claims) {
        this.claims = claims;
    }

    private boolean hasPermission(Player player, Location location){
        for (Claim claim : claims) {
            if (claim.contains(location) && player != claim.getOwner()) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that. It's " + ChatColor.ITALIC + "" + ChatColor.YELLOW + claim.getOwner().getName() + ChatColor.RED + "'s claim.");
                return true;
            }
        }
        return false;
    }

    private boolean isInAnyClaim(Location location){
        for(Claim claim: claims){
            if(claim.contains(location)){
                return true;
            }
        }
        return false;
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(hasPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event){
        event.setCancelled(hasPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onMultiPlaceBlock(BlockMultiPlaceEvent event){
        event.setCancelled(hasPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        List<Block> blocks = event.blockList();

        List<Block> explodedBlocks = new ArrayList<>();

        for(Block block : blocks){
            if(block.getType() == Material.AIR) continue;

            if(isInAnyClaim(block.getLocation())) continue;

            explodedBlocks.add(block);
        }

        blocks.clear();
        blocks.addAll(explodedBlocks);
    }
}
