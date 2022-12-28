package me.mimovnik.claimz;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ClaimCubeFactory {
    private Material unitsMaterial = Material.WHITE_STAINED_GLASS;
    private Material ninesMaterial = Material.LIGHT_BLUE_STAINED_GLASS;
    private Material eightyonesMaterial = Material.PURPLE_STAINED_GLASS;

    public List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        ShapedRecipe fromUnitsToNines = new ShapedRecipe(NamespacedKey.minecraft("from_units_to_nines"), getCubeStack(1, Unit.NINES));
        fromUnitsToNines.shape(
                "111",
                "111",
                "111"
        );
        fromUnitsToNines.setIngredient('1', new RecipeChoice.ExactChoice(getCubeStack(1, Unit.UNITS)));
        recipes.add(fromUnitsToNines);

        ShapelessRecipe fromNinesToUnits = new ShapelessRecipe(NamespacedKey.minecraft("from_nines_to_units"), getCubeStack(9, Unit.UNITS));
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


    public boolean isCube(ItemStack itemStack) {
        Material type = itemStack.getType();
        ItemMeta itemMeta = itemStack.getItemMeta();
        return (type == unitsMaterial
                || type == ninesMaterial
                || type == eightyonesMaterial)
                && itemMeta != null
                && itemMeta.hasEnchant(Enchantment.LUCK);
    }
}
