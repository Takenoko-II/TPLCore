package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.TPLEventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InteractionCloseEvent implements ContainerInteractionEvent {
    private final ContainerInteraction interaction;

    private final Player player;

    protected InteractionCloseEvent(@NotNull ContainerInteraction interaction, @NotNull Player player) {
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
    public @NotNull TPLEventType<InteractionCloseEvent> getType() {
        return INTERACTION_CLOSE;
    }
}
