package mysavev.mygui.command;

import net.minecraft.commands.CommandSourceStack;

import java.lang.reflect.Method;
import java.util.Locale;

public final class PermissionUtil {
    public static final String OPEN_ALL = "mysavev.fabricmenu.open.*";
    public static final String OPEN_PREFIX = "mysavev.fabricmenu.open.";
    public static final String EDITOR = "mysavev.fabricmenu.editor";
    public static final String RELOAD = "mysavev.fabricmenu.reload";

    private static final String PERMISSIONS_CLASS = "me.lucko.fabric.api.permissions.v0.Permissions";
    private static Method permissionsCheckMethod;

    private PermissionUtil() {
    }

    public static boolean canOpenMenu(CommandSourceStack source, String menuName) {
        if (source.hasPermission(4)) {
            return true;
        }

        String normalized = normalizeMenuName(menuName);
        return check(source, OPEN_ALL, false) || check(source, OPEN_PREFIX + normalized, false);
    }

    public static boolean canUseEditor(CommandSourceStack source) {
        return source.hasPermission(4) || check(source, EDITOR, false);
    }

    public static boolean canReload(CommandSourceStack source) {
        return source.hasPermission(4) || check(source, RELOAD, false);
    }

    private static boolean check(CommandSourceStack source, String node, boolean fallback) {
        try {
            Method method = getPermissionsCheckMethod();
            if (method == null) {
                return fallback;
            }
            Object result = method.invoke(null, source, node, fallback);
            return result instanceof Boolean && (Boolean) result;
        } catch (Throwable ignored) {
            return fallback;
        }
    }

    private static Method getPermissionsCheckMethod() {
        if (permissionsCheckMethod != null) {
            return permissionsCheckMethod;
        }

        try {
            Class<?> clazz = Class.forName(PERMISSIONS_CLASS);
            permissionsCheckMethod = clazz.getMethod("check", CommandSourceStack.class, String.class, boolean.class);
            return permissionsCheckMethod;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String normalizeMenuName(String menuName) {
        if (menuName == null) {
            return "";
        }
        return menuName.trim().toLowerCase(Locale.ROOT);
    }
}

