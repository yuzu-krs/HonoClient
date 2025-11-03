package me.yuzu.hono.ui.clickgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.gui.ClickGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClickGuiMain extends Screen {

	    private final ClickGui clickGui;
	    private final Minecraft mc = Minecraft.getInstance();
	    private List<GuiCategory> categories = new ArrayList<>();
	    private final int categoryWidth = 50;
	    private final int categoryHeight = 20;
	    private final int categorySpacing = 10;

	    // カテゴリの位置（x, y）を保持するマップ
	    public static Map<Category, int[]> categoryPositions = new HashMap<>();

	    // カテゴリの展開状態を保持するマップ
	    public static Map<Category, Boolean> expandedStates = new HashMap<>();
	    
	    public ClickGuiMain(ClickGui clickGui) {
	        super(Component.literal("ClickGui"));
	        this.clickGui = clickGui;

	        int screenWidth = mc.getWindow().getGuiScaledWidth();
	        int xOffset = 20;
	        int yOffset = 40;

	        for (Category category : Category.values()) {
	            if (category == Category.GUI) continue; // GUIカテゴリはスキップ

	            int[] position = categoryPositions.getOrDefault(category, new int[]{xOffset, yOffset});
	            int savedX = position[0];
	            int savedY = position[1];

	            boolean isExpanded = expandedStates.getOrDefault(category, false);

	            GuiCategory guiCategory = new GuiCategory(category, savedX, savedY);
	            guiCategory.setExpanded(isExpanded);

	            categories.add(guiCategory);
	            xOffset += categoryWidth + categorySpacing;
	        }
	    }

	    @Override
	    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
	        // 一度だけ背景をぼかす
	        graphics.fillGradient(0, 0, width, height,  0x00000000, 0xCC000000);

	        for (GuiCategory category : categories) {
	            category.render(graphics, mouseX, mouseY, delta);
	        }
	    }

	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int button) {
	        for (GuiCategory category : categories) {
	            if (category.mouseClicked(mouseX, mouseY, button)) {
	                return true;
	            }
	        }
	        return super.mouseClicked(mouseX, mouseY, button);
	    }
	    

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		    if (keyCode == GLFW.GLFW_KEY_ESCAPE ) {
		        clickGui.closeGui();
		        return true;
		    }
		    return super.keyPressed(keyCode, scanCode, modifiers);
		}
		
		@Override
		public boolean isPauseScreen() {
		    return false;
		}
		
		@Override
		public boolean mouseReleased(double mouseX, double mouseY, int button) {
		    for (GuiCategory category : categories) {
		        category.mouseReleased(mouseX, mouseY, button);
		    }
		    return super.mouseReleased(mouseX, mouseY, button);
		}
}
