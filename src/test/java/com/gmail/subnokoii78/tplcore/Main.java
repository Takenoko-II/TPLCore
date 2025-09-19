package com.gmail.subnokoii78.tplcore;

import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONObject;

public class Main {
    public static void main(String[] args) {
        // TODO: ContainerInteraction for消す

        final JSONObject object = JSONParser.object("""
            {
                "foo": {
                    "bar": 0
                }
            }
            """);

        final JSONPath path = JSONPath.of("foo.bar");

        System.out.println(object.get(path, JSONValueTypes.OBJECT));
    }
}
