package mysavev.mygui.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.gui.MenuManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class MenuCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("menu")
                .then(Commands.literal("open")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getMenuNames(), builder))
                        .executes(MenuCommand::openMenu)
                    )
                )
                .then(Commands.literal("reload")
                    .requires(PermissionUtil::canReload)
                    .executes(context -> {
                        ConfigManager.loadMenus();
                        context.getSource().sendSuccess(() -> Component.literal("§aMenus reloaded!"), false);
                        return 1;
                    })
                )
            );
        });
    }

    private static int openMenu(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        CommandSourceStack source = context.getSource();

        if (!PermissionUtil.canOpenMenu(source, name)) {
            source.sendFailure(Component.literal("§cYou do not have permission to open this menu."));
            return 0;
        }

        try {
            MenuManager.openMenu(source.getPlayerOrException(), name);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
}
