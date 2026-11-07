package com.tntflingmaster;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;

public class ItemHelper {
    public static void switchTo(PlayerEntity player, Item item) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() == item) {
                player.getInventory().selectedSlot = i;
                return;
            }
        }
    }
}
