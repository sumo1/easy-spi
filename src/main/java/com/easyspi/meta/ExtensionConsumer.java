package com.easyspi.meta;

import java.util.function.Consumer;

public interface ExtensionConsumer<T extends IBusinessExt> extends Consumer<T> {

    @Override
    void accept(T extension);

}
