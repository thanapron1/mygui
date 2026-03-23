package mysavev.mygui.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mysavev.mygui.FabricMenus;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("fabricmenus/menus").toFile();
    private static final Map<String, MenuModel> loadedMenus = new HashMap<>();

    public static void init() {
        if (!CONFIG_DIR.exists()) {
            CONFIG_DIR.mkdirs();
            createExampleMenu();
        }
        loadMenus();
    }

    public static void loadMenus() {
        loadedMenus.clear();
        if (!CONFIG_DIR.exists()) return;

        File[] files = CONFIG_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                MenuModel menu = GSON.fromJson(reader, MenuModel.class);
                String name = file.getName().replace(".json", "");
                loadedMenus.put(name, menu);
                FabricMenus.LOGGER.info("Loaded menu: " + name);
            } catch (Exception e) {
                FabricMenus.LOGGER.error("Failed to load menu: " + file.getName(), e);
            }
        }
    }

    public static void saveMenu(String name, MenuModel menu) {
        File file = new File(CONFIG_DIR, name + ".json");
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(menu, writer);
            loadedMenus.put(name, menu);
        } catch (IOException e) {
            FabricMenus.LOGGER.error("Failed to save menu: " + name, e);
        }
    }

    public static MenuModel getMenu(String name) {
        return loadedMenus.get(name);
    }
    
    public static Set<String> getMenuNames() {
        return loadedMenus.keySet();
    }

    private static void createExampleMenu() {
        MenuModel menu = new MenuModel("Example Menu", 3);
        Map<String, ButtonModel> buttons = new HashMap<>();

        List<String> lore = new ArrayList<>();
        lore.add("And this is a line of lore");
        lore.add("Click me to say hello!");
        
        List<String> actions = new ArrayList<>();
        actions.add("[player] say Hello World!");
        actions.add("[message] You clicked the diamond!");
        actions.add("[close]");

        ButtonModel button = new ButtonModel(
            "minecraft:diamond",
            "&bThe Diamond Button",
            lore,
            List.of(13), // Center slot
            actions
        );
        
        buttons.put("diamond_btn", button);
        menu.setButtons(buttons);
        
        saveMenu("example", menu);
    }
}
