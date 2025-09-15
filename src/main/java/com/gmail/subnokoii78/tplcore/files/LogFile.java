package com.gmail.subnokoii78.tplcore.files;

import com.gmail.subnokoii78.tplcore.TPLCore;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LogFile {
    public static final String LATEST_LOG_FILE_PATH = "logs/latest.log";

    private final Path path;

    public LogFile(@NotNull Path path) {
        this.path = path;
        if (!path.toFile().isFile()) {
            throw new IllegalArgumentException("そのパスはファイルパスとして無効です");
        }
    }

    public LogFile(@NotNull String path) {
        this(Path.of(path));
    }

    public LogFile(@NotNull File file) {
        this(file.toPath());
    }

    @NotNull
    public Path getPath() {
        return this.path;
    }

    protected @NotNull List<String> readPageLines(int pageNumber, @Nullable LogHistoryType type) throws IllegalStateException, IllegalArgumentException {
        if (pageNumber < 1) throw new IllegalArgumentException("Invalid argument: pageNumber (<=0)");

        if (this.exists()) {
            final List<String> logs = new ArrayList<>();

            try (
                final ReversedLinesFileReader reader = ReversedLinesFileReader.builder()
                    .setPath(path)
                    .setCharset(StandardCharsets.UTF_8)
                    .get()
            ) {
                int begin = (pageNumber - 1) * LogPage.ONE_PAGE_LINES_COUNT;
                int i = 0;
                String line;
                while (i < begin + LogPage.ONE_PAGE_LINES_COUNT) {
                    line = reader.readLine();
                    if (line == null) break;

                    if (type != null) {
                        if (!type.matches(line)) {
                            continue;
                        }
                    }

                    if (i < begin) {
                        i++;
                        continue;
                    }

                    logs.add(line);
                    i++;
                }
            }
            catch (IOException e) {
                throw new IllegalStateException("ファイルの読み取りに失敗しました", e);
            }

            return logs;
        }
        else {
            throw new IllegalStateException("ファイルが存在しません");
        }
    }

    public boolean exists() {
        return this.path.toFile().exists();
    }

    public void create() throws IllegalStateException {
        if (this.exists()) {
            throw new IllegalStateException("既にファイルは存在します");
        } else {
            try {
                Files.createFile(this.path);
            } catch (IOException e) {
                throw new IllegalStateException("ファイルの作成に失敗しました", e);
            }
        }
    }

    public void delete() throws IllegalStateException {
        if (this.exists()) {
            try {
                Files.delete(path);
            }
            catch (IOException e) {
                throw new IllegalStateException("ファイルの削除に失敗しました", e);
            }
        }
        else {
            throw new IllegalStateException("ファイルが存在しません");
        }
    }

    public long getSize() throws IllegalStateException {
        if (this.exists()) {
            try {
                return Files.size(path);
            }
            catch (IOException e) {
                throw new IllegalStateException("ファイルサイズの取得に失敗しました", e);
            }
        }
        else {
            throw new IllegalStateException("ファイルが存在しません");
        }
    }

    public @NotNull LogPage readPage(int pageNumber, @Nullable LogHistoryType type) throws IllegalStateException, IllegalArgumentException {
        return new LogPage(readPageLines(pageNumber, type), type);
    }

    public void write(@NotNull String message, @NotNull PluginMessageType type) throws IllegalStateException {
        TPLCore.getPlugin().getComponentLogger().info(type.toDecoratedMessage(message));
    }
}
