package com.gmail.subnokoii78.tplcore.api;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.DatapackMessageReceiveEvent;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

@NullMarked
public final class PluginApi {
    public static final String NAMESPACE = "plugin_api";

    public static final String TRIGGER = NAMESPACE + ':' + "__trigger__";

    public static final String EXECUTOR_ENTITY_TAG = NAMESPACE + '.' + "executor";

    public static final String LOCATION_MESSENGER_ENTITY_TAG = NAMESPACE + '.' + "location_messenger";

    public static final String KEY = "KEY";

    private static final ResourceLocation STORAGE_BROADCASTING = ResourceLocation.fromNamespaceAndPath(NAMESPACE, "broadcast");

    private static final CommandSender PLUGIN_API_COMMAND_SENDER = Bukkit.createCommandSender($ -> {

    });

    public PluginApi() {

    }

    private MojangsonCompound readBroadcasting() {
        final CompoundTag tag = MinecraftServer.getServer().getCommandStorage().get(STORAGE_BROADCASTING);

        return MojangsonParser.compound(tag.toString());
    }

    private void writeBroadcasting(MojangsonCompound compound) {
        final CompoundTag tag;
        try {
            tag = TagParser.parseCompoundFully(
                MojangsonSerializer.serialize(compound)
            );
        }
        catch (CommandSyntaxException e) {
            throw new IllegalArgumentException();
        }

        MinecraftServer.getServer().getCommandStorage().set(STORAGE_BROADCASTING, tag);
    }

    private @Nullable MojangsonCompound readBroadcastingInput() {
        final MojangsonCompound compound = readBroadcasting();

        if (!(compound.has("in") && compound.getTypeOf("in").equals(MojangsonValueTypes.COMPOUND))) {
            return null;
        }

        final MojangsonCompound in = compound.get("in", MojangsonValueTypes.COMPOUND);

        final String id;
        try {
            id = compound.get("id", MojangsonValueTypes.STRING).getValue();
        }
        catch (Exception e) {
            return null;
        }

        compound.set("in", new MojangsonCompound());
        writeBroadcasting(compound);
        return in;
    }

    private void writeBroadcastingOutput(MojangsonCompound output) {
        if (!(output.has("returnValue") && output.getTypeOf("returnValue").equals(MojangsonValueTypes.INT))) {
            throw new IllegalArgumentException("returnValue: int が見つかりませんでした");
        }

        final MojangsonCompound compound = readBroadcasting();
        compound.set("out", output);
        writeBroadcasting(compound);
    }

    private @Nullable CommandSourceStack getContext() {
        final EntitySelector<Entity> executorSelector = EntitySelector.E.arg(SelectorArgument.TAG, EXECUTOR_ENTITY_TAG);
        final List<Entity> executors = new CommandSourceStack().getEntities(executorSelector);

        final Entity executor;
        if (executors.isEmpty()) {
            executor = null;
        }
        else if (executors.size() > 1) {
            throw new IllegalArgumentException("Could not get context: executor must be 1");
        }
        else {
            executor = executors.getFirst();
        }

        final EntitySelector<Entity> locationMessengerSelector = EntitySelector.E.arg(SelectorArgument.TAG, LOCATION_MESSENGER_ENTITY_TAG);
        final List<Entity> locationMessengers = new CommandSourceStack().getEntities(locationMessengerSelector);

        final Entity locationMessenger;
        if (locationMessengers.isEmpty()) {
            return null;
        }
        else if (locationMessengers.size() > 1) {
            throw new IllegalArgumentException("Could not get context: location messenger must be 1");
        }
        else {
            locationMessenger = locationMessengers.getFirst();
        }

        return new CommandSourceStack(
            PLUGIN_API_COMMAND_SENDER,
            executor,
            locationMessenger.getLocation()
        );
    }

    /**
     * commands:
     * <br>tag @s add plugin_api.executor
     * <br>summon marker ~ ~ ~ {Tags: ["plugin_api.location_messenger"]}
     * <br>function plugin_api:__trigger__ {key: "KEY"}
     * <br>tag @s remove plugin_api.executor
     * <br>kill @e[type=marker,tag=plugin_api.location_messenger,limit=1]
     *
     * @param key {@value KEY}
     */
    public void trigger(String key) {
        if (!key.equals(KEY)) {
            return;
        }

        final MojangsonCompound in = readBroadcastingInput();
        final CommandSourceStack context = getContext();

        if (in == null || context == null) {
            return;
        }

        final DatapackMessageReceiveEvent event = new DatapackMessageReceiveEvent(context, in);

        TPLCore.events.getDispatcher(TPLEventTypes.DATAPACK_MESSAGE_RECEIVE).dispatch(event);

        event.getOutput().set("returnValue", event.getReturnValue());
        writeBroadcastingOutput(event.getOutput());
    }
}
