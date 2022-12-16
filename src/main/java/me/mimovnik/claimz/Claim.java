package me.mimovnik.claimz;

import org.bukkit.*;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static org.bukkit.Particle.*;

public class Claim {
    private int minX, maxX, minY, maxY, minZ, maxZ;
    private World world;
    private Player owner;
    private static ArrayList<Claim> claims;

    public Claim(Location firstVertex, Location secondVertex, World world, Player owner) {
        this.world = world;
        this.owner = owner;
        minX = Math.min(firstVertex.getBlockX(), secondVertex.getBlockX());
        maxX = Math.max(firstVertex.getBlockX(), secondVertex.getBlockX());

        minY = Math.min(firstVertex.getBlockY(), secondVertex.getBlockY());
        maxY = Math.max(firstVertex.getBlockY(), secondVertex.getBlockY());

        minZ = Math.min(firstVertex.getBlockZ(), secondVertex.getBlockZ());
        maxZ = Math.max(firstVertex.getBlockZ(), secondVertex.getBlockZ());
    }

    public void edit(Location newVertex) {

    }

    public static Claim getClaimAt(Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location)) {
                return claim;
            }
        }
        return null;
    }

    public static boolean hasNOTPermission(Player player, Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location) && player != claim.getOwner()) {
                player.sendMessage(ChatColor.RED + "You don't have permission to do that. It's " +
                        ChatColor.ITALIC + "" + ChatColor.YELLOW + claim.getOwner().getName() + ChatColor.RED + "'s claim.");
                return true;
            }
        }
        return false;
    }

    public static boolean isInAnyClaim(Location location) {
        for (Claim claim : claims) {
            if (claim.contains(location)) {
                return true;
            }
        }
        return false;
    }

    public static void setClaims(ArrayList<Claim> claims) {
        Claim.claims = claims;
    }

    public boolean isVertex(Location location) {
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

    public boolean intersects(Claim other) {
        return minX < other.maxX &&
                maxX > other.minX &&
                minY < other.maxY &&
                maxY > other.minY &&
                minZ < other.maxZ &&
                maxZ > other.minZ;
    }

    public void display() {

        int count = 1;
        float size = 5;
        Color color = Color.RED;
        double offset = 0.5;
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
        return "Claim{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", minZ=" + minZ +
                ", maxZ=" + maxZ +
                '}';
    }

    public Player getOwner() {
        return owner;
    }

    public boolean contains(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        return (x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ);
    }

    public Location getOpposingVertex(Location vertex) {
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
        return new Location(world, x, y, z, 0, 0);
    }

    public void setNewBoundaries(Location firstVertex, Location secondVertex) {
        minX = Math.min(firstVertex.getBlockX(), secondVertex.getBlockX());
        maxX = Math.max(firstVertex.getBlockX(), secondVertex.getBlockX());

        minY = Math.min(firstVertex.getBlockY(), secondVertex.getBlockY());
        maxY = Math.max(firstVertex.getBlockY(), secondVertex.getBlockY());

        minZ = Math.min(firstVertex.getBlockZ(), secondVertex.getBlockZ());
        maxZ = Math.max(firstVertex.getBlockZ(), secondVertex.getBlockZ());
    }
}
