package mysavev.mygui.economy;

import mysavev.mygui.FabricMenus;
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
	private static volatile EconomyProvider INSTANCE = new NoopEconomyService();
	private static volatile boolean INITIALIZED = false;

	private EconomyServices() {
	}

	public static EconomyProvider get() {
		// Safety: if someone calls before init(), ensure we try to initialize at least once.
		if (!INITIALIZED) {
			init();
		}
		return INSTANCE;
	}

	/**
	 * Initialize/refresh the economy provider.
	 * <p>
	 * Intended to be called on {@code ServerLifecycleEvents.SERVER_STARTING} when all mods are loaded.
	 */
	public static synchronized void init() {
		INSTANCE = create();
		INITIALIZED = true;
	}

	private static EconomyProvider create() {
		// Impactor mod id is "impactor" (as used in fabric.mod.json)
		if (FabricLoader.getInstance().isModLoaded("impactor")) {
			FabricMenus.LOGGER.info("Economy: detected 'impactor' mod. Using lazy Impactor economy provider.");
			return new ImpactorApiEconomyService();
		}
		FabricMenus.LOGGER.warn("Economy: no supported economy provider found. Using Noop economy (all transactions will fail).");
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
		private volatile EconomyService cached;

		private EconomyService getService() {
			EconomyService eco = this.cached;
			if (eco != null) return eco;

			try {
				var impactor = Impactor.instance();
				var services = impactor.services();
				// Note: we intentionally resolve lazily to avoid onInitialize timing issues.
				eco = services.provide(EconomyService.class);
				this.cached = eco;
				return eco;
			} catch (IllegalStateException e) {
				// "The Impactor API is not loaded" (API not ready yet)
				FabricMenus.LOGGER.warn("Economy: Impactor API not ready yet: {}", e.toString());
				return null;
			} catch (Throwable t) {
				FabricMenus.LOGGER.warn("Economy: failed to resolve Impactor EconomyService lazily: {}", t.toString());
				return null;
			}
		}

		@Override
		public boolean isAvailable() {
			return getService() != null;
		}

		@Override
		public BigDecimal getBalance(ServerPlayer player) {
			var eco = getService();
			if (eco == null) return BigDecimal.ZERO;
			try {
				var account = eco.account(player.getUUID()).join();
				return account.balance();
			} catch (Exception e) {
				FabricMenus.LOGGER.warn("Economy: getBalance failed for {} ({})",
						player.getName().getString(), player.getUUID(), e);
				return BigDecimal.ZERO;
			}
		}

		@Override
		public boolean withdraw(ServerPlayer player, BigDecimal amount) {
			var eco = getService();
			if (eco == null) return false;
			try {
				var account = eco.account(player.getUUID()).join();
				boolean ok = account.withdraw(amount).successful();
				if (!ok) {
					FabricMenus.LOGGER.warn("Economy: withdraw failed for {} ({}) amount={} (unsuccessful)",
							player.getName().getString(), player.getUUID(), amount);
				}
				return ok;
			} catch (Exception e) {
				FabricMenus.LOGGER.warn("Economy: withdraw exception for {} ({}) amount={}",
						player.getName().getString(), player.getUUID(), amount, e);
				return false;
			}
		}

		@Override
		public boolean deposit(ServerPlayer player, BigDecimal amount) {
			var eco = getService();
			if (eco == null) return false;
			try {
				var account = eco.account(player.getUUID()).join();
				boolean ok = account.deposit(amount).successful();
				if (!ok) {
					FabricMenus.LOGGER.warn("Economy: deposit failed for {} ({}) amount={} (unsuccessful)",
							player.getName().getString(), player.getUUID(), amount);
				}
				return ok;
			} catch (Exception e) {
				FabricMenus.LOGGER.warn("Economy: deposit exception for {} ({}) amount={}",
						player.getName().getString(), player.getUUID(), amount, e);
				return false;
			}
		}
	}
}
