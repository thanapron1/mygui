package mysavev.mygui.editor;

import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;

public class InputHandler {
    private static final Map<UUID, Consumer<String>> callbacks = new HashMap<>();

    public static void awaitInput(ServerPlayer player, Component prompt, Consumer<String> callback) {
        callbacks.put(player.getUUID(), callback);
        player.sendSystemMessage(prompt);
        player.closeContainer();
    }

    public static boolean handleChat(ServerPlayer player, String message) {
        if (callbacks.containsKey(player.getUUID())) {
            Consumer<String> callback = callbacks.remove(player.getUUID());
            if (message.equalsIgnoreCase("cancel")) {
                player.sendSystemMessage(Component.literal("§cInput cancelled."));
            } else {
                callback.accept(message);
            }
            return true;
        }
        return false;
    }
}

