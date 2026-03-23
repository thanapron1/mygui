package mysavev.mygui.editor;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.config.MenuModel;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

import java.util.HashMap;

public class NewMenuScreen extends SimpleGui {
    private String menuName;
    private int menuRows;

    public NewMenuScreen(ServerPlayer player) {
        this(player, "new_menu_" + System.currentTimeMillis(), 3);
    }

    public NewMenuScreen(ServerPlayer player, String menuName, int menuRows) {
        super(MenuType.GENERIC_9x3, player, false);
        this.menuName = menuName;
        this.menuRows = menuRows;
        this.setTitle(Component.literal("Create New Menu"));
        render();
    }

    private void render() {
        // Name Button (Slot 11)
        this.setSlot(11, new ItemBuilder(Items.NAME_TAG)
                .setName("§eSet Name")
                .addLore("§7Current: §f" + menuName)
                .addLore("§a[Click to Change]")
                .build(), 
            (index, type, action) -> {
                InputHandler.awaitInput(player, Component.literal("§eEnter menu file name (no spaces):"), (input) -> {
                    // Basic validation
                    String cleanName = input.replaceAll("[^a-zA-Z0-9_.-]", "");
                    if (cleanName.isEmpty()) {
                        player.sendSystemMessage(Component.literal("§cInvalid name."));
                        new NewMenuScreen(player, menuName, menuRows).open();
                        return;
                    }
                    if (ConfigManager.getMenu(cleanName) != null) {
                         player.sendSystemMessage(Component.literal("§cMenu with that name already exists!"));
                         new NewMenuScreen(player, menuName, menuRows).open();
                         return;
                    }
                    new NewMenuScreen(player, cleanName, menuRows).open();
                });
            });

        // Size Button (Slot 13)
        this.setSlot(13, new ItemBuilder(Items.CHEST)
                .setName("§eSet Size")
                .addLore("§7Current: §f" + menuRows + " Rows")
                .addLore("§a[Click to Cycle (1-6)]")
                .build(), 
            (index, type, action) -> {
                int newRows = menuRows + 1;
                if (newRows > 6) newRows = 1;
                menuRows = newRows;
                render(); // Re-render to update lore
            });

        // Create Button (Slot 15)
        this.setSlot(15, new ItemBuilder(Items.EMERALD_BLOCK)
                .setName("§a[Create Menu]")
                .addLore("§7Name: " + menuName)
                .addLore("§7Rows: " + menuRows)
                .build(),
            (index, type, action) -> {
                if (ConfigManager.getMenu(menuName) != null) {
                    player.sendSystemMessage(Component.literal("§cMenu '" + menuName + "' already exists!"));
                    return;
                }
                
                // Create and Save
                MenuModel newMenu = new MenuModel(menuName, menuRows);
                newMenu.setButtons(new HashMap<>()); // Initialize empty map
                ConfigManager.saveMenu(menuName, newMenu);
                
                player.sendSystemMessage(Component.literal("§aMenu created successfully!"));
                // Open Editor
                EditorSession.start(player, menuName);
            });

        // Cancel/Back (Slot 22)
        this.setSlot(22, new ItemBuilder(Items.BARRIER).setName("§cCancel").build(), 
            (index, type, action) -> {
                new MainEditorScreen(player).open();
            });
    }
}

