package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class DatapackMessageReceiveEvent implements Event {
    private final Location location;

    private final Set<Entity> targets;

    private final JSONObject message;

    protected DatapackMessageReceiveEvent(@NotNull Location location, @NotNull Set<Entity> targets, @NotNull JSONObject message) {
        this.location = location;
        this.targets = targets;
        this.message = message;
    }

    @Override
    public @NotNull EventType<? extends Event> getType() {
        return EventTypes.DATAPACK_MESSAGE_RECEIVE;
    }

    public @NotNull Location getLocation() {
        return location;
    }

    public @NotNull Set<Entity> getTargets() {
        return targets;
    }

    public @NotNull JSONObject getMessage() {
        return message;
    }
}
