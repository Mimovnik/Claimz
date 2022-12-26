package me.mimovnik.claimz;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static me.mimovnik.claimz.Claim.getClaimAt;
import static me.mimovnik.claimz.Claim.hasNOTPermission;

public class DeleteClaim implements CommandExecutor {
    private  ArrayList<Claim> claims;

    public DeleteClaim(ArrayList<Claim> claims){
        this.claims = claims;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length != 0){
            return false;
        }
        if (sender instanceof Player player) {
            if (hasNOTPermission(player, player.getLocation())) {
                return true;
            }
            Claim claim = getClaimAt(player.getLocation());
            claim.removeFromFile();
            claims.remove(claim);
            sender.sendMessage("Successfully removed claim: " + claim);
            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }
}
