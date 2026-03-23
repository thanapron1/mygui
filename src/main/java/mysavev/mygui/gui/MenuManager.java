package mysavev.mygui.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.config.MenuModel;
import mysavev.mygui.util.ColorUtil;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.chat.Component;

public class MenuManager {

    public static void openMenu(ServerPlayer player, String menuName) {
        MenuModel menuModel = ConfigManager.getMenu(menuName);
        if (menuModel == null) {
            player.sendSystemMessage(Component.literal("§cMenu not found: " + menuName));
            return;
        }
        new BaseMenu(player, menuModel).open();
    }
}

