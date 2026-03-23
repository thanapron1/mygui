package mysavev.mygui.util;

import net.minecraft.network.chat.Component;

public class ColorUtil {
    public static Component parse(String text) {
        if (text == null) return Component.empty();
        // Simple translation for now, can be expanded to hex colors later
        return Component.literal(text.replace("&", "§"));
    }

    public static String serialize(Component component) {
        return component.getString().replace("§", "&");
    }
}

