/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno.store;

import io.kaumei.jdbc.anno.JavaAnnoTypes;
import io.kaumei.jdbc.anno.JavaAnnoMessenger;
import io.kaumei.jdbc.anno.ProcessorEnvironment;
import io.kaumei.jdbc.anno.ProcessorSteps;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CompositeStore<T extends Converter> implements ProcessorSteps {
    // ----- services
    private final JavaAnnoMessenger logger;
    private final JavaAnnoTypes javaModelUtil;
    private final String name;
    private final Store<T> basic;
    private final Store<T> global;
    private final Search<T> globalSearch;
    private final TryToGenerate<T> fallback;
    // ----- state
    private final Map<Element, Search<T>> local = new HashMap<>();

    public CompositeStore(JavaAnnoMessenger logger, JavaAnnoTypes javaModelUtil, String name, Store<T> basic, Store<T> global, TryToGenerate<T> fallback) {
        this.logger = logger;
        this.javaModelUtil = javaModelUtil;
        this.name = name;
        this.basic = basic;
        this.global = global;
        this.fallback = fallback;
        this.globalSearch = new Search<>(logger, javaModelUtil, global, fallback);
    }

    // ------------------------------------------------------------------------

    @Override
    public void process(ProcessorEnvironment roundEnv) {
        for (var element : roundEnv.jdbcInterfaces()) {
            var store = this.global.createChildStore(name + ".local." + element.getQualifiedName());
            this.local.put(element, new Search<>(logger, javaModelUtil, store, fallback));
        }
    }

    // ------------------------------------------------------------------------

    public Store<T> getStoreForElement(Element element) {
        while (element != null && element.getKind() != ElementKind.PACKAGE) {
            var search = local.get(element);
            if (search != null) {
                return search.store();
            }
            element = element.getEnclosingElement();
        }
        return this.global;
    }

    public ConverterSearch<T> getSearchForElement(@Nullable Element element) {
        while (element != null && element.getKind() != ElementKind.PACKAGE) {
            var search = local.get(element);
            if (search != null) {
                return search;
            }
            element = element.getEnclosingElement();
        }
        return this.globalSearch;
    }

    // ------------------------------------------------------------------------

    public String csvStats() {
        var count = 0;
        for (var repo : local.values()) {
            count += repo.store().size();
        }
        //  basic, global, local
        return basic.size() + "," + global.size() + "," + count;
    }

    public void dump(StringBuilder out) {
        basic.dump(out);
        global.dump(out);
        var t = new TreeMap<String, Store<T>>();
        local.forEach((element, store) -> t.put(element.toString(), store.store()));
        for (var entry : t.entrySet()) {
            var store = entry.getValue();
            if (store.size() != 0) {
                store.dump(out);
            }
        }
    }

}
