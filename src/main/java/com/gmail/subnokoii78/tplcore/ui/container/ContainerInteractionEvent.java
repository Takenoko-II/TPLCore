package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.TPLEvent;
import com.gmail.subnokoii78.tplcore.events.TPLEventType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ContainerInteractionEvent extends TPLEvent {
    @NotNull ContainerInteraction getInteraction();

    @NotNull Player getPlayer();

    @Override
    @NotNull TPLEventType<? extends TPLEvent> getType();

    TPLEventType<InteractionCloseEvent> INTERACTION_CLOSE = new TPLEventType<>(InteractionCloseEvent.class);

    TPLEventType<ItemButtonClickEvent> ITEM_BUTTON_CLICK = new TPLEventType<>(ItemButtonClickEvent.class);
}
