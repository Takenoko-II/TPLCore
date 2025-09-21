package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.execute.NumberRange;
import com.gmail.subnokoii78.tplcore.random.LootTable;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class Main {
    public static void main(String[] args) {
        // TODO: ContainerInteraction 動作確認
        // TODO: DynamicLootTableBuilder をつくる (PaperAPIにはレジストリからの取得しかできない)

        final LootTable lootTable = LootTable.of(
            LootTable.Pool.of(
                1,
                LootTable.Entry.of(Material.APPLE)
                    .count(NumberRange.of(1, 5))
                    .weight(1)
                    .function((meta, randomService) -> {
                        meta.setUnbreakable(true);
                        meta.addEnchant(Enchantment.INFINITY, randomService.getRandomizer().randInt(NumberRange.of(1, 3)), true);
                    })
            )
        );
    }
}
