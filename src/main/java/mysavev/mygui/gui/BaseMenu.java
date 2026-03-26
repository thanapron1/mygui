package mysavev.mygui.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.config.MenuModel;
import mysavev.mygui.util.ColorUtil;
import mysavev.mygui.util.ItemBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import java.math.BigDecimal;

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
                            boolean handled = tryHandleEconomyClick(player, btn, clickType.isRight);
                            if (!handled) {
                              ClickAction.execute(player, btn.getActions());
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

  /**
   * Economy logic:
   * - Left click = buy (if enabled)
   * - Right click = sell (if enabled)
   */
  private boolean tryHandleEconomyClick(ServerPlayer player, ButtonModel btn, boolean rightClick) {
    boolean isSell = rightClick;
    if (isSell && !btn.isAllowSell()) return false;
    if (!isSell && !btn.isAllowBuy()) return false;

    BigDecimal price = isSell ? btn.getSellPrice() : btn.getBuyPrice();
    if (price.signum() <= 0) {
      return false; // Free implies no economy transaction
    }

    ItemStack item = new ItemBuilder(btn.getMaterial())
        .setName(ColorUtil.parse(btn.getName()))
        .setLoreStrings(btn.getLore())
        .setAmount(Math.max(1, btn.getAmount()))
        .setCustomModelData(btn.getCustomModelData())
        .setGlow(btn.isGlow())
        .setHeadTexture(btn.getHeadTexture())
        .build();

	  BigDecimal amount = price;
    if (!isSell) {
      // BUY
      BigDecimal bal = mysavev.mygui.economy.EconomyServices.get().getBalance(player);
      if (bal == null) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cEconomy not available."));
        return true;
      }
      if (bal.compareTo(amount) < 0) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNot enough money."));
        return true;
      }

      boolean ok = mysavev.mygui.economy.EconomyServices.get().withdraw(player, amount);
      if (!ok) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cTransaction failed."));
        return true;
      }

      if (!player.getInventory().add(item)) {
        // refund if inventory full
        mysavev.mygui.economy.EconomyServices.get().deposit(player, amount);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cInventory full."));
        return true;
      }
      player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aBought for §e" + price.stripTrailingZeros().toPlainString()));
      return true;
    } else {
      // SELL
      if (!hasAtLeast(player, item, item.getCount())) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cNot enough items to sell."));
        return true;
      }
      removeItems(player, item, item.getCount());
      boolean ok = mysavev.mygui.economy.EconomyServices.get().deposit(player, amount);
      if (!ok) {
        // rollback if deposit failed
        player.getInventory().add(item);
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§cEconomy not available."));
        return true;
      }
      player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§aSold for §e" + price.stripTrailingZeros().toPlainString()));
      return true;
    }
  }

  private static boolean hasAtLeast(ServerPlayer player, ItemStack want, int count) {
    int left = count;
    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      ItemStack s = player.getInventory().getItem(i);
      if (ItemStack.isSameItemSameComponents(s, want)) {
        left -= s.getCount();
        if (left <= 0) return true;
      }
    }
    return false;
  }

  private static void removeItems(ServerPlayer player, ItemStack want, int count) {
    int left = count;
    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      ItemStack s = player.getInventory().getItem(i);
      if (ItemStack.isSameItemSameComponents(s, want)) {
        int take = Math.min(left, s.getCount());
        s.shrink(take);
        left -= take;
        if (left <= 0) return;
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
