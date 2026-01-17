/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

public interface ProcessorSteps {
    void process(ProcessorEnvironment roundEnv);
}
