/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 *
 * SPDX-License-Identifier: Apache-2.0 AND EPL-2.0
 *
 * SPDX-FileComment: Portions of this file are derived from the JaCoCo project
 * and remain subject to the Eclipse Public License v2.0.
 */

package io.kaumei.jdbc.jacoco;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.internal.analysis.filter.IgnoreLinesFilter;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.*;
import org.jacoco.report.csv.CSVFormatter;
import org.jacoco.report.html.HTMLFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportMain {

    static void main(String... args) throws Exception {
        new ReportMain(args).run();
    }

    private final String name = "Kaumei JDBC-" + LocalDate.now();
    private final Path root;
    private final Path html;
    private final Path csv;
    private final List<Path> exec;
    private final List<Path> classFolderOrFile;
    private final List<Path> sourceFolder;

    ReportMain(String... args) {
        this.root = args == null || args.length == 0
                ? Path.of(".").toAbsolutePath()
                : Path.of(args[0]);
        this.html = this.root.resolve("my_jacoco_html");
        this.csv = this.html.resolve("jacoco.csv");
        this.exec = List.of(
                this.root.resolve("jdbc-processor/target/jacoco-test.exec"),
                this.root.resolve("jdbc-processor-spec/target/jacoco.exec"),
                this.root.resolve("jdbc-processor-spec2/target/jacoco.exec")
        );
        this.classFolderOrFile = List.of(this.root.resolve("jdbc-processor/target/classes"));
        this.sourceFolder = List.of(this.root.resolve("jdbc-processor/src/main/java"));
    }

    void run() throws IOException {
        // ----- load exec files
        var loader = new ExecFileLoader();
        for (var execFile : exec) {
            if (Files.exists(execFile)) {
                System.out.println("load exec: " + execFile);
                try (var in = Files.newInputStream(execFile)) {
                    loader.load(in);
                }
            } else {
                System.out.println("Not found exec: " + execFile);
            }
        }

        // ----- define source locations
        var sourceLocator = new MultiSourceFileLocator(4);
        for (var path : sourceFolder) {
            System.out.println("source: " + path);
            sourceLocator.add(new DirectorySourceFileLocator(path.toFile(), "UTF-8", 4));
        }

        IgnoreLinesFilter.locator = sourceLocator;

        // ---- analyse class files
        var builder = new CoverageBuilder();
        var analyzer = new Analyzer(loader.getExecutionDataStore(), builder);
        for (var path : this.classFolderOrFile) {
            System.out.println("classes: " + path);
            analyzer.analyzeAll(path.toFile());
        }

        if (!builder.getNoMatchClasses().isEmpty()) {
            System.out.println(builder.getNoMatchClasses());
        }
        var bundle = builder.getBundle(name);

        System.out.println("Class......: " + bundle.getClassCounter());
        System.out.println("Method.....: " + bundle.getMethodCounter());
        System.out.println("Line.......: " + bundle.getLineCounter());
        System.out.println("Instruction: " + bundle.getInstructionCounter());
        System.out.println("Branch.....: " + bundle.getBranchCounter());
        System.out.println("Complexity.: " + bundle.getComplexityCounter());

        // ----- define output visitors
        var visitors = new ArrayList<IReportVisitor>();
        visitors.add(new CSVFormatter().createVisitor(new FileOutputStream(csv.toFile())));
        visitors.add(new HTMLFormatter().createVisitor(new FileMultiReportOutput(html.toFile())));
        var visitor = new MultiReportVisitor(visitors);

        visitor.visitInfo(loader.getSessionInfoStore().getInfos(), loader.getExecutionDataStore().getContents());
        visitor.visitBundle(bundle, sourceLocator);
        visitor.visitEnd();
    }

}
