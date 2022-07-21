package com.github.arnhav.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {

    public static String itemToString(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    public static ItemStack stringToItem(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(string);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i");
    }

}
