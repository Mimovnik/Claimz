package me.mimovnik.claimz;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

public class ClaimEditor implements Listener {
    private Location firstCorner, secondCorner;
    private boolean isHoldingEditorTool = false;
    private Material editorTool = Material.STICK;
    private Claim claim;
    private ScheduledExecutorService particleRenderer = Executors.newSingleThreadScheduledExecutor();

    public ClaimEditor() {
        particleRenderer.scheduleAtFixedRate(this::displayClaim, 0, 250, MILLISECONDS);
    }

    private void displayClaim() {
        if (claim != null && isHoldingEditorTool) {
            claim.display();
        }
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null) {
            player.sendMessage("You are NOT holding the editor tool.");
            isHoldingEditorTool = false;
            return;
        }

        if (item.getType().equals(editorTool)) {
            player.sendMessage("You are holding the editor tool.");
            isHoldingEditorTool = true;


        } else {
            player.sendMessage("You are NOT holding the editor tool.");
            isHoldingEditorTool = false;
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isHoldingEditorTool && event.getHand() == EquipmentSlot.HAND) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                Block block = event.getClickedBlock();
                if (block == null) {
                    player.sendMessage("Claim setup cancelled");
                    firstCorner = null;
                    secondCorner = null;
                } else {
                    if (firstCorner == null) {
                        firstCorner = block.getLocation();
                        player.sendMessage("First Corner set to:" + block.getLocation());
                    } else {
                        secondCorner = block.getLocation();
                        player.sendMessage("Second Corner set to:" + block.getLocation());
                        claim = new Claim(firstCorner, secondCorner, player.getWorld());
                        player.sendMessage("Claim set to" + claim);
                        firstCorner = null;
                        secondCorner = null;
                    }
                }
            }
        }
    }

}
