package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.execute.CommandSourceStack;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;

import java.util.Set;

@NullMarked
public class DatapackMessageReceiveEvent implements TPLEvent {
    private final CommandSourceStack context;

    private final MojangsonCompound input;

    private final Set<Entity> targets;

    private final MojangsonCompound output;

    private int returnValue = 0;

    public DatapackMessageReceiveEvent(CommandSourceStack context, MojangsonCompound input, Set<Entity> targets) {
        this.context = context;
        this.input = input;
        this.targets = targets;
        this.output = new MojangsonCompound();
    }

    @Override
    public TPLEventType<? extends TPLEvent> getType() {
        return TPLEventTypes.DATAPACK_MESSAGE_RECEIVE;
    }

    public CommandSourceStack getContext() {
        return context;
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
        return targets;
    }

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int value) {
        returnValue = value;
    }
}
