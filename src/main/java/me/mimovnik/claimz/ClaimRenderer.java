package me.mimovnik.claimz;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClaimRenderer implements CommandExecutor {
    private final ClaimContainer claimContainer;
    private static final int maxClaimDisplayDistance = 500;
    private final Set<Player> normalRenderPlayers = new HashSet<>();
    private final Set<Player> forcedRenderPlayers = new HashSet<>();

    public ClaimRenderer(ClaimContainer claimContainer) {
        this.claimContainer = claimContainer;
        ScheduledExecutorService particleRenderer = Executors.newSingleThreadScheduledExecutor();
        particleRenderer.scheduleAtFixedRate(this::displayClaims, 0, 500, MILLISECONDS);
    }

    private void displayClaims() {
        Set<Player> renderPlayers = new HashSet<>();
        renderPlayers.addAll(normalRenderPlayers);
        renderPlayers.addAll(forcedRenderPlayers);

        if (renderPlayers.isEmpty()) {
            return;
        }
        Color color;
        for (Claim claim : claimContainer.getAllClaims()) {
            if (renderPlayers.stream().anyMatch(player -> isClose(player, claim))) {
                color = Color.RED;

                claim.display(color);
            }
        }
    }

    private double square(int base) {
        return base * base;
    }

    private boolean isClose(Player player, Claim claim) {
        Location claimCenter = claim.getCenter();
        Location playerPos = player.getLocation();
        double distanceSqr = square(claimCenter.getBlockX() - playerPos.getBlockX()) +
                square(claimCenter.getBlockY() - playerPos.getBlockY()) +
                square(claimCenter.getBlockZ() - playerPos.getBlockZ());
        return distanceSqr <= square(maxClaimDisplayDistance);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            return false;
        }
        if (sender instanceof Player player) {
            toggleForceShow(player);

            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }

    public void show(Player player) {
        normalRenderPlayers.add(player);
    }

    public void hide(Player player) {
        normalRenderPlayers.remove(player);
    }

    public void toggleForceShow(Player player) {
        if (forcedRenderPlayers.contains(player)) {
            forcedRenderPlayers.remove(player);
            player.sendMessage(ChatColor.YELLOW + "Showing claims disabled.");
        } else {
            forcedRenderPlayers.add(player);
            player.sendMessage(ChatColor.YELLOW + "Showing claims enabled.");
        }
    }
}
