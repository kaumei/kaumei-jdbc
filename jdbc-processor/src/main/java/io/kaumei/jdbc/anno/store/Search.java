/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.JdbcTypeKind;
import io.kaumei.jdbc.anno.msg.Msg;

import java.util.LinkedHashSet;
import java.util.Set;

public class Search<T extends Converter> implements ConverterSearch<T> {

    private final JavaAnnoMessenger logger;
    private final JavaAnnoTypes javaModelUtil;
    private final Store<T> store;
    private final TryToGenerate<T> factory;

    // ----- state
    private final Set<SearchKey> inCycle = new LinkedHashSet<>();

    public Search(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, Store<T> store, TryToGenerate<T> factory) {
        this.logger = logger;
        this.javaModelUtil = javaModelUtil;
        this.store = store;
        this.factory = factory;
    }

    Store<T> store() {
        return store;
    }

    @Override
    public StoreResolve<T> resolve(SearchKey searchKey) {
        var analyseResult = this.javaModelUtil.analyseTypeMirror(searchKey.type());
        StoreResolve<T> storeResolve = switch (analyseResult.kind()) {
            case PRIMITIVE, OBJECT:
                yield new StoreResolve<>(analyseResult.kind(), searchKey, search(searchKey), null);
            case ARRAY: {
                var result = search(searchKey);
                if(result.hasMessages()) {
                    yield new StoreResolve<>(analyseResult.kind(), searchKey, null, analyseResult.component());
                } else {
                    yield new StoreResolve<>(JdbcTypeKind.OBJECT, searchKey, result, null);
                }
            }
            default:
                yield new StoreResolve<>(analyseResult.kind(), searchKey, null, analyseResult.component());
        };
        this.logger.debug("resolve", "searchKey", searchKey, "result", storeResolve);
        return storeResolve;
    }

    @Override
    public T search(SearchKey searchKey) {
        T entry = this.store.search(searchKey);
        if(entry == null) {
            if(searchKey.hasName()) {
                return this.store.put(searchKey, store.createInvalidConverter(Msg.notFound(searchKey)));
            }
            entry = tryToCreate(searchKey);
        }
        if(!entry.hasMessages() && !factory.checkType(searchKey.type(), entry)) {
            return store.createInvalidConverter(Msg.INCOMPATIBLE_TYPE);
        }
        return entry;
    }

    private T tryToCreate(SearchKey searchKey) {
        try {
            if(!this.inCycle.add(searchKey)) {
                return store.createInvalidConverter(Msg.cycle(searchKey, this.inCycle));
            }
            return factory.tryToCreate(searchKey);
        } finally {
            this.inCycle.remove(searchKey); // after processed, remove from cycle
        }
    }

}