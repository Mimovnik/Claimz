package me.mimovnik.claimz;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class ToggleShowClaims implements CommandExecutor {

    ArrayList<ClaimEditor> claimEditors;

    public ToggleShowClaims(ArrayList<ClaimEditor> claimEditors) {
        this.claimEditors = claimEditors;
    }

    public @Nullable ClaimEditor getClaimEditor(UUID ownerID) {
        for (ClaimEditor claimEditor : claimEditors) {
            if (claimEditor.getOwnerID().equals(ownerID)) {
                return claimEditor;
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length != 0){
            return false;
        }
        if (sender instanceof Player player) {
            ClaimEditor claimEditor = getClaimEditor(player.getUniqueId());
            if (claimEditor == null) {
                throw new RuntimeException("This player doesn't have a claim editor: " + player);
            }
            claimEditor.setShowClaims(!claimEditor.getShowClaims());

            if (claimEditor.getShowClaims()) {
                sender.sendMessage(ChatColor.YELLOW + "Showing claims enabled.");
            } else {
                sender.sendMessage(ChatColor.YELLOW + "Showing claims disabled.");
            }
            return true;
        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }
}
