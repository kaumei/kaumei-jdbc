/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 *
 * SPDX-License-Identifier: Apache-2.0 AND EPL-2.0
 *
 * SPDX-FileComment: Portions of this file are derived from the JaCoCo project
 * and remain subject to the Eclipse Public License v2.0.
 */

package org.jacoco.core.internal.analysis.filter;

public final class Filters {

    private Filters() {
        // no instances
    }

    /**
     * Filter that does nothing.
     */
    public static final IFilter NONE = new FilterSet();

    /**
     * Creates a filter that combines all filters.
     * @return filter that combines all filters
     */
    public static IFilter all() {
        final IFilter allCommonFilters = allCommonFilters();
        final IFilter allKotlinFilters = allKotlinFilters();
        final IFilter allNonKotlinFilters = allNonKotlinFilters();
        return (methodNode, context, output) -> {
            allCommonFilters.filter(methodNode, context, output);
            if (isKotlinClass(context)) {
                allKotlinFilters.filter(methodNode, context, output);
            } else {
                allNonKotlinFilters.filter(methodNode, context, output);
            }
        };
    }

    private static IFilter allCommonFilters() {
        return new FilterSet( //
                new EnumFilter(), //
                new BridgeFilter(), //
                new SynchronizedFilter(), //
                new TryWithResourcesJavac11Filter(), //
                new TryWithResourcesJavacFilter(), //
                new TryWithResourcesEcjFilter(), //
                new FinallyFilter(), //
                new PrivateEmptyNoArgConstructorFilter(), //
                new AssertFilter(), //
                new StringSwitchJavacFilter(), //
                new StringSwitchFilter(), //
                new EnumEmptyConstructorFilter(), //
                new RecordsFilter(), //
                new ExhaustiveSwitchFilter(), //
                new RecordPatternFilter(), //
                new AnnotationGeneratedFilter(), //
                new IgnoreLinesFilter(),
                new ToStringFilter()
        );
    }

    private static IFilter allNonKotlinFilters() {
        return new FilterSet( //
                new SyntheticFilter());
    }

    private static IFilter allKotlinFilters() {
        return new FilterSet( //
                new KotlinGeneratedFilter(), //
                new KotlinSyntheticAccessorsFilter(), //
                new KotlinSerializableFilter(), //
                new KotlinEnumFilter(), //
                new KotlinJvmOverloadsFilter(), //
                new KotlinSafeCallOperatorFilter(), //
                new KotlinLateinitFilter(), //
                new KotlinWhenFilter(), //
                new KotlinWhenStringFilter(), //
                new KotlinUnsafeCastOperatorFilter(), //
                new KotlinNotNullOperatorFilter(), //
                new KotlinInlineClassFilter(), //
                new KotlinDefaultArgumentsFilter(), //
                new KotlinInlineFilter(), //
                new KotlinCoroutineFilter(), //
                new KotlinDefaultMethodsFilter(), //
                new KotlinComposeFilter());
    }

    /**
     * Checks whether the class corresponding to the given context has
     * <code>kotlin/Metadata</code> annotation.
     * @param context context information
     * @return <code>true</code> if the class corresponding to the given context
     * has <code>kotlin/Metadata</code> annotation
     */
    public static boolean isKotlinClass(final IFilterContext context) {
        return context.getClassAnnotations()
                .contains(KotlinGeneratedFilter.KOTLIN_METADATA_DESC);
    }

}
