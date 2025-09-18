package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.TPLEventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemButtonBuildEvent implements ContainerInteractionEvent {
    private final ContainerInteraction interaction;

    private final Player player;

    ItemButtonBuildEvent(@NotNull ContainerInteraction interaction, @NotNull Player player) {
        this.interaction = interaction;
        this.player = player;
    }

    @Override
    public @NotNull ContainerInteraction getInteraction() {
        return interaction;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull TPLEventType<ItemButtonBuildEvent> getType() {
        return ITEM_BUTTON_CREATE;
    }
}
