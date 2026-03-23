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
            dispatcher.register(Commands.literal("logo") // Alias or main command? Request said /menu open, so usually under /menu
                // But if I split them, they both need to register under "menu". 
                // Brigadier merges command trees.
            );
            
            // Registering /menu open
            dispatcher.register(Commands.literal("menu")
                .then(Commands.literal("open")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getMenuNames(), builder))
                        .executes(MenuCommand::openMenu)
                    )
                )
                .then(Commands.literal("reload")
                    .requires(source -> source.hasPermission(4))
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
        try {
            MenuManager.openMenu(context.getSource().getPlayerOrException(), name);
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
        }
        return 1;
    }
}

