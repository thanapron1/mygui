package mysavev.mygui.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import mysavev.mygui.gui.MenuManager; // Will be created
import java.util.List;
import java.math.BigDecimal;

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
    String balance = getBalancePlaceholder(player);
        return input
                .replace("%player_name%", name)
                .replace("%balance%", balance)
                .replace("%player%", name);
    }

  private static String getBalancePlaceholder(ServerPlayer player) {
    try {
      BigDecimal bal = mysavev.mygui.economy.EconomyServices.get().getBalance(player);
      if (bal == null) {
        return "0";
      }
      return bal.stripTrailingZeros().toPlainString();
    } catch (Throwable ignored) {
      return "0";
    }
  }

    private static void parseAndRun(ServerPlayer player, String actionLine) {
    actionLine = applyPlaceholders(player, actionLine);

        if (actionLine.startsWith("[console] ")) {
            String cmd = actionLine.substring(10).trim();
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
