package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;

public class Main {
    public static void main(String[] args) {
        EntitySelector.A
            .arg(SelectorArgument.ADVANCEMENTS, Advancements.advancements().$("a", false).$("b", true))
            .arg(SelectorArgument.SCORES, Scores.scores().$("foo", "1..2").$("bar", "..0"));

        VectorParser.axisAligned("~~1~").write(new Vector3Builder());
    }
}
