package mysavev.mygui.editor;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;

public class MainEditorScreen extends SimpleGui {
    
    public MainEditorScreen(ServerPlayer player) {
        super(MenuType.GENERIC_9x6, player, false);
        this.setTitle(Component.literal("All Menus"));
        render();
    }
    
    private void render() {
        int index = 0;
        for (String menuName : ConfigManager.getMenuNames()) {
            this.setSlot(index, new ItemBuilder(Items.CHEST).setName("§e" + menuName).build(), 
                (i, type, action) -> {
                    EditorSession.start(player, menuName);
            });
            index++;
        }
        
        // Add "Create New Menu" button at the last slot (53)
        this.setSlot(53, new ItemBuilder(Items.EMERALD_BLOCK)
                .setName("§a[+] Create New Menu")
                .addLore("§7Click to create a new menu.")
                .build(), 
            (i, type, action) -> {
                new NewMenuScreen(player).open();
            });
    }
}
