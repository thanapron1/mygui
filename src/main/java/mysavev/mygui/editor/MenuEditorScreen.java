package mysavev.mygui.editor;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.config.MenuModel;
import mysavev.mygui.util.ItemBuilder;
import mysavev.mygui.util.ColorUtil;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuEditorScreen extends SimpleGui {

    private final EditorSession session;

    public MenuEditorScreen(EditorSession session) {
        super(getScreenType(session.getMenuModel().getRows()), session.getPlayer(), false);
        this.session = session;
        this.setTitle(Component.literal("Editing: " + session.getMenuName()));
        
        render();
    }
    
    // We override open to ensure we render fresh every time
    @Override
    public void onOpen() {
        render();
    }

    private void render() {
        MenuModel model = session.getMenuModel();
        int rows = model.getRows();
        
        // 1. Clear slots
        for(int i=0; i<this.getSize(); i++) {
             this.clearSlot(i);
        }

        // 2. Render Buttons
        for (Map.Entry<String, ButtonModel> entry : model.getButtons().entrySet()) {
            String btnId = entry.getKey();
            ButtonModel btn = entry.getValue();
            
            ItemBuilder builder = new ItemBuilder(btn.getMaterial())
                    .setName(ColorUtil.parse(btn.getName()))
                    .setLoreStrings(btn.getLore())
                    .addLore("", "§e[Left] Edit", "§c[Right] Delete", "§7ID: " + btnId);
            
            for (int slot : btn.getSlots()) {
                if (slot < rows * 9) {
                    this.setSlot(slot, builder.build(), (index, clickType, action) -> {
                        if (clickType.isRight) {
                            model.getButtons().remove(btnId);
                            session.save(); // Auto-save
                            render(); // Refresh
                        } else {
                            new ButtonEditorScreen(session, btn, btnId).open();
                        }
                    });
                }
            }
        }
        
        // 3. Render Empty Slots (Add Button)
        for(int i=0; i<rows*9; i++) {
           boolean occupied = false;
           for(ButtonModel btn : model.getButtons().values()) {
               if(btn.getSlots().contains(i)) occupied = true;
           }
           if(!occupied) {
               this.setSlot(i, new ItemBuilder(Items.GRAY_STAINED_GLASS_PANE).setName("§a[+] Add Button").build(), 
                   (index, type, action) -> {
                        createNewButton(index);
                   });
           }
        }

        // 4. Render Controls (Bottom Row)
        // Controls removed as per request
    }
    
    private void createNewButton(int slot) {
        String newId = "btn_" + System.currentTimeMillis();
        List<String> lore = new ArrayList<>();
        lore.add("New button");
        ButtonModel newBtn = new ButtonModel("minecraft:stone", "New Button", lore, List.of(slot), new ArrayList<>());
        session.getMenuModel().getButtons().put(newId, newBtn);
        session.save(); // Auto-save
        
        render(); // Refresh GUI locally
        new ButtonEditorScreen(session, newBtn, newId).open();
    }

    private static MenuType<?> getScreenType(int rows) {
        return switch (Math.min(rows, 6)) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            default -> MenuType.GENERIC_9x6;
        };
    }
}
