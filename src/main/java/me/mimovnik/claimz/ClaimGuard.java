package me.mimovnik.claimz;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;

public class ClaimGuard implements Listener {

    private ClaimContainer claimContainer;

    public ClaimGuard(ClaimContainer claimContainer) {
        this.claimContainer = claimContainer;
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(claimContainer.hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(claimContainer.hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        event.setCancelled(claimContainer.hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        event.setCancelled(claimContainer.hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (!event.getClickedBlock().getType().isInteractable()) {
            return;
        }
        event.setCancelled(claimContainer.hasNOTPermission(event.getPlayer(), event.getClickedBlock().getLocation()));
    }

    @EventHandler
    public void onLiquidBlockFlow(BlockFromToEvent event) {
        Claim sourceBlockClaim = claimContainer.getClaimAt(event.getBlock().getLocation());
        Claim flowBlockClaim = claimContainer.getClaimAt(event.getToBlock().getLocation());
        // Flow can happen to any unclaimed block(From claimed to unclaimed)
        if (flowBlockClaim == null) {
            return;
        }
        // Flow can happen if it's in the same claim or outside any(From claimed to the same claim)
        event.setCancelled(sourceBlockClaim != flowBlockClaim);
    }

    @EventHandler
    public void onMultiPlaceBlock(BlockMultiPlaceEvent event) {
        event.setCancelled(claimContainer.hasNOTPermission(event.getPlayer(), event.getBlock().getLocation()));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> blocks = event.blockList();

        List<Block> explodedBlocks = new ArrayList<>();

        for (Block block : blocks) {
            if (block.getType() == Material.AIR) continue;

            if (claimContainer.isInAnyClaim(block.getLocation())) continue;

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
        if (!claimContainer.isInAnyClaim(defender.getLocation())) return;

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

        event.setCancelled(claimContainer.hasNOTPermission(attacker, defender.getLocation()));
    }
}
