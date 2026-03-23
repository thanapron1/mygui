package mysavev.mygui.editor;

import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;

public class InputHandler {
    private static final Map<UUID, Consumer<String>> callbacks = new HashMap<>();
    private static final Map<UUID, Runnable> cancelCallbacks = new HashMap<>();

    public static void awaitInput(ServerPlayer player, Component prompt, Consumer<String> callback, Runnable onCancel) {
        callbacks.put(player.getUUID(), callback);
        if (onCancel != null) cancelCallbacks.put(player.getUUID(), onCancel);
        player.sendSystemMessage(prompt);
        player.closeContainer();
    }
    
    public static void awaitInput(ServerPlayer player, Component prompt, Consumer<String> callback) {
        awaitInput(player, prompt, callback, null);
    }

    public static boolean handleChat(ServerPlayer player, String message) {
        if (callbacks.containsKey(player.getUUID())) {
            Consumer<String> callback = callbacks.remove(player.getUUID());
            Runnable cancelCallback = cancelCallbacks.remove(player.getUUID());
            
            if (message.equalsIgnoreCase("cancel")) {
                if (cancelCallback != null) {
                    cancelCallback.run();
                } else {
                    player.sendSystemMessage(Component.literal("§cInput cancelled."));
                }
            } else {
                callback.accept(message);
            }
            return true;
        }
        return false;
    }
}
