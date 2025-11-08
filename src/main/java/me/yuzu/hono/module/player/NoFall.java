package me.yuzu.hono.module.player;


import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;


public class NoFall extends Module {

    private boolean wasFlying = true;

    public NoFall() {
        super("NoFall", "Negates fall damage", Category.MOVEMENT, 0);
    }

    @Override
    public void onUpdate() {
        if (this.isToggled() && mc.player != null) {
            if (mc.player.fallDistance > 0.0f) {
                // 落下距離リセット
                mc.player.fallDistance = 0.0f;
                // サーバーへ「地上にいる」と通知して落下ダメージを無効化
                mc.player.connection.send(new ServerboundMovePlayerPacket.Pos(mc.player.getX(),mc.player.getY(),mc.player.getZ(),true, false));
            }
        }
    }

  
	@Override
	public void setOptions() {
		
	}
}