package mysavev.mygui.gui;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple per-menu click throttling to prevent spam.
 */
public final class ClickGuard {
	private final Map<String, Long> lastClickTimes = new HashMap<>();
	private final long delayMs;

	public ClickGuard(long delayMs) {
		this.delayMs = delayMs;
	}

	/**
	 * @return true if the caller is allowed to proceed for the given key; false if throttled
	 */
	public boolean tryAcquire(String key) {
		long currentTime = System.currentTimeMillis();
		long lastClick = lastClickTimes.getOrDefault(key, 0L);
		if (currentTime - lastClick < delayMs) {
			return false;
		}
		lastClickTimes.put(key, currentTime);
		return true;
	}
}

