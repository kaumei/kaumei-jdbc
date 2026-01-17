/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.engine.extension.ExtensionContextInternal;

import java.util.HashSet;

public class JdbcTestOnCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        var tags = new HashSet<DatasourceExtension.DBType>();
        for (var t : DatasourceExtension.DBType.values()) {
            if (context.getTags().contains(t.name())) {
                tags.add(t);
            }
        }
        if (tags.isEmpty()) {
            return ConditionEvaluationResult.enabled(null);
        }
        if (context instanceof ExtensionContextInternal ec) {
            for (var de : ec.getExtensions(DatasourceExtension.class)) {
                if (tags.contains(de.dbType())) {
                    return ConditionEvaluationResult.enabled(null);
                }
            }
        }
        return ConditionEvaluationResult.disabled(null);
    }

}