package me.mimovnik.claimz;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.bukkit.Particle.REDSTONE;

public class Claim {
    private int minX, maxX, minY, maxY, minZ, maxZ;
    private UUID claimID;
    private UUID worldID;
    private UUID ownerID;
    private List<UUID> trusted;

    public UUID getWorldID() {
        return worldID;
    }

    public Claim(@NotNull Location firstVertex, @NotNull Location secondVertex, UUID worldID, UUID ownerID) {
        claimID = UUID.randomUUID();
        this.worldID = worldID;
        this.ownerID = ownerID;
        trusted = new ArrayList<>();
        minX = Math.min(firstVertex.getBlockX(), secondVertex.getBlockX());
        maxX = Math.max(firstVertex.getBlockX(), secondVertex.getBlockX());

        minY = Math.min(firstVertex.getBlockY(), secondVertex.getBlockY());
        maxY = Math.max(firstVertex.getBlockY(), secondVertex.getBlockY());

        minZ = Math.min(firstVertex.getBlockZ(), secondVertex.getBlockZ());
        maxZ = Math.max(firstVertex.getBlockZ(), secondVertex.getBlockZ());
    }

    public Claim(UUID claimID, int minX, int maxX, int minY, int maxY, int minZ, int maxZ, UUID worldID, UUID ownerID, List<UUID> trusted) {
        this.claimID = claimID;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        this.worldID = worldID;
        this.ownerID = ownerID;
        this.trusted = trusted;
    }

    public boolean addTrustedId(UUID id) {
        if (trusted.contains(id) || ownerID == id) {
            return false;
        }
        trusted.add(id);
        return true;
    }

    public boolean removeTrustedId(UUID id) {
        if (trusted.contains(id)) {
            trusted.remove(id);
            return true;
        }
        return false;
    }

    public List<UUID> getTrusted(){
        return trusted;
    }

    public Location getCenter() {
        return new Location(Bukkit.getWorld(worldID), (float) (minX + maxX) / 2, (float) (minY + maxY) / 2, (float) (minZ + maxZ) / 2);
    }

    public void saveToFile() {
        try {
            trySave();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeFromFile() {
        try {
            tryRemove();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void tryRemove() throws IOException {
        File dataFile = new File(Bukkit.getServer().getPluginManager().getPlugin("Claimz").getDataFolder() + File.separator + "Claimz.data");
        Logger logger = Bukkit.getServer().getLogger();
        if (!dataFile.exists()) {
            logger.log(Level.INFO, "Could not remove claim: " + this + " because this file '" + dataFile + "' does not exist.");
            return;
        }

        File tempFile = new File(dataFile + "temp");
        createNewFileWithoutThisClaim(dataFile, tempFile);

        dataFile.delete();
        tempFile.renameTo(dataFile);
        logger.log(Level.INFO, "Successfully removed claim: " + this);
    }

    private void trySave() throws IOException {
        File dir = Bukkit.getServer().getPluginManager().getPlugin("Claimz").getDataFolder();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dataFile = new File(dir + File.separator + "Claimz.data");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }

        File tempFile = new File(dataFile + "temp");

        // Remove old data
        createNewFileWithoutThisClaim(dataFile, tempFile);
        // Insert new data
        appendClaimData(tempFile);

        dataFile.delete();
        tempFile.renameTo(dataFile);
    }

    private void createNewFileWithoutThisClaim(@NotNull File source, @NotNull File destination) throws IOException {
        try (Scanner scanner = new Scanner(source);
             FileWriter fileWriter = new FileWriter(destination)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains(claimID.toString())) {
                    continue;
                }
                fileWriter.write(line + '\n');
            }
        }
    }

    private void appendClaimData(@NotNull File file) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.write(this.toSaveFormat() + '\n');
            Bukkit.getServer().getLogger().log(Level.INFO, "Claim (" + claimID + ") data successfully saved.");
        }
    }


    public boolean isVertex(@NotNull Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return (x == minX && y == minY && z == minZ) ||
                (x == minX && y == minY && z == maxZ) ||
                (x == minX && y == maxY && z == maxZ) ||
                (x == minX && y == maxY && z == minZ) ||
                (x == maxX && y == minY && z == minZ) ||
                (x == maxX && y == minY && z == maxZ) ||
                (x == maxX && y == maxY && z == maxZ) ||
                (x == maxX && y == maxY && z == minZ);
    }

    public boolean intersects(@NotNull Claim other) {
        return minX < other.maxX &&
                maxX > other.minX &&
                minY < other.maxY &&
                maxY > other.minY &&
                minZ < other.maxZ &&
                maxZ > other.minZ;
    }

    public boolean hasPermission(Player player) {
        return player.getUniqueId().equals(ownerID) || trusted.contains(player.getUniqueId());
    }

    public void display(Color color) {

        int count = 1;
        float size = 5;
        double offset = 0.5;
        World world = Bukkit.getWorld(worldID);
        // X edges
        for (int x = minX; x <= maxX; x++) {
            world.spawnParticle(REDSTONE, x + offset, minY + offset, minZ + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, x + offset, minY + offset, maxZ + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, x + offset, maxY + offset, minZ + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, x + offset, maxY + offset, maxZ + offset, count, new Particle.DustOptions(color, size));
        }
        // Y edges
        for (int y = minY; y <= maxY; y++) {
            world.spawnParticle(REDSTONE, minX + offset, y + offset, minZ + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, minX + offset, y + offset, maxZ + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, maxX + offset, y + offset, minZ + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, maxX + offset, y + offset, maxZ + offset, count, new Particle.DustOptions(color, size));
        }
        // Z edges
        for (int z = minZ; z <= maxZ; z++) {
            world.spawnParticle(REDSTONE, minX + offset, minY + offset, z + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, minX + offset, maxY + offset, z + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, maxX + offset, minY + offset, z + offset, count, new Particle.DustOptions(color, size));
            world.spawnParticle(REDSTONE, maxX + offset, maxY + offset, z + offset, count, new Particle.DustOptions(color, size));
        }
    }

    @Override
    public String toString() {
        return "Claim: " +
                " minX=" + minX +
                " maxX=" + maxX +
                " minY=" + minY +
                " maxY=" + maxY +
                " minZ=" + minZ +
                " maxZ=" + maxZ;
    }

    private String toSaveFormat() {
        String saveFormat = claimID +
                ";" + minX +
                ";" + maxX +
                ";" + minY +
                ";" + maxY +
                ";" + minZ +
                ";" + maxZ +
                ";" + worldID +
                ";" + ownerID;
        for (UUID trust : trusted) {
            saveFormat += ";" + trust;
        }
        return saveFormat;
    }

    public UUID getOwnerID() {
        return ownerID;
    }

    public boolean contains(@NotNull Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return (x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ);
    }

    public Location getOpposingVertex(@NotNull Location vertex) {
        int x = minX;
        int y = minY;
        int z = minZ;
        if (vertex.getBlockX() == x) {
            x = maxX;
        }
        if (vertex.getBlockY() == y) {
            y = maxY;
        }
        if (vertex.getBlockZ() == z) {
            z = maxZ;
        }
        return new Location(Bukkit.getWorld(worldID), x, y, z, 0, 0);
    }

    public void setNewBoundaries(@NotNull Location firstVertex, @NotNull Location secondVertex) {
        minX = Math.min(firstVertex.getBlockX(), secondVertex.getBlockX());
        maxX = Math.max(firstVertex.getBlockX(), secondVertex.getBlockX());

        minY = Math.min(firstVertex.getBlockY(), secondVertex.getBlockY());
        maxY = Math.max(firstVertex.getBlockY(), secondVertex.getBlockY());

        minZ = Math.min(firstVertex.getBlockZ(), secondVertex.getBlockZ());
        maxZ = Math.max(firstVertex.getBlockZ(), secondVertex.getBlockZ());
    }

    public int getVolume() {
        int edgeX = maxX - minX + 1;
        int edgeY = maxY - minY + 1;
        int edgeZ = maxZ - minZ + 1;
        return edgeX * edgeY * edgeZ;
    }
}
