package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;

public class RotateCommand {
    public static void register(CommandDispatcher<CommandSourceStack> p_361568_) {
        p_361568_.register(
            Commands.literal("rotate")
                .requires(p_362688_ -> p_362688_.hasPermission(2))
                .then(
                    Commands.argument("target", EntityArgument.entity())
                        .then(
                            Commands.argument("rotation", RotationArgument.rotation())
                                .executes(
                                    p_364915_ -> rotate(
                                            p_364915_.getSource(),
                                            EntityArgument.getEntity(p_364915_, "target"),
                                            RotationArgument.getRotation(p_364915_, "rotation")
                                        )
                                )
                        )
                        .then(
                            Commands.literal("facing")
                                .then(
                                    Commands.literal("entity")
                                        .then(
                                            Commands.argument("facingEntity", EntityArgument.entity())
                                                .executes(
                                                    p_366948_ -> rotate(
                                                            p_366948_.getSource(),
                                                            EntityArgument.getEntity(p_366948_, "target"),
                                                            new LookAt.LookAtEntity(
                                                                EntityArgument.getEntity(p_366948_, "facingEntity"), EntityAnchorArgument.Anchor.FEET
                                                            )
                                                        )
                                                )
                                                .then(
                                                    Commands.argument("facingAnchor", EntityAnchorArgument.anchor())
                                                        .executes(
                                                            p_370107_ -> rotate(
                                                                    p_370107_.getSource(),
                                                                    EntityArgument.getEntity(p_370107_, "target"),
                                                                    new LookAt.LookAtEntity(
                                                                        EntityArgument.getEntity(p_370107_, "facingEntity"),
                                                                        EntityAnchorArgument.getAnchor(p_370107_, "facingAnchor")
                                                                    )
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.argument("facingLocation", Vec3Argument.vec3())
                                        .executes(
                                            p_365271_ -> rotate(
                                                    p_365271_.getSource(),
                                                    EntityArgument.getEntity(p_365271_, "target"),
                                                    new LookAt.LookAtPosition(Vec3Argument.getVec3(p_365271_, "facingLocation"))
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int rotate(CommandSourceStack p_366764_, Entity p_367594_, Coordinates p_363288_) {
        Vec2 vec2 = p_363288_.getRotation(p_366764_);
        p_367594_.forceSetRotation(vec2.y, vec2.x);
        p_366764_.sendSuccess(() -> Component.translatable("commands.rotate.success", p_367594_.getDisplayName()), true);
        return 1;
    }

    private static int rotate(CommandSourceStack p_361401_, Entity p_364656_, LookAt p_364516_) {
        p_364516_.perform(p_361401_, p_364656_);
        p_361401_.sendSuccess(() -> Component.translatable("commands.rotate.success", p_364656_.getDisplayName()), true);
        return 1;
    }
}