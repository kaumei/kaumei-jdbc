/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 *
 * SPDX-License-Identifier: Apache-2.0 AND EPL-2.0
 *
 * SPDX-FileComment: Portions of this file are derived from the JaCoCo project
 * and remain subject to the Eclipse Public License v2.0.
 */

package org.jacoco.core.internal.analysis.filter;

import org.objectweb.asm.tree.MethodNode;

public class ToStringFilter implements IFilter {
    public void filter(MethodNode methodNode,
                       IFilterContext context,
                       IFilterOutput output) {
        if ("toString".equals(methodNode.name)) {
            output.ignore(methodNode.instructions.getFirst(), methodNode.instructions.getLast());
        }
    }
}
