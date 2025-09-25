package com.gmail.subnokoii78.tplcore.api;

import com.gmail.subnokoii78.tplcore.TPLCore;
import com.gmail.subnokoii78.tplcore.events.DatapackMessageReceiveEvent;
import com.gmail.subnokoii78.tplcore.events.EventDispatcher;
import com.gmail.subnokoii78.tplcore.events.TPLEventTypes;
import com.gmail.subnokoii78.tplcore.execute.*;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

import java.util.List;

@NullMarked
public final class PluginApi {
    public static final String NAMESPACE = "plugin_api";

    public static final String TRIGGER = NAMESPACE + "__trigger__";

    public static final String KEY = "test_key";

    private static final ResourceLocation STORAGE_BROADCASTING = ResourceLocation.fromNamespaceAndPath(NAMESPACE, "broadcast");

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

    private @Nullable Pair<CommandSourceStack, MojangsonCompound> readBroadcastingInput() {
        final MojangsonCompound compound = readBroadcasting();

        if (!(compound.has("in") && compound.getTypeOf("in").equals(MojangsonValueTypes.COMPOUND))) {
            return null;
        }

        final MojangsonCompound in = compound.get("in", MojangsonValueTypes.COMPOUND);

        final String id;
        final DimensionAccess dimension;
        final Vector3Builder position;
        final DualAxisRotationBuilder rotation;
        final Entity executor;

        try {
            id = compound.get("id", MojangsonValueTypes.STRING).getValue();

            dimension = DimensionAccess.of(
                compound.get("dimension", MojangsonValueTypes.STRING).getValue()
            );

            final TypedMojangsonList<MojangsonDouble> posList = compound.get("position", MojangsonValueTypes.LIST).typed(MojangsonValueTypes.DOUBLE);

            if (posList.length() != 3) throw new IllegalStateException("position vector length must be 3");

            position = new Vector3Builder(
                posList.get(0).getValue(),
                posList.get(1).getValue(),
                posList.get(2).getValue()
            );

            final TypedMojangsonList<MojangsonFloat> rotList = compound.get("rotation", MojangsonValueTypes.LIST).typed(MojangsonValueTypes.FLOAT);

            if (rotList.length() != 2) throw new IllegalStateException("rotation vector length must be 2");

            rotation = new DualAxisRotationBuilder(
                rotList.get(0).getValue(),
                rotList.get(1).getValue()
            );
        }
        catch (Exception e) {
            compound.set("in", new MojangsonCompound());
            writeBroadcasting(compound);
            throw new IllegalStateException("プラグインAPIからの入力の解析に失敗しました: ", e);
        }

        final EntitySelector<Entity> selector = EntitySelector.E.arg(SelectorArgument.TAG, "plugin_api.executor");
        final List<Entity> entities = new CommandSourceStack().getEntities(selector);

        if (entities.size() > 1) {
            throw new IllegalStateException("プラグインAPIからの入力の解析に失敗しました: the number of executor must be zero or one");
        }

        if (entities.isEmpty()) executor = null;
        else executor = entities.getFirst();

        final CommandSourceStack stack = new CommandSourceStack(
            PLUGIN_API_COMMAND_SENDER,
            executor,
            new Location(
                dimension.getWorld(),
                position.x(),
                position.y(),
                position.z(),
                rotation.yaw(),
                rotation.pitch()
            )
        );

        compound.set("in", new MojangsonCompound());
        writeBroadcasting(compound);
        return new Pair<>(stack, in);
    }

    private void writeBroadcastingOutput(MojangsonCompound output) {
        if (!(output.has("returnValue") && output.getTypeOf("returnValue").equals(MojangsonValueTypes.INT))) {
            throw new IllegalArgumentException("returnValue: int が見つかりませんでした");
        }

        final MojangsonCompound compound = readBroadcasting();
        compound.set("out", output);
        writeBroadcasting(compound);
    }

    public void trigger(String key) {
        if (!key.equals(KEY)) {
            return;
        }

        final Pair<CommandSourceStack, MojangsonCompound> in = readBroadcastingInput();

        if (in == null) {
            return;
        }

        final DatapackMessageReceiveEvent event = new DatapackMessageReceiveEvent(
            in.a(),
            in.b()
        );

        TPLCore.events.getDispatcher(TPLEventTypes.DATAPACK_MESSAGE_RECEIVE).dispatch(event);

        event.getOutput().set("returnValue", event.getReturnValue());
        writeBroadcastingOutput(event.getOutput());
    }

    private static final CommandSender PLUGIN_API_COMMAND_SENDER = Bukkit.createCommandSender($ -> {

    });

    private record Pair<A, B>(A a, B b) {}
}
