package com.gmail.subnokoii78.tplcore.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

import java.util.List;

@NullMarked
public class PrivilegeCommand extends AbstractCommand {
    @Override
    protected String getDescription() {
        return "プラグインによる追加コマンドの一部について、実行権限を操作します";
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("privilege")
            .requires(stack -> {
                return isDeveloper(stack.getSender()) || isServerOwner(stack.getSender());
            })
            .then(
                Commands.literal("escalate")
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .executes(this::escalate)
                    )
            )
            .then(
                Commands.literal("demote")
                    .then(
                        Commands.argument("players", ArgumentTypes.players())
                            .executes(this::demote)
                    )
            )
            .build();
    }

    private int escalate(CommandContext<CommandSourceStack> ctx) {
        final List<Player> players;
        try {
            players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        }
        catch (CommandSyntaxException e) {
            return failure(ctx.getSource(), e);
        }

        final int count = players.stream().map(AbstractCommand::allow).reduce(0, Integer::sum);

        if (ctx.getSource().getSender() instanceof Player player) {
            player.updateCommands();
        }

        if (count == 0) {
            return failure(ctx.getSource(), new IllegalStateException(
                "権限を与えられるプレイヤーが見つかりませんでした"
            ));
        }
        else {
            ctx.getSource().getSender().sendMessage(
                count + " 人のプレイヤーの権限を一時昇格させました"
            );
            return Command.SINGLE_SUCCESS;
        }
    }

    private int demote(CommandContext<CommandSourceStack> ctx) {
        final List<Player> players;
        try {
            players = ctx.getArgument("players", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        }
        catch (CommandSyntaxException e) {
            return failure(ctx.getSource(), e);
        }

        final int count = players.stream().map(AbstractCommand::disallow).reduce(0, Integer::sum);

        if (ctx.getSource().getSender() instanceof Player player) {
            player.updateCommands();
        }

        if (count == 0) {
            return failure(ctx.getSource(), new IllegalStateException(
                "権限を剥奪できるプレイヤーが見つかりませんでした"
            ));
        }
        else {
            ctx.getSource().getSender().sendMessage(
                count + " 人のプレイヤーの権限を戻しました"
            );
            return Command.SINGLE_SUCCESS;
        }
    }

    public static final PrivilegeCommand PRIVILEGE_COMMAND = new PrivilegeCommand();
}
