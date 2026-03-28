package mysavev.mygui.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.config.MenuModel;
import mysavev.mygui.util.ColorUtil;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class BaseMenu extends SimpleGui {
    
    private final MenuModel model;
    private final ClickGuard clickGuard = new ClickGuard(250);
    private final EconomyTransactionHandler economyHandler = new EconomyTransactionHandler();

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
                        .applyNbt(btn.getNbt())
                        .setName(ColorUtil.parse(btn.getName()))
                        .setLoreStrings(withEconomyLore(btn))
                        .setAmount(btn.getAmount())
                        .setCustomModelData(btn.getCustomModelData())
                        .setGlow(btn.isGlow())
                        .setHeadTexture(btn.getHeadTexture());
                
                for (int slot : btn.getSlots()) {
                    if (slot >= 0 && slot < model.getRows() * 9) {
                        this.setSlot(slot, builder.build(), (index, clickType, action) -> {
                            String btnKey = "btn_" + slot;
                            if (!clickGuard.tryAcquire(btnKey)) {
                                return; // Prevent spamming
                            }

                            boolean handled = economyHandler.tryHandle(player, btn, clickType.isRight);
                            if (!handled) {
                              ClickAction.execute(player, btn.getActions());
                              // We assume actions always succeed if they have any, or we can just play success sound
                              if (btn.getActions() != null && !btn.getActions().isEmpty()) {
                                  player.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER, 1.0F, 2.0F);
                              }
                            }
                            if (btn.isCloseOnClick()) {
                                this.close();
                            }
                        });
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

  private static java.util.List<String> withEconomyLore(ButtonModel btn) {
    java.util.List<String> lore = btn.getLore() != null ? new java.util.ArrayList<>(btn.getLore()) : new java.util.ArrayList<>();

    boolean showAny = false;
    if (btn.isAllowBuy() && btn.getBuyPrice().signum() > 0) {
      lore.add("§aBuy: §e" + btn.getBuyPrice().stripTrailingZeros().toPlainString());
      showAny = true;
    }
    if (showAny) {
      lore.add("§7(Left=Buy)");
    }

    return lore;
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
