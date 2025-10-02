package com.gmail.subnokoii78.tplcore.events;

import com.gmail.subnokoii78.tplcore.eval.IScriptContext;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class ScriptApiContextInitializeEvent implements TPLEvent {
    private final Class<? extends IScriptContext> clazz;

    private final IScriptContext context;

    public ScriptApiContextInitializeEvent(Class<? extends IScriptContext> clazz, IScriptContext context) {
        this.clazz = clazz;
        this.context = context;
    }

    public Class<? extends IScriptContext> getClazz() {
        return clazz;
    }

    public <T extends IScriptContext> T getContext(Class<T> clazz) {
        return clazz.cast(context);
    }

    @Override
    public TPLEventType<? extends TPLEvent> getType() {
        return TPLEventTypes.SCRIPT_API_CONTEXT_INITIALIZE;
    }
}
