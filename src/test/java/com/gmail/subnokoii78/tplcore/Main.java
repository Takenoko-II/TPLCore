package com.gmail.subnokoii78.tplcore;

import com.gmail.subnokoii78.tplcore.json.JSONParser;
import com.gmail.subnokoii78.tplcore.json.JSONPath;
import com.gmail.subnokoii78.tplcore.json.JSONPathParser;
import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import com.gmail.subnokoii78.tplcore.ui.container.ContainerInteraction;
import com.gmail.subnokoii78.tplcore.ui.container.ItemButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;

public class Main {
    public static void main(String[] args) {
        // TODO: events
        // TODO: init() を呼ぶのをこのライブラリひとつで一回にしたい
        // TODO: LR Click Event から　ClickEventをよびたい

        final JSONObject object = JSONParser.object("""
            {
                "foo": {
                    "bar": 0
                }
            }
            """);

        final JSONPath path = JSONPathParser.parse("foo.bar");

        System.out.println(path.get(object));

        new ContainerInteraction(Component.text("a"), 1)
            .add(new ItemButton(Material.CHEST).onClick(event -> {
                event;
            }));
    }
}
