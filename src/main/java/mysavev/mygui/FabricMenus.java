package mysavev.mygui;

import mysavev.mygui.command.EditorCommand;
import mysavev.mygui.command.MenuCommand;
import mysavev.mygui.config.ConfigManager;
import mysavev.mygui.editor.InputHandler;
import mysavev.mygui.economy.EconomyServices;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricMenus implements ModInitializer {
	public static final String MOD_ID = "fabricmenus";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("FabricMenus is initializing...");
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			LOGGER.info("Economy: initializing provider on SERVER_STARTING...");
			EconomyServices.init();
		});
		ConfigManager.init();
		
		MenuCommand.register();
		EditorCommand.register();
        
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (InputHandler.handleChat(sender, message.signedContent())) {
                return false; // Cancel message if handled by editor
            }
            return true;
        });
	}
}