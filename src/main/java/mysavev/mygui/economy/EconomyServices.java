package mysavev.mygui.economy;

import net.fabricmc.loader.api.FabricLoader;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.economy.EconomyService;
import net.minecraft.server.level.ServerPlayer;

import java.math.BigDecimal;

/**
 * Central economy access point.
 *
 * Best-practice: all economy calls go through this facade, so GUI/action code does not
 * depend on a specific economy implementation.
 */
public final class EconomyServices {
	private static final EconomyProvider INSTANCE = create();

	private EconomyServices() {
	}

	public static EconomyProvider get() {
		return INSTANCE;
	}

	private static EconomyProvider create() {
		// Impactor mod id is "impactor" (as used in fabric.mod.json)
		if (FabricLoader.getInstance().isModLoaded("impactor")) {
			EconomyProvider svc = new ImpactorApiEconomyService();
			if (svc.isAvailable()) {
				return svc;
			}
		}
		return new NoopEconomyService();
	}

	public interface EconomyProvider {
		boolean isAvailable();

		BigDecimal getBalance(ServerPlayer player);

		boolean withdraw(ServerPlayer player, BigDecimal amount);

		boolean deposit(ServerPlayer player, BigDecimal amount);
	}

	private static final class NoopEconomyService implements EconomyProvider {
		@Override
		public boolean isAvailable() {
			return false;
		}

		@Override
		public BigDecimal getBalance(ServerPlayer player) {
			return BigDecimal.ZERO;
		}

		@Override
		public boolean withdraw(ServerPlayer player, BigDecimal amount) {
			return false;
		}

		@Override
		public boolean deposit(ServerPlayer player, BigDecimal amount) {
			return false;
		}
	}

	private static final class ImpactorApiEconomyService implements EconomyProvider {
		private EconomyService economy() {
			try {
				// Common pattern: Impactor.getInstance().services().provide(EconomyService.class)
				// (Exact API may vary; if this doesn't compile we'll adjust after checking actual methods.)
				var impactor = Impactor.instance();
				var services = impactor.services();
				return services.provide(EconomyService.class);
			} catch (Throwable t) {
				return null;
			}
		}

		@Override
		public boolean isAvailable() {
			return economy() != null;
		}

		@Override
		public BigDecimal getBalance(ServerPlayer player) {
			var eco = economy();
			if (eco == null) return null;
			try {
				var account = eco.account(player.getUUID()).join();
				return account.balance();
			} catch (Exception e) {
				return BigDecimal.ZERO;
			}
		}

		@Override
		public boolean withdraw(ServerPlayer player, BigDecimal amount) {
			var eco = economy();
			if (eco == null) return false;
			try {
				var account = eco.account(player.getUUID()).join();
				return account.withdraw(amount).successful();
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		public boolean deposit(ServerPlayer player, BigDecimal amount) {
			var eco = economy();
			if (eco == null) return false;
			try {
				var account = eco.account(player.getUUID()).join();
				return account.deposit(amount).successful();
			} catch (Exception e) {
				return false;
			}
		}
	}
}
