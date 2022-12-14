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

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClaimEditor implements Listener {
    private Location firstCorner, secondCorner;
    private boolean isHoldingEditorTool = false;
    private Material editorTool = Material.STICK;
    private ArrayList<Claim> claims;
    private ScheduledExecutorService particleRenderer = Executors.newSingleThreadScheduledExecutor();

    public ClaimEditor(ArrayList<Claim> claims) {
        this.claims = claims;
        particleRenderer.scheduleAtFixedRate(this::displayClaims, 0, 500, MILLISECONDS);
    }

    private void displayClaims() {
        if (isHoldingEditorTool) {
            for (Claim claim : claims) {
                claim.display();
            }
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
                        Claim newClaim = new Claim(firstCorner, secondCorner, player.getWorld(), player);
                        player.sendMessage("Claim set to" + newClaim);
                        claims.add(newClaim);
                        firstCorner = null;
                        secondCorner = null;
                    }
                }
            }
        }
    }

}
