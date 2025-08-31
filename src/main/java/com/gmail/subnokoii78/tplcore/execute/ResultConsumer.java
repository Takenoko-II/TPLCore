package com.gmail.subnokoii78.tplcore.execute;

import java.util.function.BiConsumer;

/**
 * {@link ResultCallback}にチェーンするときに使用する関数型インターフェース
 */
@FunctionalInterface
public interface ResultConsumer extends BiConsumer<CommandSourceStack, Integer> {}
