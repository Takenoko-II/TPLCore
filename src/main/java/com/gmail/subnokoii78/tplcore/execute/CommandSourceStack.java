package com.gmail.subnokoii78.tplcore.execute;

import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.level.ServerLevel;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.command.VanillaCommandWrapper;
import org.bukkit.craftbukkit.entity.CraftEntity;

import org.spigotmc.AsyncCatcher;

/**
 * executeコマンドにおける単一の実行文脈を表現するクラス
 */
@NullMarked
public class CommandSourceStack {
    private final CommandSender sender;

    @Nullable
    private Entity executor = null;

    private World dimension = DimensionAccess.OVERWORLD.getWorld();

    private final Vector3Builder location = new Vector3Builder();

    private final DualAxisRotationBuilder rotation = new DualAxisRotationBuilder();

    private final EntityAnchor entityAnchor = new EntityAnchor(this);

    private ResultCallback resultCallback = ResultCallback.EMPTY;

    /**
     * 初期状態のソーススタックを生成します。
     */
    public CommandSourceStack() {
        this(Bukkit.getConsoleSender());
    }

    /**
     * 引数に渡されたオブジェクトをコマンドの送信者としてソーススタックを生成します。
     * @param sender 送信者
     */
    public CommandSourceStack(CommandSender sender) {
        this.sender = sender;

        final net.minecraft.commands.CommandSourceStack stack = VanillaCommandWrapper.getListener(sender);
        final Entity entity = stack.getExecutor();
        final Location location = stack.getLocation();

        if (entity != null) write(entity);
        write(Vector3Builder.from(location));
        write(DualAxisRotationBuilder.from(location));
        write(location.getWorld());
    }

    public CommandSourceStack(CommandSender sender, @Nullable Entity executor, Location location) {
        this.sender = sender;
        write(executor);
        write(location.getWorld());
        write(Vector3Builder.from(location));
        write(DualAxisRotationBuilder.from(location));
    }

    /**
     * コマンドの送信者(実行者)を取得します。
     * @return 送信者
     */
    public CommandSender getSender() {
        return sender;
    }

    /**
     * 実行者が存在するかどうかをテストします。
     * @return 実行者が存在すればtrue、しなければfalse
     */
    public boolean hasExecutor() {
        if (executor == null) return false;
        else return executor.isValid();
    }

    /**
     * コマンドの実行者を取得します。
     * <br>先に{@link CommandSourceStack#hasExecutor()}を呼び出してください
     * @return 実行者
     */
    public Entity getExecutor() throws IllegalStateException {
        if (executor != null) return executor;
        else throw new IllegalStateException("実行者が存在しません");
    }

    public @Nullable Entity getExecutorOrNull() {
        return executor;
    }

    /**
     * 実行ディメンションを返します。
     * @return 実行ディメンション
     */
    public World getDimension() {
        return dimension;
    }

    /**
     * 実行座標を返します。
     * @return 実行座標(3次元ベクトル)
     */
    public Vector3Builder getPosition() {
        return location.copy();
    }

    /**
     * 実行方向を返します。
     * @return 実行方向(2次元ベクトル)
     */
    public DualAxisRotationBuilder getRotation() {
        return rotation.copy();
    }

    /**
     * 実行座標、実行方向、実行ディメンションの三つを一つの{@link Location}オブジェクトとして取得します。
     * @return 実行座標・実行方向・実行ディメンション
     */
    public Location getLocation(LocationGetOption... options) {
        final Set<LocationGetOption> set = Set.of(options);

        if (set.isEmpty()) {
            return location.withRotationAndWorld(rotation, dimension);
        }

        final Location loc = new Location(DimensionAccess.OVERWORLD.getWorld(), 0d, 0d, 0d, 0f, 0f);

        if (set.contains(LocationGetOption.DIMENSION)) {
            loc.setWorld(dimension);
        }
        if (set.contains(LocationGetOption.POSITION)) {
            loc.setX(location.x());
            loc.setY(location.y());
            loc.setZ(location.z());
        }
        if (set.contains(LocationGetOption.ROTATION)) {
            loc.setYaw(rotation.yaw());
            loc.setPitch(rotation.pitch());
        }

        return loc;
    }

    /**
     * 実行アンカーを返します。
     * @return 実行アンカー(デフォルトでfeet)
     */
    public EntityAnchor getEntityAnchor() {
        return entityAnchor;
    }

    public ResultCallback getCallback() {
        return resultCallback;
    }

    protected void write(@Nullable Entity executor) {
        this.executor = executor;
    }

    protected void write(World dimension) {
        this.dimension = dimension;
    }

    protected void write(Vector3Builder location) {
        this.location.x(location.x()).y(location.y()).z(location.z());
    }

    protected void write(DualAxisRotationBuilder rotation) {
        this.rotation.yaw(rotation.yaw()).pitch(rotation.pitch());
    }

    protected void write(EntityAnchor.Type type) {
        this.entityAnchor.setType(type);
    }

    protected void write(StoreTarget storeTarget, ResultConsumer resultConsumer) {
        this.resultCallback = this.resultCallback.chain(storeTarget, (successful, returnValue) -> {
            resultConsumer.accept(copy(), returnValue);
        });
    }

    private double parseAbsolutePos(String input) {
        if (input.matches("^[+-]?\\d$")) {
            return Double.parseDouble(input) + 0.5d;
        }
        else if (input.matches("^[+-]?\\d+(?:\\.\\d+)?$")) {
            return Double.parseDouble(input);
        }
        else {
            throw new IllegalArgumentException("絶対座標が期待されています");
        }
    }

    private double parseRelativePos(String input, int axis) {
        if (input.matches("^~(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
            final String number = input.substring(1);
            final double offset = number.isEmpty() ? 0 : Double.parseDouble(number);

            return switch (axis) {
                case 0 -> offset + location.x();
                case 1 -> offset + location.y();
                case 2 -> offset + location.z();
                default ->
                    throw new IllegalArgumentException("NEVER HAPPENS");
            };
        }
        else {
            throw new IllegalArgumentException("相対座標が期待されています");
        }
    }

    private Vector3Builder parseLocalPos(List<String> components) {
        final Vector3Builder out = location.copy();

        int i = 0;
        for (final String input : components) {
            if (input.matches("^\\^(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
                final Vector3Builder.LocalAxisProvider localAxis = rotation.getDirection3d().getLocalAxisProvider();
                final String number = input.substring(1);
                final double length = number.isEmpty() ? 0 : Double.parseDouble(number);

                final Vector3Builder offset = switch (i) {
                    case 0 -> localAxis.getX().length(length);
                    case 1 -> localAxis.getY().length(length);
                    case 2 -> localAxis.getZ().length(length);
                    default ->
                        throw new IllegalArgumentException("NEVER HAPPENS");
                };

                out.add(offset);
            }
            else {
                throw new IllegalArgumentException("ローカル座標が期待されています");
            }

            i++;
        }

        return out.add(getEntityAnchor().getOffset());
    }

    /**
     * コマンドの引数としての形式で記述された座標を読み取ります。
     * @param coordinates 解析する文字列
     * @return 絶対座標
     */
    public Vector3Builder readCoordinates(String coordinates) {
        final List<String> componentInputs = List.of(coordinates.split("\\s"));
        final List<Double> componentOutputs = new ArrayList<>();

        if (componentInputs.size() != 3) throw new IllegalArgumentException("座標は三軸です");

        for (int i = 0; i < componentInputs.size(); i++) {
            final String value = componentInputs.get(i);

            if (value.startsWith("~")) {
                componentOutputs.add(parseRelativePos(value, i));
            }
            else if (value.startsWith("^")) {
                if (i == 0) {
                    final Vector3Builder v = parseLocalPos(componentInputs);
                    componentOutputs.addAll(List.of(v.x(), v.y(), v.z()));
                    break;
                }
                else throw new IllegalArgumentException("ローカル座標とほかの記述形式を混ぜることはできません");
            }
            else {
                componentOutputs.add(parseAbsolutePos(value));
            }
        }

        return new Vector3Builder(componentOutputs.get(0), componentOutputs.get(1), componentOutputs.get(2));
    }

    private float parseAbsoluteRot(String input) {
        if (input.matches("^[+-]?\\d+(?:\\.\\d+)?$")) {
            return Float.parseFloat(input);
        }
        else {
            throw new IllegalArgumentException("絶対角度が期待されています");
        }
    }

    private float parseRelativeRot(String input, int axis) {
        if (input.matches("^~(?:[+-]?\\d+(?:\\.\\d+)?)?$")) {
            final String number = input.substring(1);
            final float offset = number.isEmpty() ? 0 : Float.parseFloat(number);

            return switch (axis) {
                case 0 -> offset + rotation.yaw();
                case 1 -> offset + rotation.pitch();
                default ->
                    throw new IllegalArgumentException("NEVER HAPPENS");
            };
        }
        else {
            throw new IllegalArgumentException("相対座標が期待されています");
        }
    }

    /**
     * コマンドの引数としての形式で記述された角度を読み取ります。
     * @param angles 解析する文字列
     * @return 絶対回転
     */
    public DualAxisRotationBuilder readAngles(String angles) {
        final List<String> componentInputs = List.of(angles.split("\\s"));
        final List<Float> componentOutputs = new ArrayList<>();

        if (componentInputs.size() != 2) throw new IllegalArgumentException("座標は二軸です");

        for (int i = 0; i < componentInputs.size(); i++) {
            final String value = componentInputs.get(i);

            if (value.startsWith("~")) {
                componentOutputs.add(parseRelativeRot(value, i));
            }
            else {
                componentOutputs.add(parseAbsoluteRot(value));
            }
        }

        return new DualAxisRotationBuilder(componentOutputs.get(0), componentOutputs.get(1));
    }

    /**
     * 渡されたセレクターから処理順に従ってエンティティを取得します。
     * @param selector セレクター
     * @return エンティティのリスト
     */
    public <T extends Entity> List<T> getEntities(EntitySelector<T> selector) {
        return selector.getEntities(this);
    }

    /**
     * 渡されたセレクターから処理順に従ってエンティティを取得します。
     * @param selector セレクター
     * @return エンティティのリスト
     */
    public <T extends Entity> List<T> getEntities(EntitySelector.Builder<T> selector) {
        return getEntities(selector.build());
    }

    /**
     * このソーススタックをコピーします。
     * @return コピーされたオブジェクト
     */
    public CommandSourceStack copy() {
        final CommandSourceStack stack = new CommandSourceStack(sender);
        stack.write(dimension);
        stack.write(executor);
        stack.write(location.copy());
        stack.write(rotation.copy());
        stack.write(entityAnchor.getType());
        stack.resultCallback = resultCallback;
        return stack;
    }

    @NetMinecraftServer
    private net.minecraft.commands.CommandSourceStack toNMS(boolean sendsOutput) {
        var stack = VanillaCommandWrapper.getListener(getSender())
            .withPermission(Commands.LEVEL_OWNERS)
            .withLocation(getLocation())
            .withAnchor(
                getEntityAnchor().getType().equals(EntityAnchor.EYES)
                    ? EntityAnchorArgument.Anchor.EYES
                    : EntityAnchorArgument.Anchor.FEET
            );

        if (hasExecutor()) {
            stack = stack.withEntity(((CraftEntity) getExecutor()).getHandle());
        }

        if (!sendsOutput) {
            stack = stack.withSuppressedOutput();
        }

        return stack;
    }

    @NetMinecraftServer
    @ApiStatus.Experimental
    public int matchRegions(String begin, String end, String destination, ScanMode scanMode) {
        final net.minecraft.commands.CommandSourceStack nms = toNMS(false);

        final Vector3Builder beginV = readCoordinates(begin);
        final Vector3Builder endV = readCoordinates(end);
        final Vector3Builder destinationV = readCoordinates(destination);

        final OptionalInt i;
        try {
            final Method method = ExecuteCommand.class.getDeclaredMethod(
                "checkRegions",
                ServerLevel.class,
                BlockPos.class,
                BlockPos.class,
                BlockPos.class,
                boolean.class
            );

            method.setAccessible(true);

            i = (OptionalInt) method.invoke(
                null,
                nms.getLevel(),
                beginV.toIntVector(true).toNMSBlockPos(),
                endV.toIntVector(true).toNMSBlockPos(),
                destinationV.toIntVector(true).toNMSBlockPos(),
                scanMode.equals(ScanMode.MASKED)
            );
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException("net.minecraft.server.commands.ExecuteCommand::checkRegions() へのアクセスに失敗しました", e);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("net.minecraft.server.commands.ExecuteCommand::checkRegions() の実行に失敗しました", e);
        }

        if (i.isEmpty()) {
            return 0;
        }
        else {
            return i.getAsInt();
        }
    }

    @NetMinecraftServer
    public boolean runCommand(String commandLine, boolean sendsOutput) {
        AsyncCatcher.catchOp("Command Dispatched Async: " + commandLine);
        final String command = StringUtils.normalizeSpace(commandLine.trim());
        final Commands commands = ((CraftServer) Bukkit.getServer()).getHandle().getServer().getCommands();
        final CommandDispatcher<net.minecraft.commands.CommandSourceStack> dispatcher = commands.getDispatcher();
        final ParseResults<net.minecraft.commands.CommandSourceStack> results = dispatcher.parse(command, toNMS(sendsOutput));

        try {
            Commands.validateParseResults(results);
        }
        catch (CommandSyntaxException e) {
            return false;
        }

        if (results.getContext().getNodes().isEmpty()) {
            return false;
        }

        commands.performCommand(results, command, false);

        return true;
    }

    /**
     * コマンドを実行します。
     * @param command 実行するコマンド
     * @return 成功したときtrue、失敗すればfalse
     * @apiNote 安定こそしているが、送信者の情報が十分でない
     */
    @ApiStatus.Obsolete
    public boolean runCommandLegacy(String command) {
        final String common = String.format(
            "in %s positioned %s rotated %s",
            DimensionAccess.of(dimension).getId(),
            location.format("$c $c $c", 5),
            rotation.format("$c $c", 5)
        );

        final String commandString;

        if (hasExecutor()) {
            commandString = String.format(
                "execute %s as %s anchored %s run %s",
                common,
                getExecutor().getUniqueId(),
                entityAnchor.getType().getId(),
                command
            );
        }
        else {
            commandString = String.format(
                "execute %s anchored %s run %s",
                common,
                entityAnchor.getType().getId(),
                command
            );
        }

        try {
            return Bukkit.dispatchCommand(COMMAND_SENDER, commandString);
        }
        catch (CommandException e) {
            return false;
        }
    }

    public static CommandSourceStack fromPaper(io.papermc.paper.command.brigadier.CommandSourceStack stack) {
        return new CommandSourceStack(
            stack.getSender(),
            stack.getExecutor(),
            stack.getLocation()
        );
    }

    /**
     * 架空のコマンド送信者のオブジェクト
     */
    private static final CommandSender COMMAND_SENDER = Bukkit.createCommandSender(component -> {});
}
