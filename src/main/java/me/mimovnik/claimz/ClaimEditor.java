package me.mimovnik.claimz;

import me.mimovnik.claimz.ClaimCubeFactory.Unit;
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
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.mimovnik.claimz.ClaimCubeFactory.Unit.*;

public class ClaimEditor implements Listener{
    private UUID ownerID;
    private Location firstVertex, secondVertex;
    private boolean isHoldingEditorTool = false;
    private Material editorTool = Material.STICK;
    private Claim claimToEdit;
    private Location opposingVertex;
    private ClaimRenderer renderer;
    private ClaimContainer claimContainer;
    private ClaimCubeFactory factory;

    public ClaimEditor(ClaimContainer claimContainer, ClaimRenderer renderer, UUID ownerID, ClaimCubeFactory factory) {
        this.ownerID = ownerID;
        this.claimContainer = claimContainer;
        this.renderer = renderer;
        this.factory = factory;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    @EventHandler
    public void onPlayerItemHeld(@NotNull PlayerItemHeldEvent event) {
        if (!event.getPlayer().getUniqueId().equals(ownerID)) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());
        if (item == null || !item.getType().equals(editorTool)) {
            isHoldingEditorTool = false;
        } else {
            player.sendMessage(ChatColor.YELLOW + "(All with editor tool in main hand)");
            player.sendMessage(ChatColor.YELLOW + "To claim right click two opposing vertices of a prism.");
            player.sendMessage(ChatColor.YELLOW + "To edit right click existing vertex.");
            player.sendMessage(ChatColor.YELLOW + "To cancel left click anywhere.");
            isHoldingEditorTool = true;
        }

        if (isHoldingEditorTool) {
            renderer.show(player);
        } else {
            renderer.hide(player);
        }
    }

    @EventHandler
    public void onRightClick(@NotNull PlayerInteractEvent event) {
        if (!event.getPlayer().getUniqueId().equals(ownerID)) {
            return;
        }
        Player player = event.getPlayer();
        if (isHoldingEditorTool && event.getHand() == EquipmentSlot.HAND) {
            Action action = event.getAction();
            if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
                cancelSetup(player);
            }
            if (action == Action.RIGHT_CLICK_BLOCK) {
                Block block = event.getClickedBlock();
                if (claimContainer.hasNOTPermission(player, block.getLocation())) {
                    return;
                }

                if (claimToEdit != null) {
                    int oldVolume = claimToEdit.getVolume();

                    Claim newClaim = new Claim(block.getLocation(), opposingVertex, claimToEdit.getWorldID(), claimToEdit.getOwnerID());
                    int newVolume = newClaim.getVolume();

                    int cost = newVolume - oldVolume;
                    int playerCubes = factory.countCubes(player);

                    if (playerCubes < cost) {
                        player.sendMessage(ChatColor.RED + "Cost: " + cost + " You cubes: " + playerCubes + " Need: " + (cost - playerCubes));
                        return;
                    }
                    claimToEdit.setNewBoundaries(block.getLocation(), opposingVertex);
                    claimToEdit.saveToFile();
                    factory.balanceCubes(player, playerCubes - cost);
                    claimToEdit = null;
                    return;
                }

                Claim clicked = claimContainer.getClaimAt(block.getLocation());
                if (firstVertex == null && clicked != null && clicked.isVertex(block.getLocation())) {
                    claimToEdit = clicked;
                    opposingVertex = clicked.getOpposingVertex(block.getLocation());
                    player.sendMessage("Editing your claim. Right click a block to select new vertex.");
                } else {
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
            for (Claim claim : claimContainer.getAllClaims()) {
                if (newClaim.intersects(claim) && !claim.hasPermission(player)) {
                    player.sendMessage(ChatColor.RED + "This claim would intersect with " +
                            ChatColor.ITALIC + "" + ChatColor.YELLOW + Bukkit.getOfflinePlayer(claim.getOwnerID()).getName() +
                            ChatColor.RED + "'s claim. Choose another vertex.");
                    secondVertex = null;
                    return;
                }
            }

            int cost = newClaim.getVolume();
            int playerCubes = factory.countCubes(player);

            if (playerCubes >= cost) {
                factory.balanceCubes(player, playerCubes - cost);
            } else {
                player.sendMessage(ChatColor.RED + "Cost: " + cost + " You cubes: " + playerCubes + " Need: " + (cost - playerCubes));
                return;
            }

            player.sendMessage("Claim set to" + newClaim);
            claimContainer.add(newClaim);
            newClaim.saveToFile();
            firstVertex = null;
            secondVertex = null;
        }
    }


    private void cancelSetup(@NotNull Player player) {
        player.sendMessage("Claim setup canceled.");
        firstVertex = null;
        secondVertex = null;
        claimToEdit = null;
    }

}
