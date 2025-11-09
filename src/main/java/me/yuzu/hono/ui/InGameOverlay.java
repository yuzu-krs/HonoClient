package me.yuzu.hono.ui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import me.yuzu.hono.Hono;
import me.yuzu.hono.util.RainbowUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class InGameOverlay {

	private final Minecraft mc=Minecraft.getInstance();
	
	public InGameOverlay() {}
	
	public void render(GuiGraphics guiGraphics) {
		if(mc.player!=null){
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			
			
			
			String clientName = "Hono | ";
			guiGraphics.drawString(mc.font, clientName, 10, 5, 0xff4500 );
			int clientNameWidth = mc.font.width(clientName);
			
			int fps=mc.getFps();
			String fpsText="FPS: " + fps;
			guiGraphics.drawString(mc.font, fpsText, 10+clientNameWidth, 5, 0xff4500 );
			
			
			int fpsTextWidth=mc.font.width(fpsText);
					
			int totalWidth=clientNameWidth+fpsTextWidth +5;
			int totalHeight=mc.font.lineHeight+2;
			
			
			int x1=10;
			int y1=5;
			int x2=x1+totalWidth;
			int y2=y1+totalHeight;
			guiGraphics.fill(x1-5, y1-3, x2, y2,0x99000000);
			
			
			renderEnabledMods(guiGraphics);
			
			RenderSystem.disableBlend();
			
		}
	}
	
	private void renderEnabledMods(GuiGraphics guiGraphics) {
		List<me.yuzu.hono.module.Module> enabledMods=Hono.instance.modManager.getEnabledModules();
		
		int screenWidth=mc.getWindow().getGuiScaledWidth();
		int yOffset=5;
		
		for(int i=0;i<enabledMods.size();i++) {
			me.yuzu.hono.module.Module mod=enabledMods.get(i);
			String modName=mod.getName();
			
			if(mod.getName().equalsIgnoreCase("ClickGui")) {
				continue;
			}
			
			
			int modNameWidth=mc.font.width(modName);
			
			int rainbowColor=RainbowUtil.getRainbowColor(i*0.1f);
			
			guiGraphics.drawString(mc.font, "- " + modName,screenWidth-80,yOffset,rainbowColor);

			yOffset += mc.font.lineHeight+2;
			
		}
	}
}









