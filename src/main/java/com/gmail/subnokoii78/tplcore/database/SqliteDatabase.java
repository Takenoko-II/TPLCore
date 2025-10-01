package com.gmail.subnokoii78.tplcore.database;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@NullMarked
public class SqliteDatabase {
    private final File file;

    private final String url;

    @Nullable
    private Connection connection;

    private SqliteDatabase(String path) {
        this.file = new File(path);
        this.url = "jdbc:sqlite:" + file.getAbsolutePath();
    }

    private void connect() {
        if (connection != null) {
            throw new IllegalStateException("既に接続されています");
        }

        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースへの接続に失敗しました: ", e);
        }
    }

    private void disconnect() {
        if (connection == null) {
            throw new IllegalStateException("切断に失敗しました: 既に接続されていません");
        }

        try {
            connection.close();
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースの接続切断に失敗しました: ", e);
        }
    }
}
