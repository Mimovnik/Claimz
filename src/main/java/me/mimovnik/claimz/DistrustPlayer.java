package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class DistrustPlayer implements CommandExecutor {
    private ClaimContainer claimContainer;

    public DistrustPlayer(ClaimContainer claimContainer) {
        this.claimContainer = claimContainer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 1) {
            return false;
        }
        OfflinePlayer toDistrust = Bukkit.getOfflinePlayer(args[0]);
        if (!toDistrust.hasPlayedBefore()) {
            sender.sendMessage("There is no such player as " + args[0]);
            return true;
        }
        if (sender instanceof Player player) {
            Claim claim = claimContainer.getClaimAt(player.getLocation());
            if (claim == null) {
                sender.sendMessage("Cannot remove trusted player. Your not standing at any claim.");
                return true;
            }
            if (claimContainer.hasNOTPermission(player, player.getLocation())) {
                return true;
            }
            if (claim.removeTrustedId(toDistrust.getUniqueId())) {
                claim.saveToFile();
                sender.sendMessage("Successfully distrusted " + args[0]);
            } else {
                sender.sendMessage(args[0] + " is not a trusted member of this claim.");
            }

            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }
}
