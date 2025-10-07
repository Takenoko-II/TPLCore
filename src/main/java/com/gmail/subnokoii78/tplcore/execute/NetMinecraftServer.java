package com.gmail.subnokoii78.tplcore.execute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * NMS由来のコードを含む関数であることを示すアノテーション
 */
@Retention(RetentionPolicy.CLASS)
@Target({
    ElementType.METHOD
})
public @interface NetMinecraftServer {

}
