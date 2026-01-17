/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 *
 * SPDX-License-Identifier: Apache-2.0 AND EPL-2.0
 *
 * SPDX-FileComment: Portions of this file are derived from the JaCoCo project
 * and remain subject to the Eclipse Public License v2.0.
 */

package org.jacoco.core.internal.analysis.filter;

import org.jacoco.report.ISourceFileLocator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Set;

public class IgnoreLinesFilter implements IFilter {

    public static ISourceFileLocator locator;

    public void filter(MethodNode methodNode, IFilterContext context, IFilterOutput output) {
        if (locator == null) {
            return;
        }
        var ignoreSet = getIgnoreLines(context);
        AbstractInsnNode lastNode = methodNode.instructions.getFirst();
        AbstractInsnNode startIgnore = null;
        for (AbstractInsnNode instruction : methodNode.instructions) {
            if (instruction instanceof LineNumberNode lineNumberNode) {
                var ignore = ignoreSet.contains(lineNumberNode.line);
                if (startIgnore == null && ignore) {
                    startIgnore = instruction;
                } else if (startIgnore != null && !ignore) {
                    output.ignore(startIgnore, lastNode);
                    startIgnore = null;
                }
            }
            lastNode = instruction;
        }
        if (startIgnore != null) {
            output.ignore(startIgnore, lastNode);
        }
    }

    private Set<Integer> ignoreLinesSet;

    private Set<Integer> getIgnoreLines(IFilterContext context) {
        if (this.ignoreLinesSet != null) {
            return this.ignoreLinesSet;
        }

        this.ignoreLinesSet = new HashSet<>();
        var hasErrors = false;
        var packageName = getPackageName(context);
        var fileName = context.getSourceFileName();
        try (var in = locator.getSourceFile(packageName, fileName)) {
            if (in == null) {
                System.out.println("Ignore source. Source could not be found: " + packageName + ", " + fileName);
                return this.ignoreLinesSet;
            }
            try (var reader = new BufferedReader(locator.getSourceFile(packageName, fileName))) {
                boolean ignoreLines = false;
                int nr = 0;
                var sourceLine = reader.readLine();
                while (sourceLine != null) {
                    nr++;
                    sourceLine = sourceLine.trim().toLowerCase();
                    if (sourceLine.endsWith("jacoco:on") || sourceLine.endsWith("sanity-check:off")) {
                        if (!ignoreLines) {
                            hasErrors = true;
                            System.out.println(packageName + "/" + fileName + "(" + nr + "): Missing jacoco:off or sanity-check:on");
                        }
                        ignoreLines = false;
                    } else if (sourceLine.endsWith("jacoco:off") || sourceLine.endsWith("sanity-check:on")) {
                        if (ignoreLines) {
                            hasErrors = true;
                            System.out.println(packageName + "/" + fileName + "(" + nr + "): Missing jacoco:on or sanity-check:off");
                        }
                        ignoreLines = true;
                    } else if (ignoreLines || sourceLine.endsWith("jacoco:no") || sourceLine.endsWith("sanity-check")) {
                        this.ignoreLinesSet.add(nr);
                    }
                    sourceLine = reader.readLine();
                }

                if (ignoreLines) {
                    hasErrors = true;
                    System.out.println(packageName + "/" + fileName + "(" + nr + "): Missing jacoco:off or sanity-check:on");
                }
            }
        } catch (Exception e) {
            System.out.println("Ignore source: " + e.getMessage());
        }

        if (hasErrors) {
            this.ignoreLinesSet.clear();
        }
        return this.ignoreLinesSet;
    }

    String getPackageName(IFilterContext context) {
        var i = context.getClassName().lastIndexOf('/');
        return context.getClassName().substring(0, i);
    }

}
