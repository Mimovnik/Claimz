package me.mimovnik.claimz;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static org.bukkit.Particle.SONIC_BOOM;

public class Claim {
    private int minX, maxX, minY, maxY, minZ, maxZ;
    private World world;
    private Player owner;

    public Claim(Location firstCorner, Location secondCorner, World world, Player owner) {
        this.world = world;
        this.owner = owner;
        minX = Math.min(firstCorner.getBlockX(), secondCorner.getBlockX());
        maxX = Math.max(firstCorner.getBlockX(), secondCorner.getBlockX());

        minY = Math.min(firstCorner.getBlockY(), secondCorner.getBlockY());
        maxY = Math.max(firstCorner.getBlockY(), secondCorner.getBlockY());

        minZ = Math.min(firstCorner.getBlockZ(), secondCorner.getBlockZ());
        maxZ = Math.max(firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public void display() {
        Particle particle = SONIC_BOOM;
        int count = 1;
        // X edges
        for (int x = minX; x < maxX; x++) {
            world.spawnParticle(particle, x, minY, minZ, count);
            world.spawnParticle(particle, x, minY, maxZ, count);
            world.spawnParticle(particle, x, maxY, minZ, count);
            world.spawnParticle(particle, x, maxY, maxZ, count);
        }
        // Y edges
        for (int y = minY; y < maxY; y++) {
            world.spawnParticle(particle, minX, y, minZ, count);
            world.spawnParticle(particle, minX, y, maxZ, count);
            world.spawnParticle(particle, maxX, y, minZ, count);
            world.spawnParticle(particle, maxX, y, maxZ, count);
        }
        // Z edges
        for (int z = minZ; z < maxZ; z++) {
            world.spawnParticle(particle, minX, minY, z, count);
            world.spawnParticle(particle, minX, maxY, z, count);
            world.spawnParticle(particle, maxX, minY, z, count);
            world.spawnParticle(particle, maxX, maxY, z, count);
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
}
