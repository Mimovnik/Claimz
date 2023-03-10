package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class TrustPlayer implements CommandExecutor {
    private ClaimContainer claimContainer;

    public TrustPlayer(ClaimContainer claimContainer) {
        this.claimContainer = claimContainer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return false;
        }
        OfflinePlayer toTrust = Bukkit.getOfflinePlayer(args[0]);
        if (!toTrust.hasPlayedBefore()) {
            sender.sendMessage("There is no such player as " + args[0]);
            return true;
        }
        if (sender instanceof Player player) {
            Claim claim = claimContainer.getClaimAt(player.getLocation());
            if (claim == null) {
                sender.sendMessage("Cannot add trusted player to this claim. Your not standing at any claim.");
                return true;
            }
            if (claimContainer.hasNOTPermission(player, player.getLocation())) {
                return true;
            }
            if (claim.addTrustedId(toTrust.getUniqueId())) {
                claim.saveToFile();
                sender.sendMessage("Successfully trusted " + args[0]);
            } else {
                sender.sendMessage(args[0] + " is already a trusted member of this claim.");
            }

            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }
}
