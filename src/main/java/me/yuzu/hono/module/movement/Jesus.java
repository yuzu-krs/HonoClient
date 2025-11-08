package me.yuzu.hono.module.movement;


import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import net.minecraft.world.level.block.Blocks;


public class Jesus extends Module {

    public Jesus() {
        super("Jesus", "Allows player to walk on water", Category.MOVEMENT, 0);
    }

    @Override
    public void onUpdate() {
        if (this.isToggled() && mc.player != null) {
            // プレイヤーの足元のブロックを取得
            if (mc.level.getBlockState(mc.player.blockPosition().below()).getBlock() == Blocks.WATER) {

                // 落下している場合のみ処理
                if (mc.player.getDeltaMovement().y < 0) {

                    // 垂直速度を0にして沈まないようにする
                    mc.player.setDeltaMovement(
                        mc.player.getDeltaMovement().x,
                        0.0,
                        mc.player.getDeltaMovement().z
                    );

                    // プレイヤーを水面に固定
                    mc.player.setPos(
                        mc.player.getX(),
                        Math.floor(mc.player.getY()),
                        mc.player.getZ()
                    );

                    // サーバー側に地上扱いとして送信
                    mc.player.setOnGround(true);
                }
            }
        }
    }



	@Override
	public void setOptions() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
}
