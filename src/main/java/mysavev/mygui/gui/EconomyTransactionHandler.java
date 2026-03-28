package mysavev.mygui.gui;

import mysavev.mygui.config.ButtonModel;
import mysavev.mygui.util.ColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.math.BigDecimal;

/**
 * Handles buy/sell economy clicks for a button.
 */
public final class EconomyTransactionHandler {

	/**
	 * Economy logic:
	 * - Left click = buy (if enabled)
	 *
	 * @return true if this handler consumed the click (even on failure); false if not applicable.
	 */
	public boolean tryHandle(ServerPlayer player, ButtonModel btn, boolean rightClick) {
		// SELL is removed completely
		if (rightClick) return false;
		if (!btn.isAllowBuy()) return false;

		BigDecimal price = btn.getBuyPrice();
		if (price.signum() <= 0) {
			return false; // Free implies no economy transaction
		}

		BigDecimal amount = price;
		// BUY (command-only)
		BigDecimal bal = mysavev.mygui.economy.EconomyServices.get().getBalance(player);
		if (bal == null) {
			player.sendSystemMessage(Component.literal("§cEconomy not available."));
			player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0F, 1.0F);
			return true;
		}
		if (bal.compareTo(amount) < 0) {
			player.sendSystemMessage(Component.literal("§cNot enough money."));
			player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0F, 1.0F);
			return true;
		}

		boolean ok = mysavev.mygui.economy.EconomyServices.get().withdraw(player, amount);
		if (!ok) {
			player.sendSystemMessage(Component.literal("§cTransaction failed."));
			player.playNotifySound(SoundEvents.VILLAGER_NO, SoundSource.MASTER, 1.0F, 1.0F);
			return true;
		}

		// After payment, execute the button's normal actions
		ClickAction.execute(player, btn.getActions());
		player.sendSystemMessage(Component.literal("§aBought for §e" + price.stripTrailingZeros().toPlainString()));
		player.playNotifySound(SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.MASTER, 1.0F, 2.0F);
		return true;
	}
}


