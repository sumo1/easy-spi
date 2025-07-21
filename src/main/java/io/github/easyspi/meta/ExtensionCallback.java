package io.github.easyspi.meta;

import java.util.function.Function;

public interface ExtensionCallback<T extends IBusinessExt, R> extends Function<T, R> {

    @Override
    R apply(T extension);
}
