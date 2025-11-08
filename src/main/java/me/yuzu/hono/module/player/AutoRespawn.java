package me.yuzu.hono.module.player;
import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;

public class AutoRespawn extends Module{

	public AutoRespawn() {
		super("AutoRespawn", "Automatically Respawns Player Upon Death",Category.PLAYER,0);

	}
	
	@Override
	public void onUpdate() {
		if(this.isToggled()&&mc.player!=null) {
			if(mc.player.isDeadOrDying()) {
				mc.player.respawn();
			}
		}
	}

	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
		
	}

}
