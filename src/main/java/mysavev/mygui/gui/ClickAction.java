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

    private static void parseAndRun(ServerPlayer player, String actionLine) {
        if (actionLine.startsWith("[console] ")) {
            String cmd = actionLine.substring(10).trim().replace("%player%", player.getName().getString());
            player.getServer().getCommands().performPrefixedCommand(player.getServer().createCommandSourceStack(), cmd);
        } else if (actionLine.startsWith("[player] ")) {
            String cmd = actionLine.substring(9).trim();
            player.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), cmd);
        } else if (actionLine.startsWith("[message] ")) {
            String msg = actionLine.substring(10).trim().replace("&", "§");
            player.sendSystemMessage(Component.literal(msg));
        } else if (actionLine.startsWith("[opengui] ")) {
            String menuName = actionLine.substring(10).trim();
            MenuManager.openMenu(player, menuName);
        } else if (actionLine.equals("[close]")) {
            player.closeContainer();
        }
    }
}

