package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class ShowTrustedPlayers implements CommandExecutor {
    private ClaimContainer claimContainer;

    public ShowTrustedPlayers(ClaimContainer claimContainer) {
        this.claimContainer = claimContainer;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            return false;
        }
        if (sender instanceof Player player) {
            Claim claim = claimContainer.getClaimAt(player.getLocation());
            if (claim == null) {
                sender.sendMessage("Cannot show trusted players. Your not standing at any claim.");
                return true;
            }
            if (claimContainer.hasNOTPermission(player, player.getLocation())) {
                return true;
            }
            sender.sendMessage("Owner: " + Bukkit.getOfflinePlayer(claim.getOwnerID()).getName());
            if(claim.getTrusted().size() > 0){
                sender.sendMessage("Trusted:");
            }
            for(UUID id : claim.getTrusted()){
                sender.sendMessage(Bukkit.getOfflinePlayer(id).getName());
            }

            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }
}
