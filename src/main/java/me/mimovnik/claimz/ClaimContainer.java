package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;


public class ClaimContainer {

    private List<Claim> claims;

    public ClaimContainer(List<Claim> claims) {
        this.claims = claims;
    }


    public @Nullable Claim getClaimAt(Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location)) {
                return claim;
            }
        }
        return null;
    }

    public boolean hasNOTPermission(@NotNull Player player, @NotNull Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location) && !claim.hasPermission(player)) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that. It's " +
                        ChatColor.ITALIC + "" + ChatColor.YELLOW + Bukkit.getOfflinePlayer(claim.getOwnerID()).getName() + ChatColor.RED + "'s claim.");
                return true;
            }
        }
        return false;
    }

    public boolean isInAnyClaim(Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location)) {
                return true;
            }
        }
        return false;
    }

    public static ClaimContainer loadFromFile(File folder) {
        try {
            return new ClaimContainer(tryLoad(folder));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Claim> tryLoad(File folder) throws IOException {
        List<Claim> loaded = new ArrayList<>();

        File dataFile = new File(folder + File.separator + "Claimz.data");
        if (!dataFile.exists()) {
            Bukkit.getServer().getLogger().log(Level.INFO, "Could not load claims because this file '" + dataFile + "' does not exist.");
            return loaded;
        }

        try (Scanner scanner = new Scanner(dataFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(";");
                int minDataCount = 9;
                if (data.length < minDataCount) {
                    throw new RuntimeException("Corrupted line of save data in this line '" + line + "' of this file '" + dataFile + "'.");
                }
                UUID claimID = UUID.fromString(data[0]);
                int minX = Integer.parseInt(data[1]);
                int maxX = Integer.parseInt(data[2]);
                int minY = Integer.parseInt(data[3]);
                int maxY = Integer.parseInt(data[4]);
                int minZ = Integer.parseInt(data[5]);
                int maxZ = Integer.parseInt(data[6]);
                UUID worldID = UUID.fromString(data[7]);
                UUID ownerID = UUID.fromString(data[8]);
                List<UUID> trusted = new ArrayList<>(data.length - minDataCount);
                for (int i = minDataCount; i < data.length; i++) {
                    trusted.add(UUID.fromString(data[i]));
                }

                loaded.add(new Claim(claimID, minX, maxX, minY, maxY, minZ, maxZ, worldID, ownerID, trusted));
            }
        }

        Bukkit.getServer().getLogger().log(Level.INFO, "Claims successfully loaded.");
        return loaded;
    }

    public void remove(Claim claim) {
        claims.remove(claim);
    }

    public List<Claim> getAllClaims() {
        return claims;
    }

    public void add(Claim newClaim) {
        claims.add(newClaim);
    }
}
