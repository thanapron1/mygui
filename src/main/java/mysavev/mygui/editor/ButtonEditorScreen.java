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
                this.open();
            }, this::open).open(0);
        });

        // 12: Name
        this.setSlot(12, new ItemBuilder(Items.NAME_TAG)
                .setName("§eChange Name")
                .addLore("§7Current: " + button.getName())
                .build(), (index, type, action) -> {
            InputHandler.awaitInput(player, Component.literal("§eEnter new Name (color codes supported with &):"), (input) -> {
                button.setName(input);
                session.save(); // Auto-save
                this.open();
            });
        });

        // 14: Lore
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
        
        this.setSlot(14, loreItem.build(), (index, type, action) -> {
            if (type.isRight) {
                if (!button.getLore().isEmpty()) {
                    button.getLore().remove(button.getLore().size() - 1);
                    session.save(); // Auto-save
                    this.open(); // Refresh
                }
            } else {
                InputHandler.awaitInput(player, Component.literal("§eEnter new Lore line:"), (input) -> {
                    button.getLore().add(input);
                    session.save(); // Auto-save
                    this.open();
                });
            }
        });

        // 16: Actions
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

        this.setSlot(16, actionItem.build(), (index, type, action) -> {
            if (type.isRight) {
                if (!button.getActions().isEmpty()) {
                     button.getActions().remove(button.getActions().size() - 1);
                     session.save(); // Auto-save
                     this.open();
                }
            } else {
                player.sendSystemMessage(Component.literal("Action Examples: [console] command, [player] command, [message] text, [opengui] menu, [close]"));
                InputHandler.awaitInput(player, Component.literal("§eEnter action:"), (input) -> {
                    button.getActions().add(input);
                    session.save(); // Auto-save
                    this.open();
                });
            }
        });
        
        // Back Button
        this.setSlot(22, new ItemBuilder(Items.ARROW).setName("§cBack to Menu Editor").build(), (index, type, action) -> {
             new MenuEditorScreen(session).open();
        });
    }
}
