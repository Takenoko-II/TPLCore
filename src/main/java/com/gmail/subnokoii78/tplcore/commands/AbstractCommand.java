package com.gmail.subnokoii78.tplcore.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand {
    protected AbstractCommand() {}

    protected abstract @NotNull LiteralCommandNode<CommandSourceStack> getCommandNode();
}
