package com.gmail.subnokoii78.tplcore.database;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@NullMarked
public class SqliteDatabase {
    protected final File file;

    private final String url;

    @Nullable
    private Connection connection;

    protected SqliteDatabase(String path) {
        this.file = new File(path);
        this.url = "jdbc:sqlite:" + file.getAbsolutePath();
    }

    protected Connection getConnection() throws IllegalStateException {
        if (connection == null) {
            throw new IllegalStateException("データベースに接続されていません");
        }
        return connection;
    }

    protected void connect() {
        if (connection != null) {
            throw new IllegalStateException("データベースへの接続に失敗しました: 既に接続されています");
        }

        try {
            connection = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            throw new IllegalStateException("データベースへの接続に失敗しました: ", e);
        }
    }

    protected void disconnect() {
        if (connection == null) {
            throw new IllegalStateException("切断に失敗しました: 既に接続されていません");
        }

        try {
            connection.close();
        }
        catch (SQLException e) {
            throw new IllegalStateException("切断に失敗しました: ", e);
        }
    }
}
