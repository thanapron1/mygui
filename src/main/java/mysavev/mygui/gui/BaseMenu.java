package mysavev.mygui.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.config.MenuModel;
import mysavev.mygui.util.ColorUtil;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class BaseMenu extends SimpleGui {
    
    private final MenuModel model;

    public BaseMenu(ServerPlayer player, MenuModel model) {
        super(getScreenType(model.getRows()), player, false);
        this.model = model;
        this.setTitle(ColorUtil.parse(model.getTitle()));
        render();
    }

    private void render() {
        for (ButtonModel btn : model.getButtons().values()) {
            try {
                ItemBuilder builder = new ItemBuilder(btn.getMaterial())
                        .setName(ColorUtil.parse(btn.getName()))
                        .setLoreStrings(btn.getLore())
                        .setAmount(btn.getAmount())
                        .setCustomModelData(btn.getCustomModelData())
                        .setGlow(btn.isGlow())
                        .setHeadTexture(btn.getHeadTexture());
                
                for (int slot : btn.getSlots()) {
                    if (slot >= 0 && slot < model.getRows() * 9) {
                        this.setSlot(slot, builder.build(), (index, clickType, action) -> {
                            ClickAction.execute(player, btn.getActions());
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static MenuType<?> getScreenType(int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            case 6 -> MenuType.GENERIC_9x6;
            default -> MenuType.GENERIC_9x3;
        };
    }
}
