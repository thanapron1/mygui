package mysavev.mygui.config;

import java.util.HashMap;
import java.util.Map;

public class MenuModel {
    private String title;
    private int rows;
    private Map<String, ButtonModel> buttons = new HashMap<>();

    public MenuModel() {}

    public MenuModel(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public Map<String, ButtonModel> getButtons() {
        return buttons;
    }

    public void setButtons(Map<String, ButtonModel> buttons) {
        this.buttons = buttons;
    }
}

