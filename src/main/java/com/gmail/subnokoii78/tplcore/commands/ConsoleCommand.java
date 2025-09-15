package com.gmail.subnokoii78.tplcore.commands;

import com.gmail.subnokoii78.tplcore.files.LogFile;
import com.gmail.subnokoii78.tplcore.files.LogHistoryType;
import com.gmail.subnokoii78.tplcore.files.LogPage;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import com.gmail.subnokoii78.tplcore.commands.arguments.LogTypeHistoryArgument;

public final class ConsoleCommand {
    public void $() {
        Commands.literal("console")
            .then(
                Commands.literal("query")
                    .then(
                        Commands.argument("log_type", LogTypeHistoryArgument.logType())
                            .then(
                                Commands.argument("page_number", IntegerArgumentType.integer())
                                    .executes(stack -> {
                                        final LogHistoryType logHistoryType = stack.getArgument("log_type", LogHistoryType.class);
                                        final int pageNumber = stack.getArgument("page_number", Integer.class);

                                        final LogFile logFile = new LogFile(LogFile.LATEST_LOG_FILE_PATH);

                                        if (logFile.exists()) {
                                            final LogPage logPage = logFile.readPage(pageNumber, logHistoryType);
                                            // TODO: log message parser
                                        }

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
            )
            /*.then(
                Commands.literal("put")
                    .then(
                        Commands.argument("plugin_message_type", )
                    )
            )*/;
    }
}
