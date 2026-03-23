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
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuEditorScreen extends SimpleGui {

    private final EditorSession session;

    public MenuEditorScreen(EditorSession session) {
        super(getScreenType(session.getMenuModel().getRows()), session.getPlayer(), false);
        this.session = session;
        this.setTitle(Component.literal("Editing: " + session.getMenuName()));
        this.setLockPlayerInventory(false);
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
                    .setAmount(btn.getAmount())
                    .setCustomModelData(btn.getCustomModelData())
                    .setGlow(btn.isGlow())
                    .setHeadTexture(btn.getHeadTexture())
                    .addLore("", "§e[Left] Edit", "§c[Right] Delete", "§7ID: " + btnId);
            
            for (int slot : btn.getSlots()) {
                if (slot < rows * 9) {
                    this.setSlot(slot, builder.build(), (index, clickType, action) -> {
                        ItemStack source = getSourceStack();
                        if (!source.isEmpty()) {
                             updateButtonFromItem(btn, source);
                             session.save();
                             render();
                             return;
                        }

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
        
        ButtonModel newBtn;
        ItemStack cursor = getSourceStack();

        if (!cursor.isEmpty()) {
             String material = BuiltInRegistries.ITEM.getKey(cursor.getItem()).toString();
             String name = ColorUtil.serialize(cursor.getHoverName());
             lore.add("From Inventory");
             newBtn = new ButtonModel(material, name, lore, List.of(slot), new ArrayList<>());
             updateButtonFromItem(newBtn, cursor);
        } else {
             lore.add("New button");
             newBtn = new ButtonModel("minecraft:stone", "New Button", lore, List.of(slot), new ArrayList<>());
        }
        
        session.getMenuModel().getButtons().put(newId, newBtn);
        session.save(); // Auto-save
        
        render(); // Refresh GUI locally
        new ButtonEditorScreen(session, newBtn, newId).open();
    }
    
    private void updateButtonFromItem(ButtonModel btn, ItemStack stack) {
        btn.setMaterial(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
        btn.setName(ColorUtil.serialize(stack.getHoverName()));
        btn.setAmount(stack.getCount());
        
        CustomModelData cmd = stack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (cmd != null) {
            btn.setCustomModelData(cmd.value());
        } else {
            btn.setCustomModelData(null);
        }
        
        // Glow if enchanted or explicit glow component
        Boolean glint = stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE);
        if (glint != null) {
            btn.setGlow(glint);
        } else {
            btn.setGlow(stack.isEnchanted());
        }

        // Note: Preserving actions, resetting lore? 
        // User probably expects the item to look like the one in inventory.
        btn.setLore(new ArrayList<>());
        btn.getLore().add("Updated from inventory");
    }

    private ItemStack getSourceStack() {
        ItemStack cursor = this.player.containerMenu.getCarried();
        if (!cursor.isEmpty()) {
            return cursor;
        }

        int selected = this.player.getInventory().selected;
        ItemStack selectedStack = this.player.getInventory().getItem(selected);
        return selectedStack == null ? ItemStack.EMPTY : selectedStack;
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
