package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonList;
import com.gmail.takenokoii78.mojangson.values.MojangsonString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class DatapackMessageReceiveEvent implements TPLEvent {
    private final CommandSourceStack stack;

    private final MojangsonCompound input;

    private final MojangsonCompound output;

    private int returnValue = 0;

    public DatapackMessageReceiveEvent(CommandSourceStack stack, MojangsonCompound input) {
        this.stack = stack;
        this.input = input;
        this.output = new MojangsonCompound();
    }

    @Override
    public TPLEventType<? extends TPLEvent> getType() {
        return TPLEventTypes.DATAPACK_MESSAGE_RECEIVE;
    }

    public CommandSourceStack getStack() {
        return stack;
    }

    public MojangsonCompound getInput() {
        return input;
    }

    public MojangsonCompound getOutput() {
        return output;
    }

    public String getId() {
        return input.get("id", MojangsonValueTypes.STRING).getValue();
    }

    public Set<Entity> getTargets() {
        if (!(input.has("targets") && input.getTypeOf("targets").equals(MojangsonValueTypes.LIST))) {
            return Set.of();
        }

        final MojangsonList list = input.get("targets", MojangsonValueTypes.LIST);

        if (!list.isListOf(MojangsonValueTypes.STRING)) {
            return Set.of();
        }

        final Set<Entity> targets = new HashSet<>();

        for (final MojangsonString string : list.typed(MojangsonValueTypes.STRING)) {
            final UUID uuid;
            try {
                uuid = UUID.fromString(string.getValue());
            }
            catch (IllegalArgumentException e) {
                continue;
            }

            final Entity entity = Bukkit.getEntity(uuid);

            if (entity == null) continue;

            targets.add(entity);
        }

        return targets;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int value) {
        returnValue = value;
    }
}
