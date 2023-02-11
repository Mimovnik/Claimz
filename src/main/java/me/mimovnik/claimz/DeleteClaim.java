package me.mimovnik.claimz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;


public class DeleteClaim implements CommandExecutor {
    private ClaimContainer claimContainer;
    private ClaimCubeFactory factory;

    public DeleteClaim(ClaimContainer claimContainer, ClaimCubeFactory factory) {
        this.claimContainer = claimContainer;
        this.factory = factory;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            return false;
        }
        if (sender instanceof Player player) {
            Claim claim = claimContainer.getClaimAt(player.getLocation());
            if(claim == null){
                sender.sendMessage("Cannot delete claim. Your not standing at any claim.");
                return true;
            }
            if (claimContainer.hasNOTPermission(player, player.getLocation())) {
                return true;
            }

            factory.balanceCubes(player, factory.countCubes(player) + claim.getVolume());

            claim.removeFromFile();
            claimContainer.remove(claim);
            sender.sendMessage("Successfully removed claim: " + claim);
            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }
}
