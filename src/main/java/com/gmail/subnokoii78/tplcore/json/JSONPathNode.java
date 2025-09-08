package com.gmail.subnokoii78.tplcore.json;

import com.gmail.subnokoii78.tplcore.generic.TupleLR;
import com.gmail.subnokoii78.tplcore.json.values.JSONArray;
import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JSONPathNode<S, T> {
    protected final T parameter;

    protected JSONPathNode(@NotNull T parameter) {
        this.parameter = parameter;
    }

    public abstract <U extends JSONValue<?>> @Nullable U get(@NotNull S structure, @NotNull JSONValueType<U> type);

    public static final class ObjectKeyNode extends JSONPathNode<JSONObject, String> {
        private ObjectKeyNode(@NotNull String name) {
            super(name);
        }

        @Override
        public <U extends JSONValue<?>> @Nullable U get(@NotNull JSONObject structure, @NotNull JSONValueType<U> type) {
            if (structure.hasKey(parameter)) {
                return structure.getKey(parameter, type);
            }
            else return null;
        }
    }

    public static class ArrayIndexNode extends JSONPathNode<JSONArray, Integer> {
        protected ArrayIndexNode(@NotNull Integer index) {
            super(index);
        }

        @Override
        public <U extends JSONValue<?>> @Nullable U get(@NotNull JSONArray structure, @NotNull JSONValueType<U> type) {
            if (structure.has(parameter)) {
                return structure.get(parameter, type);
            }
            else return null;
        }
    }

    public static class ObjectKeyCheckerNode extends JSONPathNode<JSONObject, TupleLR<String, JSONObject>> {
        protected ObjectKeyCheckerNode(@NotNull String name, @NotNull JSONObject jsonObject) {
            super(new TupleLR<>(name, jsonObject));
        }

        @Override
        public <U extends JSONValue<?>> @Nullable U get(@NotNull JSONObject structure, @NotNull JSONValueType<U> type) {
            if (structure.hasKey(parameter.left())) {
                final U value = structure.getKey(parameter.left(), type);

                if (value instanceof JSONObject target) {
                    final JSONObject condition = parameter.right();
                    if (target.isSuperOf(condition)) {
                        return value;
                    }
                    else return null;
                }
                else return null;
            }
            else return null;
        }
    }

    public static class ArrayIndexFinderNode extends JSONPathNode<JSONArray, JSONObject> {
        protected ArrayIndexFinderNode(@NotNull JSONObject parameter) {
            super(parameter);
        }

        @Override
        public <U extends JSONValue<?>> @Nullable U get(@NotNull JSONArray structure, @NotNull JSONValueType<U> type) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != type) {
                    continue;
                }

                final U element = structure.get(i, type);

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
