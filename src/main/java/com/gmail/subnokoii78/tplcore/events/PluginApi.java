package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.datapack.Datapack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NullMarked
public final class PluginApi {
    public static final String ID = "plugin_api";

    public static final String BROADCASTING = "broadcasting";

    public static final String EXECUTOR_ENTITY_TAG = ID + '.' + "executor";

    public static final String MESSENGER_ENTITY_TAG = ID + '.' + "messenger";

    public static final String TARGET_ENTITY_TAG = ID + '.' + "target";

    private static final ResourceLocation STORAGE = ResourceLocation.fromNamespaceAndPath(ID, "");

    private static final CommandSender PLUGIN_API_COMMAND_SENDER = Bukkit.createCommandSender($ -> {

    });

    public PluginApi() {

    }

    private MojangsonCompound readStorage() {
        final CompoundTag tag = MinecraftServer.getServer().getCommandStorage().get(STORAGE);

        return MojangsonParser.compound(tag.toString());
    }

    private void writeStorage(MojangsonCompound compound) {
        final CompoundTag tag;
        try {
            tag = TagParser.parseCompoundFully(
                MojangsonSerializer.serialize(compound)
            );
        }
        catch (CommandSyntaxException e) {
            throw new IllegalArgumentException();
        }

        MinecraftServer.getServer().getCommandStorage().set(STORAGE, tag);
    }

    private MojangsonCompound readBroadcasting() {
        final MojangsonCompound compound = readStorage();

        if (compound.has(BROADCASTING) && compound.getTypeOf(BROADCASTING).equals(MojangsonValueTypes.COMPOUND)) {
            return compound.get(BROADCASTING, MojangsonValueTypes.COMPOUND);
        }
        else return new MojangsonCompound();
    }

    private void writeBroadcasting(MojangsonCompound compound) {
        final MojangsonCompound root = readStorage();

        root.set(BROADCASTING, compound);

        writeStorage(root);
    }

    private @Nullable MojangsonCompound readBroadcastingInput() {
        final MojangsonCompound broadcasting = readBroadcasting();

        if (!(broadcasting.has("in") && broadcasting.getTypeOf("in").equals(MojangsonValueTypes.COMPOUND))) {
            System.out.println(broadcasting);
            return null;
        }

        final MojangsonCompound in = broadcasting.get("in", MojangsonValueTypes.COMPOUND);

        final String id;
        try {
            id = in.get("id", MojangsonValueTypes.STRING).getValue();
        }
        catch (Exception e) {
            System.out.println(broadcasting);
            return null;
        }

        broadcasting.set("in", new MojangsonCompound());
        writeBroadcasting(broadcasting);
        return in;
    }

    private void writeBroadcastingOutput(MojangsonCompound output) {
        if (!(output.has("return_value") && output.getTypeOf("return_value").equals(MojangsonValueTypes.INT))) {
            output.set("return_value", 0);
        }

        final MojangsonCompound broadcasting = readBroadcasting();
        broadcasting.set("out", output);
        writeBroadcasting(broadcasting);
    }

    private @Nullable CommandSourceStack getContext(Entity messenger) {
        final EntitySelector<Entity> executorSelector = EntitySelector.E.arg(SelectorArgument.TAG, EXECUTOR_ENTITY_TAG);
        final List<Entity> executors = new CommandSourceStack().getEntities(executorSelector);

        final Entity executor;
        if (executors.isEmpty()) {
            executor = null;
        }
        else if (executors.size() > 1) {
            throw new IllegalArgumentException("Could not get context: executor must be single entity");
        }
        else {
            executor = executors.getFirst();
        }

        final Location location = messenger.getLocation();
        messenger.remove();

        return new CommandSourceStack(
            PLUGIN_API_COMMAND_SENDER,
            executor,
            location
        );
    }

    private Set<Entity> getTargets() {
        return new HashSet<>(new CommandSourceStack().getEntities(EntitySelector.E.arg(SelectorArgument.TAG, TARGET_ENTITY_TAG)));
    }

    void broadcast(Entity messenger) {
        final MojangsonCompound in = readBroadcastingInput();
        final CommandSourceStack context = getContext(messenger);
        final Set<Entity> targets = getTargets();

        if (in == null || context == null) {
            System.out.println("null stopper: in=" + in + ", context=" + context);
            return;
        }

        final DatapackMessageReceiveEvent event = new DatapackMessageReceiveEvent(context, in, targets);

        TPLCore.events.getDispatcher(TPLEventTypes.DATAPACK_MESSAGE_RECEIVE).dispatch(event);

        event.getOutput().set("return_value", event.getReturnValue());
        writeBroadcastingOutput(event.getOutput());
    }

    public String getPackName() {
        return TPLCore.getPlugin().getName() + '/' + ID;
    }

    public Datapack getDatapack() {
        final Datapack datapack = Bukkit.getDatapackManager().getPack(getPackName());

        if (datapack == null) {
            throw new IllegalStateException("データパック '" + ID + "' の取得に失敗しました");
        }

        return datapack;
    }
}
