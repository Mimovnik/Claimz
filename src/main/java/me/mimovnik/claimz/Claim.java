package me.mimovnik.claimz;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

import static org.bukkit.Particle.ELECTRIC_SPARK;
import static org.bukkit.Particle.SONIC_BOOM;

public class Claim {
    private int minX, maxX, minY, maxY, minZ, maxZ;
    private World world;

    public Claim(Location firstCorner, Location secondCorner, World world) {
        this.world = world;
        minX = Math.min(firstCorner.getBlockX(), secondCorner.getBlockX());
        maxX = Math.max(firstCorner.getBlockX(), secondCorner.getBlockX());

        minY = Math.min(firstCorner.getBlockY(), secondCorner.getBlockY());
        maxY = Math.max(firstCorner.getBlockY(), secondCorner.getBlockY());

        minZ = Math.min(firstCorner.getBlockZ(), secondCorner.getBlockZ());
        maxZ = Math.max(firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public void display() {
        Particle particle = SONIC_BOOM;

        // X edges
        for (int x = minX; x < maxX; x++) {
            world.spawnParticle(particle, x, minY, minZ, 10);
            world.spawnParticle(particle, x, minY, maxZ, 10);
            world.spawnParticle(particle, x, maxY, minZ, 10);
            world.spawnParticle(particle, x, maxY, maxZ, 10);
        }
        // Y edges
        for (int y = minY; y < maxY; y++) {
            world.spawnParticle(particle, minX, y, minZ, 10);
            world.spawnParticle(particle, minX, y, maxZ, 10);
            world.spawnParticle(particle, maxX, y, minZ, 10);
            world.spawnParticle(particle, maxX, y, maxZ, 10);
        }
        // Z edges
        for (int z = minZ; z < maxZ; z++) {
            world.spawnParticle(particle, minX, minY, z, 10);
            world.spawnParticle(particle, minX, maxY, z, 10);
            world.spawnParticle(particle, maxX, minY, z, 10);
            world.spawnParticle(particle, maxX, maxY, z, 10);
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
}
