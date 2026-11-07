package com.tntflingmaster;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    public static KeyBinding flingKey;

    public static void register() {
        flingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.tntmaster.fling", 
            InputUtil.Type.KEYSYM, 
            GLFW.GLFW_KEY_G, 
            "TNT FLING MASTER"
        ));
    }
}
