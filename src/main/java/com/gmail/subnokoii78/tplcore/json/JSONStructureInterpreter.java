package com.gmail.subnokoii78.tplcore.json;

import com.gmail.subnokoii78.tplcore.execute.DimensionAccess;
import com.gmail.subnokoii78.tplcore.json.values.JSONArray;
import com.gmail.subnokoii78.tplcore.json.values.JSONNumber;
import com.gmail.subnokoii78.tplcore.json.values.JSONObject;
import com.gmail.subnokoii78.tplcore.json.values.TypedJSONArray;
import com.gmail.subnokoii78.tplcore.vector.DualAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.TripleAxisRotationBuilder;
import com.gmail.subnokoii78.tplcore.vector.Vector3Builder;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JSONStructureInterpreter<T> {
    private final Class<T> clazz;

    protected JSONStructureInterpreter(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract @Nullable T tryInterpret(@NotNull Object value);

    public boolean isConvertable(@NotNull Object value) {
        return tryInterpret(value) != null;
    }

    @NotNull
    public T interpret(@NotNull Object value) {
        final var v = tryInterpret(value);
        if (v == null) throw new IllegalArgumentException("渡された値は " + clazz.getName() + " に変換できません");
        else return v;
    }

    public static final JSONStructureInterpreter<Vector3Builder> VECTOR3 = new JSONStructureInterpreter<>(Vector3Builder.class) {
        private boolean isNumberKey(@NotNull JSONObject jsonObject, @NotNull String key) {
            if (jsonObject.hasKey(key)) {
                return jsonObject.getTypeOfKey(key).equals(JSONValueTypes.NUMBER);
            }
            else return false;
        }

        private @Nullable Vector3Builder object(@NotNull JSONObject jsonObject) {
            if (isNumberKey(jsonObject, "x") && isNumberKey(jsonObject, "y") && isNumberKey(jsonObject, "z")) {
                return new Vector3Builder(
                    jsonObject.getKey("x", JSONValueTypes.NUMBER).doubleValue(),
                    jsonObject.getKey("y", JSONValueTypes.NUMBER).doubleValue(),
                    jsonObject.getKey("z", JSONValueTypes.NUMBER).doubleValue()
                );
            }
            else return null;
        }

        private @Nullable Vector3Builder array(@NotNull JSONArray jsonArray) {
            if (jsonArray.length() == 3 && jsonArray.isArrayOf(JSONValueTypes.NUMBER)) {
                final TypedJSONArray<JSONNumber> typedJSONArray = jsonArray.typed(JSONValueTypes.NUMBER);
                return new Vector3Builder(
                    typedJSONArray.get(0).doubleValue(),
                    typedJSONArray.get(1).doubleValue(),
                    typedJSONArray.get(2).doubleValue()
                );
            }
            else return null;
        }

        @Override
        protected @Nullable Vector3Builder tryInterpret(@NotNull Object value) {
            return switch (value) {
                case JSONObject jsonObject -> object(jsonObject);
                case JSONArray jsonArray -> array(jsonArray);
                default -> null;
            };
        }
    };

    public static final JSONStructureInterpreter<DualAxisRotationBuilder> DUAL_AXIS_ROTATION = new JSONStructureInterpreter<>(DualAxisRotationBuilder.class) {
        private boolean isNumberKey(@NotNull JSONObject jsonObject, @NotNull String key) {
            if (jsonObject.hasKey(key)) {
                return jsonObject.getTypeOfKey(key).equals(JSONValueTypes.NUMBER);
            }
            else return false;
        }

        private @Nullable DualAxisRotationBuilder object(@NotNull JSONObject jsonObject) {
            if (isNumberKey(jsonObject, "yaw") && isNumberKey(jsonObject, "pitch")) {
                return new DualAxisRotationBuilder(
                    jsonObject.getKey("yaw", JSONValueTypes.NUMBER).floatValue(),
                    jsonObject.getKey("pitch", JSONValueTypes.NUMBER).floatValue()
                );
            }
            else return null;
        }

        private @Nullable DualAxisRotationBuilder array(@NotNull JSONArray jsonArray) {
            if (jsonArray.length() == 2 && jsonArray.isArrayOf(JSONValueTypes.NUMBER)) {
                final TypedJSONArray<JSONNumber> typedJSONArray = jsonArray.typed(JSONValueTypes.NUMBER);
                return new DualAxisRotationBuilder(
                    typedJSONArray.get(0).floatValue(),
                    typedJSONArray.get(1).floatValue()
                );
            }
            else return null;
        }

        @Override
        protected @Nullable DualAxisRotationBuilder tryInterpret(@NotNull Object value) {
            return switch (value) {
                case JSONObject jsonObject -> object(jsonObject);
                case JSONArray jsonArray -> array(jsonArray);
                default -> null;
            };
        }
    };

    public static final JSONStructureInterpreter<TripleAxisRotationBuilder> TRIPLE_AXIS_ROTATION = new JSONStructureInterpreter<>(TripleAxisRotationBuilder.class) {
        private boolean isNumberKey(@NotNull JSONObject jsonObject, @NotNull String key) {
            if (jsonObject.hasKey(key)) {
                return jsonObject.getTypeOfKey(key).equals(JSONValueTypes.NUMBER);
            }
            else return false;
        }

        private @Nullable TripleAxisRotationBuilder object(@NotNull JSONObject jsonObject) {
            if (isNumberKey(jsonObject, "yaw") && isNumberKey(jsonObject, "pitch") && isNumberKey(jsonObject, "roll")) {
                return new TripleAxisRotationBuilder(
                    jsonObject.getKey("yaw", JSONValueTypes.NUMBER).floatValue(),
                    jsonObject.getKey("pitch", JSONValueTypes.NUMBER).floatValue(),
                    jsonObject.getKey("roll", JSONValueTypes.NUMBER).floatValue()
                );
            }
            else return null;
        }

        private @Nullable TripleAxisRotationBuilder array(@NotNull JSONArray jsonArray) {
            if (jsonArray.length() == 3 && jsonArray.isArrayOf(JSONValueTypes.NUMBER)) {
                final TypedJSONArray<JSONNumber> typedJSONArray = jsonArray.typed(JSONValueTypes.NUMBER);
                return new TripleAxisRotationBuilder(
                    typedJSONArray.get(0).floatValue(),
                    typedJSONArray.get(1).floatValue(),
                    typedJSONArray.get(2).floatValue()
                );
            }
            else return null;
        }

        @Override
        protected @Nullable TripleAxisRotationBuilder tryInterpret(@NotNull Object value) {
            return switch (value) {
                case JSONObject jsonObject -> object(jsonObject);
                case JSONArray jsonArray -> array(jsonArray);
                default -> null;
            };
        }
    };

    public static final JSONStructureInterpreter<Location> LOCATION_BUKKIT = new JSONStructureInterpreter<>(Location.class) {
        @Override
        protected @Nullable Location tryInterpret(@NotNull Object value) {
            if (!(value instanceof JSONObject jsonObject)) return null;
            else if (!(jsonObject.hasKey("dimension") && jsonObject.hasKey("location") && jsonObject.hasKey("rotation"))) return null;
            else if (!(jsonObject.getTypeOfKey("dimension").equals(JSONValueTypes.STRING))) return null;

            final World dimension = DimensionAccess.of(jsonObject.getKey("dimension", JSONValueTypes.STRING).getValue()).getWorld();
            final Object location = jsonObject.getKey("location", jsonObject.getTypeOfKey("location"));
            final Object rotation = jsonObject.getKey("rotation", jsonObject.getTypeOfKey("rotation"));

            if (VECTOR3.isConvertable(location) && DUAL_AXIS_ROTATION.isConvertable(rotation)) {
                final Vector3Builder vec3 = VECTOR3.interpret(location);
                final DualAxisRotationBuilder rot = DUAL_AXIS_ROTATION.interpret(rotation);
                return new Location(
                    dimension,
                    vec3.x(), vec3.y(), vec3.z(),
                    rot.yaw(), rot.pitch()
                );
            }
            else return null;
        }
    };
}
