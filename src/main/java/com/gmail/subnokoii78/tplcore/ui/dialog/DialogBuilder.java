package com.gmail.subnokoii78.tplcore.ui.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;

public class DialogBuilder {
    public static void main(String[] args) {
        Dialog dialog = Dialog.create(builder -> builder.empty()
            .base(
                DialogBase.builder(Component.text("Title"))
                    .build()
            )
            .type(DialogType.notice())
        );
    }
}
