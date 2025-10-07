package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.execute.Advancements;
import com.gmail.subnokoii78.tplcore.execute.EntitySelector;
import com.gmail.subnokoii78.tplcore.execute.Scores;
import com.gmail.subnokoii78.tplcore.execute.SelectorArgument;

public class Main {
    public static void main(String[] args) {
        EntitySelector.A.arg(SelectorArgument.ADVANCEMENTS, Advancements.chain().$("a", false).$("b", true));
    }
}
