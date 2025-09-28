package com.gmail.subnokoii78.tplcore.parse;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NullMarked
public abstract class AbstractParser<T> {
    private final String text;

    private int cursor = 0;

    protected AbstractParser(String text) {
        this.text = text;
    }

    protected boolean isOver() {
        return cursor >= text.length();
    }

    private char peekChar() {
        if (isOver()) {
            throw exception("peek() の実行に失敗しました: isOver");
        }

        return text.charAt(cursor);
    }

    private void nextChar() {
        if (isOver()) {
            throw exception("next() の実行に失敗しました: isOver");
        }

        cursor++;
    }

    protected char peek(boolean ignore) {
        ignore();
        return peekChar();
    }

    protected void next(boolean ignore) {
        ignore();
        nextChar();
    }

    protected abstract Set<Character> getWhitespace();

    protected abstract Set<Character> getQuotes();

    protected abstract String getTrue();

    protected abstract String getFalse();

    protected void ignore() {
        if (isOver()) return;

        char current = peekChar();
        while (getWhitespace().contains(current)) {
            if (isOver()) {
                break;
            }
            nextChar();
            current = peekChar();
        }
    }

    protected @Nullable Character test(boolean ignore, char... candidates) {
        ignore();
        if (isOver()) return null;
        for (char c : candidates) {
            if (peekChar() == c) {
                return c;
            }
        }

        return null;
    }

    protected @Nullable Character next(boolean ignore, char... candidates) {
        ignore();
        if (isOver()) return null;
        for (char c : candidates) {
            if (peekChar() == c) {
                nextChar();
                return c;
            }
        }

        return null;
    }

    protected char expect(boolean ignore, char... candidates) {
        final Character c = next(ignore, candidates);
        if (c == null) {
            throw exception("expect() の実行に失敗しました: " + Arrays.toString(candidates));
        }

        return c;
    }

    protected @Nullable String test(boolean ignore, String... candidates) {
        ignore();
        if (isOver()) return null;
        for (final String string : Arrays.stream(candidates).sorted((a, b) -> b.length() - a.length()).toList()) {
            if (text.substring(cursor).startsWith(string)) {
                return string;
            }
        }

        return null;
    }

    protected @Nullable String next(boolean ignore, String... candidates) {
        ignore();
        if (isOver()) return null;
        for (final String string : Arrays.stream(candidates).sorted((a, b) -> b.length() - a.length()).toList()) {
            if (text.substring(cursor).startsWith(string)) {
                cursor += string.length();
                return string;
            }
        }

        return null;
    }

    protected String expect(boolean ignore, String... candidates) {
        final  String s = next(ignore, candidates);

        if (s == null) {
            throw exception("expect() の実行に失敗しました: " + Arrays.toString(candidates));
        }

        return s;
    }

    protected double number(boolean asInt) {
        final Set<Character> SIGNS = Set.of('+', '-');
        final Set<Character> INTEGERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        final char DECIMAL_POINT = '.';

        final StringBuilder sb = new StringBuilder();

        boolean intAppeared = false;
        boolean pointAppeared = false;

        if (isOver()) {
            throw exception("number() の実行に失敗しました: isOver");
        }

        final char initial = peek(true);
        next(true);

        if (SIGNS.contains(initial)) {
            sb.append(initial);
        }
        else if (INTEGERS.contains(initial)) {
            sb.append(initial);
            intAppeared = true;
        }

        while (!isOver()) {
            final char current = peek(false);

            if (INTEGERS.contains(current)) {
                sb.append(current);
                intAppeared = true;
            }
            else if (current == DECIMAL_POINT && intAppeared && !pointAppeared) {
                sb.append(current);
                pointAppeared = true;
            }
            else {
                break;
            }

            next(false);
        }

        if (pointAppeared && asInt) {
            throw exception("number() の実行に失敗しました: 非整数を検出しました: " + sb);
        }

        try {
            return Double.parseDouble(sb.toString());
        }
        catch (NumberFormatException e) {
            throw exception("小数の解析に失敗しました: '" + sb + "'", e);
        }
    }

    protected String string(boolean asQuoted, Character... unquotedStoppers) {
        final StringBuilder sb = new StringBuilder();
        char current = peek(true);
        next(true);

        if (getQuotes().contains(current)) {
            final char ESCAPE = '\\';

            final char quote = current;
            char previous = current;
            current = peek(false);
            next(false);

            while (previous == ESCAPE || current != quote) {
                if (previous == ESCAPE && current == quote) {
                    sb.delete(sb.length() - 1, sb.length());
                }

                sb.append(current);

                previous = current;
                current = peek(false);
                next(false);
            }
        }
        else if (!asQuoted) {
            final Set<Character> SYMBOLS = Set.of(
                '.', ',', ':', ';', '\\', '@',
                '(', ')', '{', '}', '[', ']',
                '!', '?', '\'', '"', '#', '$',
                '=', '+', '-', '*', '/', '%',
                '&', '|', '~', '^', '<', '>'
            );

            while (!getWhitespace().contains(current) && !Arrays.stream(unquotedStoppers).collect(Collectors.toSet()).contains(current)) {
                if (SYMBOLS.contains(current)) {
                    throw exception("クオーテーションで囲まれていない文字列において利用できない文字( "+ current +" )を検出しました");
                }

                sb.append(current);
                if (isOver()) {
                    return sb.toString();
                }
                current = peek(false);
                next(false);
            }

            // どうしようこいつ
            cursor--;
        }
        else {
            throw exception("string() の実行に失敗しました: asQuoted=true に反しています: " + sb);
        }

        return sb.toString();
    }

    protected boolean bool() {
        if (next(true, getTrue()) != null) return true;
        else if (next(true, getFalse()) != null) return false;
        else throw exception("真偽値の解析に失敗しました");
    }

    protected ParseException exception(String message) {
        return new ParseException(message, this);
    }

    protected ParseException exception(String message, Throwable cause) {
        return new ParseException(message, this, cause);
    }

    protected void finish() {
        ignore();
        final String t = text.substring(cursor);

        if (!t.isEmpty()) {
            throw exception("解析終了後に無効な文字列を検出しました: " + t);
        }
    }

    protected abstract T parse();

    public static final class ParseException extends RuntimeException {
        private <T> ParseException(String message, AbstractParser<T> parser, Throwable cause) {
            super(
                String.format(
                    message + "; pos: %s >> %s << %s",
                    parser.text.substring(Math.max(0, parser.cursor - 8), Math.max(0, parser.cursor)),
                    parser.cursor >= parser.text.length() ? "" : parser.text.charAt(parser.cursor),
                    parser.text.substring(Math.min(parser.cursor + 1, parser.text.length()), Math.min(parser.cursor + 8, parser.text.length()))
                ),
                cause
            );
        }

        private <T> ParseException(String message, AbstractParser<T> parser) {
            super(
                String.format(
                    message + "; pos: %s >> %s << %s",
                    parser.text.substring(Math.max(0, parser.cursor - 8), Math.max(0, parser.cursor)),
                    parser.cursor >= parser.text.length() ? "" : parser.text.charAt(parser.cursor),
                    parser.text.substring(Math.min(parser.cursor + 1, parser.text.length()), Math.min(parser.cursor + 8, parser.text.length()))
                )
            );
        }
    }
}
