package me.yuzu.hono.module.player;

import me.yuzu.hono.module.Category;
import me.yuzu.hono.module.Module;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BedBreaker extends Module {

    private static final double RANGE = 6.0D; // 最大探索距離
    private static final double STEP = 0.25D; // レイ沿いの刻み（精度）

    public BedBreaker() {
        super("BedBreaker", "Break beds even through blocks in sight", Category.PLAYER, 0);
    }

    @Override
    public void onUpdate() {
        if (!this.isToggled() || mc.player == null || mc.level == null) return;

        BlockPos bedPos = findBedAlongLook();
        if (bedPos != null) {
            breakBedAt(bedPos);
        }
    }

    private BlockPos findBedAlongLook() {
        Vec3 eye = mc.player.getEyePosition(1.0f);
        Vec3 look = mc.player.getLookAngle();
        double max = RANGE;

        for (double d = 0.0; d <= max; d += STEP) {
            Vec3 p = eye.add(look.scale(d));

            // --- ここを環境に合わせてどちらかに置き換えてください ---
            // Option A (Mojang mappings / modern):
            BlockPos pos = BlockPos.containing(p);
            // Option B (fallback):
            // BlockPos pos = new BlockPos(p.x, p.y, p.z);
            // ---------------------------------------------------------

            BlockState state = mc.level.getBlockState(pos);
            if (state.getBlock() instanceof BedBlock) {
                return pos.immutable();
            }
        }

        return null;
    }
    /**
     * ベッドを破壊する処理。シングルなら level.destroyBlock、マルチなら
     * 正規の START/STOP_DESTROY_BLOCK パケットを送る。
     */
    private void breakBedAt(BlockPos pos) {
        // シングルプレイ（内蔵サーバ）ならサーバ側で処理してくれる -> 簡単に破壊
        if (mc.level.isClientSide() && mc.level.getServer() != null) {
            mc.level.destroyBlock(pos, true);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
            return;
        }

        // マルチ（またはクライアント側）: 正規パケットで破壊を試みる
        if (mc.getConnection() != null) {
            // 開始
            mc.getConnection().send(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                    pos,
                    Direction.UP // 方向は大まかで OK
            ));

            // ちょっと待ってから STOP を送る（短時間でも送る）
            // 単純実装: すぐ STOP を送る（サーバ実装次第でブロックは壊れることがある）
            mc.getConnection().send(new ServerboundPlayerActionPacket(
                    ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                    pos,
                    Direction.UP
            ));

            // 見た目の振り（アニメーション）
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        } else {
            // もし接続や権限が無ければ、クライアント側で視覚的に壊す（シングルでないと実際は壊れない）
            mc.level.destroyBlock(pos, true);
            mc.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }

    @Override
    public void setOptions() {
        // オプション（範囲やマルチ処理の切り替えなど）をここで用意しても良い
    }
}
