
package me.yuzu.hono.module;

import java.util.ArrayList;
import java.util.List;

import me.yuzu.hono.ui.clickgui.options.CheckBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class Module {

    public String name;
    public String description;
    public Category category;
    public boolean toggled;
    public int keyCode;
    public Minecraft mc;
    
    private List<CheckBox> options=new ArrayList<>();

    public Module(String name, String description, Category category, int keyCode) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.toggled = false;
        this.keyCode = keyCode;
        this.mc = Minecraft.getInstance();
    }

    public int getKeyCode() {
        return keyCode;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onUpdate() {}
    
    public abstract void setOptions();

    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onRender() {
        if (this.isToggled()) {
            renderLogic();
        }
    }

    protected void renderLogic() {}
    
    
    public String getDescription() {
    	return description;
    }

	
	public void renderOptions(GuiGraphics graphics, int x, int y) {
	    int yOffset = 0;
	    for (CheckBox option : options) {
	        option.setPosition(x + 10, y + yOffset); // x, y にオフセットを加えて配置
	        option.render(graphics);                 // メソッド名を小文字に統一
	        yOffset += 20;                           // 次のオプションの位置を下にずらす
	    }
	}
    
    public void addOption(CheckBox checkbox) {
        options.add(checkbox);
    }
    
    public List<CheckBox> getOptions() {
        return options;
    }
    
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CheckBox option : options) {
            if (option.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }
    

	
}
