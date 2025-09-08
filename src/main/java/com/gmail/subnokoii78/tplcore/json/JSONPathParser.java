package com.gmail.subnokoii78.tplcore.json;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class JSONPathParser {
    private static final Set<Character> WHITESPACE = Set.of(' ');

    private static final char DOT = '.';

    private static final Set<Character> QUOTES = Set.of('"', '\'');

    private static final char[] OBJECT_BRACES = {'{', '}'};

    private static final char[] ARRAY_BRACES = {'[', ']'};

    private static final char ESCAPE = '\\';

    private static final String EMPTY_STRING = "";

    private String text;

    private int location = 0;

    private JSONPathParser() {

    }

    private @NotNull JSONParseException newException(@NotNull String message) {
        return new JSONParseException(message, text, location);
    }

    private boolean isOver() {
        return location >= text.length();
    }

    private char peek(boolean ignorable) {
        if (isOver()) {
            throw newException("文字列の長さが期待より不足しています");
        }

        final char next = text.charAt(location + 1);

        if (ignorable) {
            return WHITESPACE.contains(next) ? peek(true) : next;
        }
        return next;
    }

    private void next() {
        if (isOver()) {
            throw newException("文字列の長さが期待より不足しています");
        }

        location++;
    }

    private void whitespace() {
        if (isOver()) return;

        final char current = text.charAt(location++);

        if (WHITESPACE.contains(current)) {
            whitespace();
        }
        else {
            location--;
        }
    }

    private boolean test(@NotNull String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        return str.startsWith(next);
    }

    private boolean test(char next) {
        return test(String.valueOf(next));
    }

    private boolean next(@NotNull String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        if (str.startsWith(next)) {
            location += next.length();
            whitespace();
            return true;
        }

        return false;
    }

    private boolean next(char next) {
        return next(String.valueOf(next));
    }

    private void expect(@NotNull String next) {
        if (!next(next)) {
            throw newException("期待された文字列は" + next + "でしたが、テストが偽を返しました");
        }
    }

    private void expect(char next) {
        expect(String.valueOf(next));
    }

    private @NotNull String string() {
        final StringBuilder sb = new StringBuilder();
        char current = peek(true);

        if (QUOTES.contains(current)) {
            next();

            final char quote = current;
            char previous = current;
            current = peek(false);
            next();

            while (previous == ESCAPE || current != quote) {
                if (previous == ESCAPE && current == quote) {
                    sb.delete(sb.length() - 1, sb.length());
                }

                sb.append(current);

                previous = current;
                current = peek(false);
                next();
            }

            return sb.toString();
        }
        else throw newException("文字列はクォーテーションで開始される必要があります");
    }

    private @NotNull String objectKey(boolean isRoot) {
        if (!isRoot) expect(DOT);

        final StringBuilder sb = new StringBuilder();
        while (!isOver()) {
            final char c = peek(false);

            if (WHITESPACE.contains(c) || c == OBJECT_BRACES[0] || c == OBJECT_BRACES[1] || c == ARRAY_BRACES[0] || c == ARRAY_BRACES[1]) {
                throw newException("期待された文字は非記号文字です");
            }
            else if (QUOTES.contains(c)) {
                sb.append(c);
                sb.append(string());
                sb.append(c);
                continue;
            }
            else if (c == DOT) {
                break;
            }

            sb.append(c);
            next();
        }

        return sb.toString();
    }

    private @NotNull String arrayIndex() {
        expect(ARRAY_BRACES[0]);

        if (next(ARRAY_BRACES[1])) {
            return EMPTY_STRING;
        }

        final StringBuilder sb = new StringBuilder();
        int depth = 1;

        while (!isOver()) {
            final char c = peek(false);

            if (c == ARRAY_BRACES[0]) {
                depth++;
            }
            else if (c == ARRAY_BRACES[1]) {
                depth--;
            }

            if (depth == 0) {
                break;
            }

            sb.append(c);
            next();
        }

        expect(ARRAY_BRACES[1]);

        return sb.toString();
    }

    private @NotNull Object root() {

    }

    private void extraChars() {
        if (!isOver()) throw newException("解析終了後、末尾に無効な文字列(" + text.substring(location) + ")を検出しました");
    }

    private @NotNull Object parse() {
        if (text == null) {
            throw newException("textがnullです");
        }

        final Object value = root();
        extraChars();
        return value;
    }
}
