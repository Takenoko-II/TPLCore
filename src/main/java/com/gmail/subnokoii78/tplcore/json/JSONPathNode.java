package com.gmail.subnokoii78.tplcore.json;

import com.gmail.subnokoii78.tplcore.generic.TupleLR;
import com.gmail.subnokoii78.tplcore.json.values.JSONArray;
import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import com.gmail.subnokoii78.tplcore.json.values.JSONStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JSONPathNode<S extends JSONStructure, T, U extends JSONValue<?>> {
    protected final T parameter;

    protected final JSONValueType<U> returns;

    protected JSONPathNode(@NotNull T parameter, @NotNull JSONValueType<U> returns) {
        this.parameter = parameter;
        this.returns = returns;
    }

    public abstract @Nullable U get(@NotNull S structure);

    public static final class ObjectKeyNode<U extends JSONValue<?>> extends JSONPathNode<JSONObject, String, U> {
        private ObjectKeyNode(@NotNull String name, @NotNull JSONValueType<U> returns) {
            super(name, returns);
        }

        @Override
        public @Nullable U get(@NotNull JSONObject structure) {
            if (!structure.hasKey(parameter)) return null;
            else if (structure.getTypeOfKey(parameter) != returns) return null;
            else return structure.getKey(parameter, returns);
        }
    }

    public static class ArrayIndexNode<U extends JSONValue<?>> extends JSONPathNode<JSONArray, Integer, U> {
        protected ArrayIndexNode(@NotNull Integer index, @NotNull JSONValueType<U> returns) {
            super(index, returns);
        }

        @Override
        public @Nullable U get(@NotNull JSONArray structure) {
            if (!structure.has(parameter)) return null;
            else if (structure.getTypeAt(parameter) != returns) return null;
            else return structure.get(parameter, returns);
        }
    }

    public static class ObjectKeyCheckerNode extends JSONPathNode<JSONObject, TupleLR<String, JSONObject>, JSONObject> {
        protected ObjectKeyCheckerNode(@NotNull String name, @NotNull JSONObject jsonObject) {
            super(new TupleLR<>(name, jsonObject), JSONValueTypes.OBJECT);
        }

        @Override
        public @Nullable JSONObject get(@NotNull JSONObject structure) {
            if (!structure.hasKey(parameter.left())) return null;
            else if (structure.getTypeOfKey(parameter.left()) != returns) return null;
            else {
                final JSONObject value = structure.getKey(parameter.left(), returns);

                if (value instanceof JSONObject target) {
                    final JSONObject condition = parameter.right();
                    if (target.isSuperOf(condition)) {
                        return value;
                    }
                    else return null;
                }
                else return null;
            }
        }
    }

    public static class ArrayIndexFinderNode extends JSONPathNode<JSONArray, JSONObject, JSONObject> {
        protected ArrayIndexFinderNode(@NotNull JSONObject parameter) {
            super(parameter, JSONValueTypes.OBJECT);
        }

        @Override
        public @Nullable JSONObject get(@NotNull JSONArray structure) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != returns) {
                    continue;
                }

                final JSONObject element = structure.get(i, returns);

                if (element instanceof JSONObject object) {
                    if (object.isSuperOf(parameter)) {
                        return element; // definitely object
                    }
                    else return null;
                }
                else return null;
            }

            return null;
        }
    }
}
