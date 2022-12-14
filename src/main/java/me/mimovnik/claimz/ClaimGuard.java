package me.mimovnik.claimz;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.*;

public class ClaimGuard implements Listener {
    private ArrayList<Claim> claims;

    public ClaimGuard(ArrayList<Claim> claims) {
        this.claims = claims;
    }

    private boolean hasNOTPermission(Player player, Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location) && player != claim.getOwner()) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that. It's " + ChatColor.ITALIC + "" + ChatColor.YELLOW + claim.getOwner().getName() + ChatColor.RED + "'s claim.");
                return true;
            }
        }
        return false;
    }

    private boolean isInAnyClaim(Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        event.setCancelled(hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onMultiPlaceBlock(BlockMultiPlaceEvent event) {
        event.setCancelled(hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();

        List<Block> explodedBlocks = new ArrayList<>();

        for (Block block : blocks) {
            if (block.getType() == Material.AIR) continue;

            if (isInAnyClaim(block.getLocation())) continue;

            explodedBlocks.add(block);
        }

        blocks.clear();
        blocks.addAll(explodedBlocks);
    }

    @EventHandler
    void onEntityByEntityDamage(EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        // Don't prevent any damage to Monsters, Players and entities outside any claim.
        if (defender instanceof Monster || defender instanceof Player) return;
        if (!isInAnyClaim(defender.getLocation())) return;

        // Prevent tnt, creeper and any explosive entity damage
        DamageCause cause = event.getCause();
        if (cause == ENTITY_EXPLOSION) {
            event.setCancelled(true);
        }

        Player attacker = null;
        Entity damager = event.getDamager();

        if (damager instanceof Player) {
            attacker = (Player) damager;
        } else if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player) {
                attacker = (Player) projectile.getShooter();
            }
        }
        if (attacker == null) return;

        event.setCancelled(hasNOTPermission(attacker, defender.getLocation()));


    }
}
