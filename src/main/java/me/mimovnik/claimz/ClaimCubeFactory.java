package me.mimovnik.claimz;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.mimovnik.claimz.ClaimCubeFactory.Unit.*;

public class ClaimCubeFactory implements CommandExecutor {
    private final Material unitsMaterial = Material.WHITE_STAINED_GLASS;
    private final Material ninesMaterial = Material.LIGHT_BLUE_STAINED_GLASS;
    private final Material eightyonesMaterial = Material.PURPLE_STAINED_GLASS;

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        ShapedRecipe fromUnitsToNines = new ShapedRecipe(NamespacedKey.minecraft("from_units_to_nines"), getCubeStack(1, Unit.NINES));
        fromUnitsToNines.shape(
                "111",
                "111",
                "111"
        );
        fromUnitsToNines.setIngredient('1', new RecipeChoice.ExactChoice(getCubeStack(1, UNITS)));
        recipes.add(fromUnitsToNines);

        ShapelessRecipe fromNinesToUnits = new ShapelessRecipe(NamespacedKey.minecraft("from_nines_to_units"), getCubeStack(9, UNITS));
        fromNinesToUnits.addIngredient(new RecipeChoice.ExactChoice(getCubeStack(1, Unit.NINES)));
        recipes.add(fromNinesToUnits);

        ShapedRecipe fromNinesToEightyones = new ShapedRecipe(NamespacedKey.minecraft("from_nines_to_eightyones"), getCubeStack(1, Unit.EIGHTY_ONES));
        fromNinesToEightyones.shape(
                "999",
                "999",
                "999"
        );
        fromNinesToEightyones.setIngredient('9', new RecipeChoice.ExactChoice(getCubeStack(1, Unit.NINES)));
        recipes.add(fromNinesToEightyones);

        ShapelessRecipe fromEightyonesToNines = new ShapelessRecipe(NamespacedKey.minecraft("from_eightyones_to_nine"), getCubeStack(9, Unit.NINES));
        fromEightyonesToNines.addIngredient(new RecipeChoice.ExactChoice(getCubeStack(1, Unit.EIGHTY_ONES)));
        recipes.add(fromEightyonesToNines);

        return recipes;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            int amount = Integer.parseInt(args[0]);

            if (!player.hasPermission("claimz.toggleguardclaims")) {
                sender.sendMessage("You don't have permission to do that.");
                return true;
            }
            player.getInventory().addItem(getCubeStack(amount, Unit.EIGHTY_ONES));
            return true;
        }
//        if (args.length >= 0) {
//            return false;
//        }
        sender.sendMessage("This command can only be issued if you're a player.");
        return true;
    }

    public enum Unit {
        UNITS,
        NINES,
        EIGHTY_ONES
    }

    public @NotNull ItemStack getCubeStack(int amount, @NotNull Unit unit) {
        Material material = null;
        String displayName = "";
        switch (unit) {
            case UNITS -> {
                material = unitsMaterial;
                displayName = "Cubes";
            }
            case NINES -> {
                material = ninesMaterial;
                displayName = "9 Cubes";
            }
            case EIGHTY_ONES -> {
                material = eightyonesMaterial;
                displayName = "81 Cubes";
            }
        }
        ItemStack cubes = new ItemStack(material, amount);
        ItemMeta meta = cubes.getItemMeta();
        meta.setDisplayName(displayName);

        List<String> lore = new ArrayList<>();
        lore.add("Currency for making claims.");
        meta.setLore(lore);

        meta.addEnchant(Enchantment.LUCK, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        cubes.setItemMeta(meta);

        return cubes;
    }

    public Unit whatUnit(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }
        Material type = itemStack.getType();

        if (type == unitsMaterial) {
            return UNITS;
        }
        if (type == ninesMaterial) {
            return Unit.NINES;
        }
        if (type == eightyonesMaterial) {
            return Unit.EIGHTY_ONES;
        }

        return null;
    }


    int countCubes(Player player) {
        PlayerInventory inventory = player.getInventory();

        int cubesInInventory = 0;
        for (ItemStack item : inventory.getContents()) {
            if (!isCube(item)) {
                continue;
            }
            Unit unit = whatUnit(item);

            if (unit == UNITS) {
                cubesInInventory += item.getAmount();
            } else if (unit == NINES) {
                cubesInInventory += 9 * item.getAmount();
            } else if (unit == EIGHTY_ONES) {
                cubesInInventory += 81 * item.getAmount();
            }

        }
        return cubesInInventory;
    }

    void balanceCubes(Player player, int amountAfterTransaction) {
        PlayerInventory inventory = player.getInventory();
        // Remove all cubes
        for (ItemStack item : inventory.getContents()) {
            if (isCube(item)) {
                inventory.remove(item);
            }
        }

        int numberOfEightyones = amountAfterTransaction / 81;
        amountAfterTransaction -= numberOfEightyones * 81;
        int numberOfNines = amountAfterTransaction / 9;
        amountAfterTransaction -= numberOfNines * 9;
        int numberOfUnits = amountAfterTransaction;

        // Add amountAfterTransaction cubes
        inventory.addItem(getCubeStack(numberOfEightyones, EIGHTY_ONES));
        inventory.addItem(getCubeStack(numberOfNines, NINES));
        inventory.addItem(getCubeStack(numberOfUnits, UNITS));
    }

    public boolean isCube(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        Material type = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        return (type == unitsMaterial
                || type == ninesMaterial
                || type == eightyonesMaterial)
                && itemMeta != null
                && itemMeta.hasEnchant(Enchantment.LUCK);
    }
}
