package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementCommands {
    private static final DynamicCommandExceptionType ERROR_NO_ACTION_PERFORMED = new DynamicCommandExceptionType(p_308608_ -> (Component)p_308608_);
    private static final Dynamic2CommandExceptionType ERROR_CRITERION_NOT_FOUND = new Dynamic2CommandExceptionType(
        (p_341132_, p_341133_) -> Component.translatableEscape("commands.advancement.criterionNotFound", p_341132_, p_341133_)
    );

    public static void register(CommandDispatcher<CommandSourceStack> p_136311_) {
        p_136311_.register(
            Commands.literal("advancement")
                .requires(p_136318_ -> p_136318_.hasPermission(2))
                .then(
                    Commands.literal("grant")
                        .then(
                            Commands.argument("targets", EntityArgument.players())
                                .then(
                                    Commands.literal("only")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358563_ -> perform(
                                                            p_358563_.getSource(),
                                                            EntityArgument.getPlayers(p_358563_, "targets"),
                                                            AdvancementCommands.Action.GRANT,
                                                            getAdvancements(
                                                                p_358563_,
                                                                ResourceKeyArgument.getAdvancement(p_358563_, "advancement"),
                                                                AdvancementCommands.Mode.ONLY
                                                            )
                                                        )
                                                )
                                                .then(
                                                    Commands.argument("criterion", StringArgumentType.greedyString())
                                                        .suggests(
                                                            (p_358572_, p_358573_) -> SharedSuggestionProvider.suggest(
                                                                    ResourceKeyArgument.getAdvancement(p_358572_, "advancement").value().criteria().keySet(),
                                                                    p_358573_
                                                                )
                                                        )
                                                        .executes(
                                                            p_358574_ -> performCriterion(
                                                                    p_358574_.getSource(),
                                                                    EntityArgument.getPlayers(p_358574_, "targets"),
                                                                    AdvancementCommands.Action.GRANT,
                                                                    ResourceKeyArgument.getAdvancement(p_358574_, "advancement"),
                                                                    StringArgumentType.getString(p_358574_, "criterion")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("from")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358555_ -> perform(
                                                            p_358555_.getSource(),
                                                            EntityArgument.getPlayers(p_358555_, "targets"),
                                                            AdvancementCommands.Action.GRANT,
                                                            getAdvancements(
                                                                p_358555_,
                                                                ResourceKeyArgument.getAdvancement(p_358555_, "advancement"),
                                                                AdvancementCommands.Mode.FROM
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("until")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358564_ -> perform(
                                                            p_358564_.getSource(),
                                                            EntityArgument.getPlayers(p_358564_, "targets"),
                                                            AdvancementCommands.Action.GRANT,
                                                            getAdvancements(
                                                                p_358564_,
                                                                ResourceKeyArgument.getAdvancement(p_358564_, "advancement"),
                                                                AdvancementCommands.Mode.UNTIL
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("through")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358565_ -> perform(
                                                            p_358565_.getSource(),
                                                            EntityArgument.getPlayers(p_358565_, "targets"),
                                                            AdvancementCommands.Action.GRANT,
                                                            getAdvancements(
                                                                p_358565_,
                                                                ResourceKeyArgument.getAdvancement(p_358565_, "advancement"),
                                                                AdvancementCommands.Mode.THROUGH
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("everything")
                                        .executes(
                                            p_136353_ -> perform(
                                                    p_136353_.getSource(),
                                                    EntityArgument.getPlayers(p_136353_, "targets"),
                                                    AdvancementCommands.Action.GRANT,
                                                    p_136353_.getSource().getServer().getAdvancements().getAllAdvancements()
                                                )
                                        )
                                )
                        )
                )
                .then(
                    Commands.literal("revoke")
                        .then(
                            Commands.argument("targets", EntityArgument.players())
                                .then(
                                    Commands.literal("only")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358556_ -> perform(
                                                            p_358556_.getSource(),
                                                            EntityArgument.getPlayers(p_358556_, "targets"),
                                                            AdvancementCommands.Action.REVOKE,
                                                            getAdvancements(
                                                                p_358556_,
                                                                ResourceKeyArgument.getAdvancement(p_358556_, "advancement"),
                                                                AdvancementCommands.Mode.ONLY
                                                            )
                                                        )
                                                )
                                                .then(
                                                    Commands.argument("criterion", StringArgumentType.greedyString())
                                                        .suggests(
                                                            (p_358560_, p_358561_) -> SharedSuggestionProvider.suggest(
                                                                    ResourceKeyArgument.getAdvancement(p_358560_, "advancement").value().criteria().keySet(),
                                                                    p_358561_
                                                                )
                                                        )
                                                        .executes(
                                                            p_358562_ -> performCriterion(
                                                                    p_358562_.getSource(),
                                                                    EntityArgument.getPlayers(p_358562_, "targets"),
                                                                    AdvancementCommands.Action.REVOKE,
                                                                    ResourceKeyArgument.getAdvancement(p_358562_, "advancement"),
                                                                    StringArgumentType.getString(p_358562_, "criterion")
                                                                )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("from")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358569_ -> perform(
                                                            p_358569_.getSource(),
                                                            EntityArgument.getPlayers(p_358569_, "targets"),
                                                            AdvancementCommands.Action.REVOKE,
                                                            getAdvancements(
                                                                p_358569_,
                                                                ResourceKeyArgument.getAdvancement(p_358569_, "advancement"),
                                                                AdvancementCommands.Mode.FROM
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("until")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358571_ -> perform(
                                                            p_358571_.getSource(),
                                                            EntityArgument.getPlayers(p_358571_, "targets"),
                                                            AdvancementCommands.Action.REVOKE,
                                                            getAdvancements(
                                                                p_358571_,
                                                                ResourceKeyArgument.getAdvancement(p_358571_, "advancement"),
                                                                AdvancementCommands.Mode.UNTIL
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("through")
                                        .then(
                                            Commands.argument("advancement", ResourceKeyArgument.key(Registries.ADVANCEMENT))
                                                .executes(
                                                    p_358570_ -> perform(
                                                            p_358570_.getSource(),
                                                            EntityArgument.getPlayers(p_358570_, "targets"),
                                                            AdvancementCommands.Action.REVOKE,
                                                            getAdvancements(
                                                                p_358570_,
                                                                ResourceKeyArgument.getAdvancement(p_358570_, "advancement"),
                                                                AdvancementCommands.Mode.THROUGH
                                                            )
                                                        )
                                                )
                                        )
                                )
                                .then(
                                    Commands.literal("everything")
                                        .executes(
                                            p_136313_ -> perform(
                                                    p_136313_.getSource(),
                                                    EntityArgument.getPlayers(p_136313_, "targets"),
                                                    AdvancementCommands.Action.REVOKE,
                                                    p_136313_.getSource().getServer().getAdvancements().getAllAdvancements()
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int perform(
        CommandSourceStack p_136320_, Collection<ServerPlayer> p_136321_, AdvancementCommands.Action p_136322_, Collection<AdvancementHolder> p_136323_
    ) throws CommandSyntaxException {
        int i = 0;

        for (ServerPlayer serverplayer : p_136321_) {
            i += p_136322_.perform(serverplayer, p_136323_);
        }

        if (i == 0) {
            if (p_136323_.size() == 1) {
                if (p_136321_.size() == 1) {
                    throw ERROR_NO_ACTION_PERFORMED.create(
                        Component.translatable(
                            p_136322_.getKey() + ".one.to.one.failure",
                            Advancement.name(p_136323_.iterator().next()),
                            p_136321_.iterator().next().getDisplayName()
                        )
                    );
                } else {
                    throw ERROR_NO_ACTION_PERFORMED.create(
                        Component.translatable(
                            p_136322_.getKey() + ".one.to.many.failure", Advancement.name(p_136323_.iterator().next()), p_136321_.size()
                        )
                    );
                }
            } else if (p_136321_.size() == 1) {
                throw ERROR_NO_ACTION_PERFORMED.create(
                    Component.translatable(p_136322_.getKey() + ".many.to.one.failure", p_136323_.size(), p_136321_.iterator().next().getDisplayName())
                );
            } else {
                throw ERROR_NO_ACTION_PERFORMED.create(Component.translatable(p_136322_.getKey() + ".many.to.many.failure", p_136323_.size(), p_136321_.size()));
            }
        } else {
            if (p_136323_.size() == 1) {
                if (p_136321_.size() == 1) {
                    p_136320_.sendSuccess(
                        () -> Component.translatable(
                                p_136322_.getKey() + ".one.to.one.success",
                                Advancement.name(p_136323_.iterator().next()),
                                p_136321_.iterator().next().getDisplayName()
                            ),
                        true
                    );
                } else {
                    p_136320_.sendSuccess(
                        () -> Component.translatable(
                                p_136322_.getKey() + ".one.to.many.success", Advancement.name(p_136323_.iterator().next()), p_136321_.size()
                            ),
                        true
                    );
                }
            } else if (p_136321_.size() == 1) {
                p_136320_.sendSuccess(
                    () -> Component.translatable(p_136322_.getKey() + ".many.to.one.success", p_136323_.size(), p_136321_.iterator().next().getDisplayName()), true
                );
            } else {
                p_136320_.sendSuccess(() -> Component.translatable(p_136322_.getKey() + ".many.to.many.success", p_136323_.size(), p_136321_.size()), true);
            }

            return i;
        }
    }

    private static int performCriterion(
        CommandSourceStack p_136325_, Collection<ServerPlayer> p_136326_, AdvancementCommands.Action p_136327_, AdvancementHolder p_299259_, String p_136329_
    ) throws CommandSyntaxException {
        int i = 0;
        Advancement advancement = p_299259_.value();
        if (!advancement.criteria().containsKey(p_136329_)) {
            throw ERROR_CRITERION_NOT_FOUND.create(Advancement.name(p_299259_), p_136329_);
        } else {
            for (ServerPlayer serverplayer : p_136326_) {
                if (p_136327_.performCriterion(serverplayer, p_299259_, p_136329_)) {
                    i++;
                }
            }

            if (i == 0) {
                if (p_136326_.size() == 1) {
                    throw ERROR_NO_ACTION_PERFORMED.create(
                        Component.translatable(
                            p_136327_.getKey() + ".criterion.to.one.failure",
                            p_136329_,
                            Advancement.name(p_299259_),
                            p_136326_.iterator().next().getDisplayName()
                        )
                    );
                } else {
                    throw ERROR_NO_ACTION_PERFORMED.create(
                        Component.translatable(p_136327_.getKey() + ".criterion.to.many.failure", p_136329_, Advancement.name(p_299259_), p_136326_.size())
                    );
                }
            } else {
                if (p_136326_.size() == 1) {
                    p_136325_.sendSuccess(
                        () -> Component.translatable(
                                p_136327_.getKey() + ".criterion.to.one.success",
                                p_136329_,
                                Advancement.name(p_299259_),
                                p_136326_.iterator().next().getDisplayName()
                            ),
                        true
                    );
                } else {
                    p_136325_.sendSuccess(
                        () -> Component.translatable(
                                p_136327_.getKey() + ".criterion.to.many.success", p_136329_, Advancement.name(p_299259_), p_136326_.size()
                            ),
                        true
                    );
                }

                return i;
            }
        }
    }

    private static List<AdvancementHolder> getAdvancements(
        CommandContext<CommandSourceStack> p_298043_, AdvancementHolder p_300683_, AdvancementCommands.Mode p_136335_
    ) {
        AdvancementTree advancementtree = p_298043_.getSource().getServer().getAdvancements().tree();
        AdvancementNode advancementnode = advancementtree.get(p_300683_);
        if (advancementnode == null) {
            return List.of(p_300683_);
        } else {
            List<AdvancementHolder> list = new ArrayList<>();
            if (p_136335_.parents) {
                for (AdvancementNode advancementnode1 = advancementnode.parent(); advancementnode1 != null; advancementnode1 = advancementnode1.parent()) {
                    list.add(advancementnode1.holder());
                }
            }

            list.add(p_300683_);
            if (p_136335_.children) {
                addChildren(advancementnode, list);
            }

            return list;
        }
    }

    private static void addChildren(AdvancementNode p_300493_, List<AdvancementHolder> p_136332_) {
        for (AdvancementNode advancementnode : p_300493_.children()) {
            p_136332_.add(advancementnode.holder());
            addChildren(advancementnode, p_136332_);
        }
    }

    static enum Action {
        GRANT("grant") {
            @Override
            protected boolean perform(ServerPlayer p_136395_, AdvancementHolder p_299481_) {
                AdvancementProgress advancementprogress = p_136395_.getAdvancements().getOrStartProgress(p_299481_);
                if (advancementprogress.isDone()) {
                    return false;
                } else {
                    for (String s : advancementprogress.getRemainingCriteria()) {
                        p_136395_.getAdvancements().award(p_299481_, s);
                    }

                    return true;
                }
            }

            @Override
            protected boolean performCriterion(ServerPlayer p_136398_, AdvancementHolder p_300422_, String p_136400_) {
                return p_136398_.getAdvancements().award(p_300422_, p_136400_);
            }
        },
        REVOKE("revoke") {
            @Override
            protected boolean perform(ServerPlayer p_136406_, AdvancementHolder p_301329_) {
                AdvancementProgress advancementprogress = p_136406_.getAdvancements().getOrStartProgress(p_301329_);
                if (!advancementprogress.hasProgress()) {
                    return false;
                } else {
                    for (String s : advancementprogress.getCompletedCriteria()) {
                        p_136406_.getAdvancements().revoke(p_301329_, s);
                    }

                    return true;
                }
            }

            @Override
            protected boolean performCriterion(ServerPlayer p_136409_, AdvancementHolder p_299512_, String p_136411_) {
                return p_136409_.getAdvancements().revoke(p_299512_, p_136411_);
            }
        };

        private final String key;

        Action(final String p_136372_) {
            this.key = "commands.advancement." + p_136372_;
        }

        public int perform(ServerPlayer p_136380_, Iterable<AdvancementHolder> p_136381_) {
            int i = 0;

            for (AdvancementHolder advancementholder : p_136381_) {
                if (this.perform(p_136380_, advancementholder)) {
                    i++;
                }
            }

            return i;
        }

        protected abstract boolean perform(ServerPlayer p_136384_, AdvancementHolder p_298402_);

        protected abstract boolean performCriterion(ServerPlayer p_136382_, AdvancementHolder p_300251_, String p_298964_);

        protected String getKey() {
            return this.key;
        }
    }

    static enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        final boolean parents;
        final boolean children;

        private Mode(final boolean p_136424_, final boolean p_136425_) {
            this.parents = p_136424_;
            this.children = p_136425_;
        }
    }
}