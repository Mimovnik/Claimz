package me.mimovnik.claimz;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static me.mimovnik.claimz.ClaimCubeFactory.Unit.EIGHTY_ONES;

public final class Claimz extends JavaPlugin implements Listener, CommandExecutor {

    private ClaimContainer claimContainer;
    private ClaimRenderer renderer;
    private final Map<UUID, ClaimEditor> claimEditors = new HashMap<>();
    private ClaimCubeFactory factory;
    private ClaimGuard claimGuard;
    private Material editorTool = Material.STICK;

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        claimContainer = ClaimContainer.loadFromFile(getDataFolder());
        claimGuard = new ClaimGuard(claimContainer);

        factory = new ClaimCubeFactory();
        renderer = new ClaimRenderer(claimContainer);


        pluginManager.registerEvents(claimGuard, this);
        pluginManager.registerEvents(this, this);
        getCommand("deleteClaim").setExecutor(new DeleteClaim(claimContainer, factory));
        getCommand("toggleShowClaims").setExecutor(new ClaimRenderer(claimContainer));
        getCommand("giveCubes").setExecutor(factory);
        getCommand("toggleGuardClaims").setExecutor(claimGuard);
        getCommand("trust").setExecutor(new TrustPlayer(claimContainer));
        getCommand("distrust").setExecutor(new DistrustPlayer(claimContainer));
        getCommand("showTrusted").setExecutor(new ShowTrustedPlayers(claimContainer));
        getCommand("claimzTips").setExecutor(this);

        for (Recipe recipe : factory.getRecipes()) {
            getServer().addRecipe(recipe);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Give starting amount of cubes on first login
        if (!player.hasPlayedBefore()) {
            player.getInventory().addItem(factory.getCubeStack(32, EIGHTY_ONES));
            sendClaimzTips(player);
        }

        // Give every player his own Claim Editor
        UUID uniqueId = player.getUniqueId();
        claimEditors.computeIfAbsent(uniqueId, (key) -> {
            ClaimEditor claimEditor = new ClaimEditor(claimContainer, renderer, key, factory, editorTool);
            getServer().getPluginManager().registerEvents(claimEditor, this);
            Bukkit.getServer().getLogger().log(Level.INFO, "Claimz: Successfully created a new claim editor for " + player.getName());
            return claimEditor;
        });
    }

    private void sendClaimzTips(Player player) {
        player.sendMessage(ChatColor.GREEN + "There is the Claimz plugin on this server. You can claim a land to protect it from strangers.");
        player.sendMessage(ChatColor.YELLOW + "(All actions with " + editorTool.name() + " in the main hand)");
        player.sendMessage(ChatColor.YELLOW + "To show borders hold an editor tool in the main hand.");
        player.sendMessage(ChatColor.YELLOW + "To claim right click two opposing vertices of a prism.");
        player.sendMessage(ChatColor.YELLOW + "To edit right click existing vertex.");
        player.sendMessage(ChatColor.YELLOW + "To cancel left click anywhere.");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (factory.isCube(item)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            return false;
        }
        if (sender instanceof Player player) {
            sendClaimzTips(player);
        }
        else {
            sender.sendMessage("This command can only be issued if you're a player.");
        }
        return true;
    }
}
