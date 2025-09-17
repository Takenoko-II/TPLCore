package com.gmail.subnokoii78.tplcore.ui.container;

import com.gmail.subnokoii78.tplcore.events.EventDispatcher;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

@ApiStatus.Experimental
public class ContainerInteraction {
    private static final class InteractionInventoryHolder implements InventoryHolder {
        private final ContainerInteraction interactionBuilder;

        private final Inventory inventory;

        private InteractionInventoryHolder(@NotNull ContainerInteraction builder) {
            interactionBuilder = builder;
            inventory = Bukkit.createInventory(this, builder.maxColumn * 9, builder.name);
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }

    private static final Set<InteractionInventoryHolder> observedInventoryHolders = new HashSet<>();

    private final TextComponent name;

    private final int maxColumn;

    private final Map<Integer, ItemButton> buttons = new HashMap<>();

    private final EventDispatcher<InteractionCloseEvent> closeEventDispatcher = new EventDispatcher<>(ContainerInteractionEvent.INTERACTION_CLOSE);

    public ContainerInteraction(@NotNull TextComponent name, int maxColumn) {
        this.name = name;
        this.maxColumn = maxColumn;
    }

    public @NotNull TextComponent getName() {
        return name;
    }

    public int getSize() {
        return maxColumn * 9;
    }

    public int getFirstEmptySlot() throws IllegalStateException {
        for (int i = 0; i < getSize(); i++) {
            if (buttons.containsKey(i)) continue;
            return i;
        }
        throw new IllegalStateException("空のスロットが存在しません");
    }

    public boolean hasEmptySlot() {
        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) return true;
        }
        return false;
    }

    public @NotNull ContainerInteraction set(int slot, @Nullable ItemButton button) throws IllegalArgumentException {
        if (getSize() <= slot) {
            throw new IllegalArgumentException("範囲外のスロットが渡されました");
        }

        if (button == null) buttons.remove(slot);
        else buttons.put(slot, button);
        return this;
    }

    public @NotNull ContainerInteraction add(@NotNull ItemButton button) throws IllegalStateException {
        buttons.put(getFirstEmptySlot(), button);
        return this;
    }

    public @NotNull ContainerInteraction fillRow(int index, @NotNull ItemButton button) {
        for (int i = index * 9; i < index * 9 + 9; i++) {
            set(i, button);
        }
        return this;
    }

    public @NotNull ContainerInteraction fillColumn(int index, @NotNull ItemButton button) {
        for (int i = 0; i < maxColumn; i++) {
            set(i * 9 + index, button);
        }
        return this;
    }

    public @NotNull ContainerInteraction clear() {
        buttons.clear();
        return this;
    }

    public @NotNull ContainerInteraction onClose(@NotNull Consumer<InteractionCloseEvent> listener) {
        closeEventDispatcher.add(listener);
        return this;
    }

    public void open(@NotNull Player player) {
        final InteractionInventoryHolder inventoryHolder = new InteractionInventoryHolder(this);
        observedInventoryHolders.add(inventoryHolder);

        for (int i = 0; i < getSize(); i++) {
            if (!buttons.containsKey(i)) continue;

            final ItemButton button = buttons.get(i);

            if (button instanceof ItemButtonCreator creator) {
                inventoryHolder.inventory.setItem(i, creator.create(player).build());
            }
            else {
                inventoryHolder.inventory.setItem(i, button.build());
            }
        }

        player.closeInventory();
        player.openInventory(inventoryHolder.inventory);
    }

    public static final class ContainerEventObserver implements Listener {
        private ContainerEventObserver() {}

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            final ItemStack itemStack = event.getCurrentItem();

            for (final InteractionInventoryHolder holder : observedInventoryHolders) {
                if (holder.inventory.equals(event.getClickedInventory())) {
                    final ItemButton button = holder.interactionBuilder.buttons.get(event.getSlot());

                    if (itemStack == null || button == null) return;

                    button.click(new ItemButtonClickEvent(player, holder.interactionBuilder, event.getSlot(), button));
                    event.setCancelled(true);
                    break;
                }
            }
        }

        @EventHandler
        public void onMove(InventoryMoveItemEvent event) {
            for (final InteractionInventoryHolder holder : observedInventoryHolders) {
                if (holder.inventory.equals(event.getInitiator())) {
                    event.setCancelled(true);
                    break;
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (!(event.getPlayer() instanceof Player player)) return;

            for (final InteractionInventoryHolder holder : observedInventoryHolders) {
                final Inventory inventory = event.getInventory();

                if (holder.inventory.equals(inventory)) {
                    observedInventoryHolders.remove(holder);
                    holder.interactionBuilder.closeEventDispatcher.dispatch(new InteractionCloseEvent(holder.interactionBuilder, player));
                    break;
                }
            }
        }

        public static final ContainerInteraction.ContainerEventObserver INSTANCE = new ContainerInteraction.ContainerEventObserver();
    }
}
