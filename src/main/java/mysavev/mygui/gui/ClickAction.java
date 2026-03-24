package mysavev.mygui.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import mysavev.mygui.gui.MenuManager; // Will be created
import java.util.List;

public class ClickAction {
    
    public enum ActionType {
        CONSOLE, PLAYER, MESSAGE, OPEN_GUI, CLOSE, UNKNOWN
    }

    public static void execute(ServerPlayer player, List<String> actions) {
        if (actions == null) return;
        for (String action : actions) {
            parseAndRun(player, action);
        }
    }

    private static String applyPlaceholders(ServerPlayer player, String input) {
        if (input == null) return "";
        String name = player.getName().getString();
        return input
                .replace("%player_name%", name)
                .replace("%player%", name);
    }

    private static void parseAndRun(ServerPlayer player, String actionLine) {
        if (actionLine.startsWith("[console] ")) {
            String cmd = applyPlaceholders(player, actionLine.substring(10).trim());
            player.getServer().getCommands().performPrefixedCommand(player.getServer().createCommandSourceStack(), cmd);
        } else if (actionLine.startsWith("[player] ")) {
            String cmd = applyPlaceholders(player, actionLine.substring(9).trim());
            player.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), cmd);
        } else if (actionLine.startsWith("[message] ")) {
            String msg = applyPlaceholders(player, actionLine.substring(10).trim()).replace("&", "§");
            player.sendSystemMessage(Component.literal(msg));
        } else if (actionLine.startsWith("[opengui] ")) {
            String menuName = applyPlaceholders(player, actionLine.substring(10).trim());
            MenuManager.openMenu(player, menuName);
        } else if (actionLine.equals("[close]")) {
            player.closeContainer();
        }
    }
}
