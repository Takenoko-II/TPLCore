package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.generic.MultiEntriesBuilder;
import com.gmail.subnokoii78.tplcore.scoreboard.ScoreObjective;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;

@NullMarked
public final class Scores extends MultiEntriesBuilder<String, NumberRange.ScoreRange, Scores> {
    private Scores() {
        super(true);
    }

    public Scores $(String objective, String range) {
        return $(objective, NumberRange.score(range));
    }

    public static Scores scores() {
        return new Scores();
    }
}
