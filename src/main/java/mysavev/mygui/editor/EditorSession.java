package mysavev.mygui.editor;

import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.config.MenuModel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorSession {
    private static final Map<UUID, EditorSession> activeSessions = new HashMap<>();

    private final ServerPlayer player;
    private final String menuName;
    private final MenuModel menuModel;

    public EditorSession(ServerPlayer player, String menuName, MenuModel menuModel) {
        this.player = player;
        this.menuName = menuName;
        this.menuModel = menuModel;
    }

    public static void start(ServerPlayer player, String menuName) {
        MenuModel menu = ConfigManager.getMenu(menuName);
        if (menu == null) {
            player.sendSystemMessage(Component.literal("§cMenu not found: " + menuName));
            return;
        }
        EditorSession session = new EditorSession(player, menuName, menu);
        activeSessions.put(player.getUUID(), session);
        new MenuEditorScreen(session).open();
    }
    
    public static EditorSession get(ServerPlayer player) {
        return activeSessions.get(player.getUUID());
    }

    public ServerPlayer getPlayer() { return player; }
    public String getMenuName() { return menuName; }
    public MenuModel getMenuModel() { return menuModel; }
    
    public void save() {
        ConfigManager.saveMenu(menuName, menuModel);
        // Do not notify on every save to avoid spam
    }

    public void close() {
        activeSessions.remove(player.getUUID());
    }
}
