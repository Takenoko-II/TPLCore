package com.gmail.subnokoii78.tplcore.ui.container;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemButtonCreator extends ItemButton {
    protected ItemButtonCreator() {
        super(Material.BARRIER);
    }

    public abstract @NotNull ItemButton create(@NotNull Player player);

    @Override
    protected final @NotNull ItemStack build() throws IllegalStateException {
        throw new IllegalStateException("create()が返すオブジェクトのbuild()を使用してください");
    }
}
