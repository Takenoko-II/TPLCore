package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.commands.arguments.ScriptLanguageArgument;
import com.gmail.subnokoii78.tplcore.eval.ScriptLanguage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NullMarked;

import java.util.Locale;

@NullMarked
public class ScriptCommand extends AbstractCommand {
    @Override
    protected String getDescription() {
        return "スクリプトを実行します(少々危険なのでスクリプトが信頼できない場合はこのコマンドを使用しないでください)";
    }

    @Override
    protected LiteralCommandNode<CommandSourceStack> getCommandNode() {
        return Commands.literal("script")
            .requires(stack -> {
                return isAllowed(stack.getSender());
            })
            .then(
                Commands.argument("script_language", ScriptLanguageArgument.scriptLanguage())
                    .then(
                        Commands.argument("script", StringArgumentType.greedyString())
                            .executes(ctx -> {
                                final ScriptLanguage type = ctx.getArgument("script_language", ScriptLanguage.class);
                                final String script = ctx.getArgument("script", String.class);
                                final int returnValue = type.interpret(ctx.getSource(), script);

                                if (returnValue > 0) {
                                    ctx.getSource().getSender().sendMessage(Component.text(
                                        String.format(
                                            "%s の実行に成功しました: %d",
                                            type.name().toLowerCase(Locale.ROOT),
                                            returnValue
                                        )
                                    ));
                                }
                                else {
                                    return failure(ctx.getSource(), new IllegalArgumentException(
                                        String.format("%s の実行に失敗しました", type.name().toLowerCase(Locale.ROOT))
                                    ));
                                }

                                return returnValue;
                            })
                    )
            )
            .build();
    }

    public static final ScriptCommand SCRIPT_COMMAND = new ScriptCommand();
}
