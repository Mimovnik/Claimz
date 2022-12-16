package me.mimovnik.claimz;

import org.bukkit.ChatColor;
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
import static me.mimovnik.claimz.Claim.hasNOTPermission;

public class ClaimEditor implements Listener {
    private Location firstVertex, secondVertex;
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
            isHoldingEditorTool = false;
            return;
        }

        if (item.getType().equals(editorTool)) {
            player.sendMessage(ChatColor.YELLOW + "(All with editor tool in main hand)");
            player.sendMessage(ChatColor.YELLOW + "To claim right click two opposing vertices of a prism.");
            player.sendMessage(ChatColor.YELLOW + "To cancel left click anywhere.");
            isHoldingEditorTool = true;
        } else {
            isHoldingEditorTool = false;
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isHoldingEditorTool && event.getHand() == EquipmentSlot.HAND) {
            Action action = event.getAction();
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
                player.sendMessage("Claim setup canceled.");
                firstVertex = null;
                secondVertex = null;
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();

                if (hasNOTPermission(player, block.getLocation())) {
                    return;
                }

                if (firstVertex == null) {
                    firstVertex = block.getLocation();
                    player.sendMessage("First vertex set to:" + block.getLocation());
                } else {
                    secondVertex = block.getLocation();
                    player.sendMessage("Second vertex set to:" + block.getLocation());
                    Claim newClaim = new Claim(firstVertex, secondVertex, player.getWorld(), player);
                    for (Claim claim : claims) {
                        if (newClaim.intersects(claim) && newClaim.getOwner() != claim.getOwner()) {
                            player.sendMessage(ChatColor.RED + "This claim would intersect with " +
                                    ChatColor.ITALIC + "" + ChatColor.YELLOW + claim.getOwner().getName() +
                                    ChatColor.RED + "'s claim. Choose another vertex.");
                            secondVertex = null;
                            return;
                        }
                    }
                    player.sendMessage("Claim set to" + newClaim);
                    claims.add(newClaim);
                    firstVertex = null;
                    secondVertex = null;
                }
            }
        }
    }

}
