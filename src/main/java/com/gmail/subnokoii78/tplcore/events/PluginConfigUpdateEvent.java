package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.files.PluginConfigLoader;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class PluginConfigUpdateEvent implements TPLEvent {
    private final PluginConfigLoader loader;

    public PluginConfigUpdateEvent(PluginConfigLoader loader) {
        this.loader = loader;
    }

    public PluginConfigLoader getLoader() {
        return loader;
    }

    @Override
    public TPLEventType<? extends TPLEvent> getType() {
        return TPLEventTypes.PLUGIN_CONFIG_UPDATE;
    }
}
