package me.mimovnik.claimz;

import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static me.mimovnik.claimz.Claim.getClaimAt;
import static me.mimovnik.claimz.Claim.hasNOTPermission;

public class ClaimEditor implements Listener {
    private Location firstVertex, secondVertex;
    private boolean isHoldingEditorTool = false;
    private Material editorTool = Material.STICK;
    private ArrayList<Claim> claims;
    private ScheduledExecutorService particleRenderer = Executors.newSingleThreadScheduledExecutor();

    private Claim claimToEdit;
    private Location opposingVertex;

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
    public void onPlayerItemHeld(@NotNull PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null) {
            isHoldingEditorTool = false;
            return;
        }

        if (item.getType().equals(editorTool)) {
            player.sendMessage(ChatColor.YELLOW + "(All with editor tool in main hand)");
            player.sendMessage(ChatColor.YELLOW + "To claim right click two opposing vertices of a prism.");
            player.sendMessage(ChatColor.YELLOW + "To edit right click existing vertex.");
            player.sendMessage(ChatColor.YELLOW + "To cancel left click anywhere.");
            isHoldingEditorTool = true;
        } else {
            isHoldingEditorTool = false;
        }
    }

    @EventHandler
    public void onRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (isHoldingEditorTool && event.getHand() == EquipmentSlot.HAND) {
            Action action = event.getAction();
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
                cancelSetup(player);
            }
            if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (hasNOTPermission(player, block.getLocation())) {
                    return;
                }

                if(claimToEdit != null){
                    claimToEdit.setNewBoundaries(block.getLocation(), opposingVertex);
                    claimToEdit.saveToFile();
                    claimToEdit = null;
                    return;
                }

                Claim clicked = getClaimAt(block.getLocation());
                if(firstVertex == null && clicked != null && clicked.isVertex(block.getLocation())){
                    claimToEdit = clicked;
                    opposingVertex = clicked.getOpposingVertex(block.getLocation());
                    player.sendMessage("Editing your claim. Right click a block to select new vertex.");
                }else{
                    setupNewClaim(player, block);
                }
            }
        }
    }

    private void setupNewClaim(Player player, Block block) {
        if (firstVertex == null) {
            firstVertex = block.getLocation();
            player.sendMessage("First vertex set to:" + block.getLocation());
        } else {
            secondVertex = block.getLocation();
            player.sendMessage("Second vertex set to:" + block.getLocation());
            Claim newClaim = new Claim(firstVertex, secondVertex, player.getWorld().getUID(), player.getUniqueId());
            for (Claim claim : claims) {
                if (newClaim.intersects(claim) && newClaim.getOwnerID() != claim.getOwnerID()) {
                    player.sendMessage(ChatColor.RED + "This claim would intersect with " +
                            ChatColor.ITALIC + "" + ChatColor.YELLOW + Bukkit.getPlayer(claim.getOwnerID()).getName() +
                            ChatColor.RED + "'s claim. Choose another vertex.");
                    secondVertex = null;
                    return;
                }
            }
            player.sendMessage("Claim set to" + newClaim);
            claims.add(newClaim);
            newClaim.saveToFile();
            firstVertex = null;
            secondVertex = null;
        }
    }

    private void cancelSetup(@NotNull Player player) {
        player.sendMessage("Claim setup canceled.");
        firstVertex = null;
        secondVertex = null;
    }

}
