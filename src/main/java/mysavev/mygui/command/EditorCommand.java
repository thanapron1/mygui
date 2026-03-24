package mysavev.mygui.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.editor.EditorSession;
import mysavev.mygui.editor.MainEditorScreen;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class EditorCommand {
    public static void register() {
         CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("menu")
                .then(Commands.literal("edit")
                    .requires(PermissionUtil::canUseEditor)
                    .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getMenuNames(), builder))
                        .executes(context -> {
                            String name = StringArgumentType.getString(context, "name");
                            ServerPlayer player = context.getSource().getPlayerOrException();
                            EditorSession.start(player, name);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("editor")
                    .requires(PermissionUtil::canUseEditor)
                    .executes(context -> {
                         ServerPlayer player = context.getSource().getPlayerOrException();
                         new MainEditorScreen(player).open();
                         return 1;
                    })
                )
            );
        });
    }
}
