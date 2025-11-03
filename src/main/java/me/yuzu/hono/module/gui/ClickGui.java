package me.yuzu.hono.module.gui;

import org.lwjgl.glfw.GLFW;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import me.yuzu.hono.ui.clickgui.ClickGuiMain;

public class ClickGui extends Module {

    private boolean isGuiOpen = false;

    public ClickGui() {
        super("ClickGui", "Displays ClickGui", Category.GUI, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    @Override
    public void onRender() {
        if (this.isToggled() && !isGuiOpen) {
            openGui();
        }
    }

    private void openGui() {
        if (!isGuiOpen && mc.screen == null) {
            mc.setScreen(new ClickGuiMain(this));
            isGuiOpen = true;
            super.onEnable();
        }
    }

    public void closeGui() {
        if (isGuiOpen) {
            mc.setScreen(null);
            isGuiOpen = false;
            this.toggle(); // トグル状態も切り替え
            super.onDisable();
        }
    }

	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
