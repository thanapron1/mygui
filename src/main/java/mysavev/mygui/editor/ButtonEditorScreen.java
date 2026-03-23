package mysavev.mygui.editor;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.util.ItemBuilder;
import mysavev.mygui.util.ColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import java.util.ArrayList;

public class ButtonEditorScreen extends SimpleGui {
    private final EditorSession session;
    private final ButtonModel button;
    private final String buttonId;

    public ButtonEditorScreen(EditorSession session, ButtonModel button, String buttonId) {
        super(MenuType.GENERIC_9x3, session.getPlayer(), false);
        this.session = session;
        this.button = button;
        this.buttonId = buttonId;
        this.setTitle(Component.literal("Edit: " + buttonId));
        render();
    }
    
    @Override
    public void onOpen() { render(); }

    private void reopen() {
        new ButtonEditorScreen(session, button, buttonId).open();
    }
 
    private void render() {
        ServerPlayer player = session.getPlayer();

        // 10: Material
        this.setSlot(10, new ItemBuilder(button.getMaterial())
                .setName("§eChange Material")
                .addLore("§7Current: " + button.getMaterial())
                .build(), (index, type, action) -> {
            new MaterialSelector(player, (selectedMaterial) -> {
                button.setMaterial(selectedMaterial);
                session.save(); // Auto-save
                reopen();
             }, this::open).open(0);
         });

        // 11: Name (Shifted from 12)
        this.setSlot(11, new ItemBuilder(Items.NAME_TAG)
                .setName("§eChange Name")
                .addLore("§7Current: " + button.getName())
                .build(), (index, type, action) -> {
            InputHandler.awaitInput(player, Component.literal("§eEnter new Name (color codes supported with &):"), (input) -> {
                button.setName(input);
                session.save(); // Auto-save
                reopen();
             }, this::open);
         });

        // 12: Lore (Shifted from 14)
        ItemBuilder loreItem = new ItemBuilder(Items.WRITABLE_BOOK).setName("§eEdit Lore");
        loreItem.addLore("§a[Left-Click] Add Line");
        loreItem.addLore("§c[Right-Click] Remove Last Line");
        loreItem.addLore("§7--- Current Lore ---§r");
        if (button.getLore() != null) {
            for(String line : button.getLore()) {
                loreItem.addLore(ColorUtil.parse(line));
            }
        } else {
            button.setLore(new ArrayList<>());
        }
        
        this.setSlot(12, loreItem.build(), (index, type, action) -> {
            if (type.isRight) {
                if (!button.getLore().isEmpty()) {
                    button.getLore().remove(button.getLore().size() - 1);
                    session.save(); // Auto-save
                    reopen(); // Refresh
                 }
            } else {
                InputHandler.awaitInput(player, Component.literal("§eEnter new Lore line:"), (input) -> {
                    button.getLore().add(input);
                    session.save(); // Auto-save
                    reopen();
                });
            }
        });

        // 13: Actions (Shifted from 16)
        ItemBuilder actionItem = new ItemBuilder(Items.COMMAND_BLOCK).setName("§eEdit Actions");
        actionItem.addLore("§a[Left-Click] Add Action");
        actionItem.addLore("§c[Right-Click] Remove Last Action");
        actionItem.addLore("§7--- Current Actions ---§r");
        if (button.getActions() != null) {
            for(String act : button.getActions()) {
                actionItem.addLore(act); // Actions are usually stored as raw strings
            }
        } else {
            button.setActions(new ArrayList<>());
        }

        this.setSlot(13, actionItem.build(), (index, type, action) -> {
            if (type.isRight) {
                if (!button.getActions().isEmpty()) {
                     button.getActions().remove(button.getActions().size() - 1);
                     session.save(); // Auto-save
                     reopen();
                }
            } else {
                player.sendSystemMessage(Component.literal("Action Examples: [console] command, [player] command, [message] text, [opengui] menu, [close]"));
                InputHandler.awaitInput(player, Component.literal("§eEnter action:"), (input) -> {
                    button.getActions().add(input);
                    session.save(); // Auto-save
                    reopen();
                });
            }
        });
        
        // 19: Amount
        this.setSlot(19, new ItemBuilder(Items.ANVIL).setName("§eSet Amount")
                .addLore("§7Current: " + button.getAmount())
                .addLore("§a[Left-Click] +1")
                .addLore("§c[Right-Click] -1")
                .build(), (index, type, action) -> {
            if (type.isRight) {
                 if(button.getAmount() > 1) {
                     button.setAmount(button.getAmount() - 1);
                     session.save();
                     reopen();
                  }
             } else {
                  if(button.getAmount() < 64) {
                     button.setAmount(button.getAmount() + 1);
                     session.save();
                     reopen();
                  }
             }
         });

        // 20: Custom Model Data
        this.setSlot(20, new ItemBuilder(Items.PAINTING).setName("§eSet Custom Model Data")
                .addLore("§7Current: " + (button.getCustomModelData() == null ? "None" : button.getCustomModelData()))
                .addLore("§a[Click to Set]")
                .addLore("§c[Right-Click to Remove]")
                .build(), (index, type, action) -> {
            if (type.isRight) {
                button.setCustomModelData(null);
                session.save();
                reopen();
             } else {
                 InputHandler.awaitInput(player, Component.literal("§eEnter CustomModelData integer:"), (input) -> {
                     try {
                         int val = Integer.parseInt(input);
                         button.setCustomModelData(val);
                         session.save();
                         reopen();
                     } catch (NumberFormatException e) {
                         player.sendSystemMessage(Component.literal("§cInvalid number."));
                         reopen();
                     }
                 }, this::open);
             }
         });

        // 21: Glow
        this.setSlot(21, new ItemBuilder(Items.GLOW_INK_SAC).setName("§eToggle Glow")
                 .addLore("§7Current: " + (button.isGlow() ? "§aTrue" : "§cFalse"))
                 .setGlow(button.isGlow())
                 .build(), (index, type, action) -> {
             button.setGlow(!button.isGlow());
             session.save();
             reopen();
         });

        // 22: Head Texture
        this.setSlot(22, new ItemBuilder(Items.PLAYER_HEAD).setName("§eSet Head Texture")
                .addLore("§7Set Base64 Skin Texture")
                .addLore("§a[Click to Set]")
                .addLore("§c[Right-Click to Remove]")
                .build(), (index, type, action) -> {
            if (type.isRight) {
                button.setHeadTexture(null);
                session.save();
                reopen();
             } else {
                 InputHandler.awaitInput(player, Component.literal("§ePaste Base64 Texture Value:"), (input) -> {
                     button.setHeadTexture(input);
                     session.save();
                     reopen();
                 }, this::open);
             }
         });

        // 26: Back Button
        this.setSlot(26, new ItemBuilder(Items.ARROW).setName("§cBack to Menu Editor").build(), (index, type, action) -> {
             new MenuEditorScreen(session).open();
        });
    }
}
