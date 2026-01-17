/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

import com.sun.source.tree.*;
import com.sun.source.util.JavacTask;
import com.sun.source.util.TreeScanner;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.assertj.core.util.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;

import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class KaumeiAssert {

    private KaumeiAssert() {
    }

    @CanIgnoreReturnValue
    public static KaumeiThrows kaumeiThrows(ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
        return new KaumeiThrows(shouldRaiseThrowable);
    }

    @CanIgnoreReturnValue
    public static KaumeiSource assertSource(Class<?> cls) {
        var name = Path.of(cls.getCanonicalName().replace('.', '/') + ".java");
        var units = parse(name);
        if(units == null) {
            throw new AssertionError("No such source file exists: " + name);
        }
        var iter = units.iterator();
        if(!iter.hasNext()) {
            throw new AssertionError("No such source file exists: " + name);
        }
        var unit = iter.next();
        if(iter.hasNext()) {
            throw new AssertionError("To many source file exists: " + name);
        }


        return new KaumeiSource(unit);
    }

    // ------------------------------------------------------------------------
    private static class KaumeiThrows0 {
        private final ThrowableAssert.ThrowingCallable shouldRaiseThrowable;
        private @Nullable AbstractThrowableAssert<?, ? extends Throwable> cache;

        private KaumeiThrows0(ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
            this.shouldRaiseThrowable = shouldRaiseThrowable;
        }

        protected AbstractThrowableAssert<?, ? extends Throwable> assertThatThrownBy() {
            if(cache == null) {
                cache = Assertions.assertThatThrownBy(shouldRaiseThrowable);
            }
            return cache;
        }
    }

    public static class KaumeiThrows extends KaumeiThrows0 {

        private KaumeiThrows(ThrowableAssert.ThrowingCallable shouldRaiseThrowable) {
            super(shouldRaiseThrowable);
        }

        @Deprecated
        public void throwsToDo(String patterns) {
            assertMessagePattern(Throwable.class, patterns);
        }

        public void noRows() {
            this.assertThatThrownBy()
                    .isExactlyInstanceOf(JdbcEmptyResultSetException.class);
        }

        public void toManyRows() {
            this.assertThatThrownBy()
                    .isExactlyInstanceOf(JdbcUnexpectedRowException.class);
        }


        public void npe(String regex) {
            assertMessagePattern(NullPointerException.class, regex);
        }

        public void jdbcException(String regex) {
            assertMessagePattern(JdbcException.class, regex);
        }

        public void sqlException() {
            this.assertThatThrownBy()
                    .isExactlyInstanceOf(JdbcException.class)
                    .cause()
                    .isInstanceOf(java.sql.SQLException.class);
        }

        public void mandatory() {
            this.assertThatThrownBy()
                    .isExactlyInstanceOf(java.lang.NullPointerException.class)
                    .hasMessage(null);
            //throwsWithPattern(java.lang.NullPointerException.class,null);
        }

        public void mandatory(String regex) {
            assertMessagePattern(java.lang.NullPointerException.class, regex);
        }

        public void illegalArgumentException(String regex) {
            assertMessagePattern(java.lang.IllegalArgumentException.class, regex);
        }

        public void annotationProcessError(String... regex) {
            assertMessagePattern(CodeGenerationException.class, regex);
        }

        // --------------------

        public void returnTypNotSupported(String type) {
            assertMessagePattern(CodeGenerationException.class,
                    "return type not supported. \\[type=" + type + "\\]");
        }
        public void returnTypNotSupported(String type,String reason) {
            assertMessagePattern(CodeGenerationException.class,
                    "return type not supported. \\[type=" + type + ", reason="+reason+"\\]");
        }

        public void returnNullnessNotSupported(String  given,String expected1, String expected2) {
            assertMessagePattern(CodeGenerationException.class,
                    "return type nullness '" + given + "' supported. Expected are one of '"+expected1+"' or '"+expected2+"'");
        }

        // --------------------

        public void unusedMethodAnnotations(Class<?>... regex) {
            var regexArray = new String[regex.length + 1];
            regexArray[0] = "Method has unused annotations";
            for (int i = 0; i < regex.length; i++) {
                regexArray[i + 1] = regex[i].getCanonicalName();
            }
            assertMessagePattern(CodeGenerationException.class, regexArray);
        }

        public KaumeiThrows unusedParameterAnnotations(String name, Class<?>... regex) {
            var regexArray = new String[regex.length + 1];
            regexArray[0] = "Parameter '" + name + "' has unused annotations";
            for (int i = 0; i < regex.length; i++) {
                regexArray[i + 1] = regex[i].getCanonicalName();
            }
            assertMessagePattern(CodeGenerationException.class, regexArray);
            return this;
        }


        public void paramInvalidConverter(String name, String type, String causeRegex) {
            assertMessagePattern(CodeGenerationException.class, "Converter invalid", name, type, causeRegex);
        }

        public void paramNoConverterFound(String name, String type) {
            assertMessagePattern(CodeGenerationException.class, name, type); // FIXME
        }

        public void paramOptionalTypeIsInvalid() {
            assertMessagePattern(CodeGenerationException.class,
                    "Optional<T> parameter type not supported",
                    "See JavaDoc of Optional",
                    "Optional is primarily intended for use as a method return"
            );
        }

        public void resultOptionalType() {
            assertMessagePattern(CodeGenerationException.class,
                    "Optional<");
        }

        public void resultColumnWasNullOnName(String name) {
            assertMessagePattern(NullPointerException.class,
                    "JDBC column was null on name", name);
        }

        public void resultColumnWasNullOnIndex(String name) {
            assertMessagePattern(NullPointerException.class,
                    "JDBC column was null on index", name);
        }

        public void invalidConverter(String nameOrType, String causeRegex) {
            assertMessagePattern(CodeGenerationException.class,
                    "Found invalid converter", nameOrType, causeRegex);
        }

        private void assertMessagePattern(Class<?> cls, String... patterns) {
            var msg = this.assertThatThrownBy()
                    .isExactlyInstanceOf(cls)
                    .message();
            for (var regex : patterns) {
                msg.containsPattern(Pattern.compile(regex));
            }
        }
    }

    // ------------------------------------------------------------------------

    private final static Path[] SEARCH_PATHS = new Path[]{
            Path.of("./src/main/java"),
            Path.of("./src/test/java"),
            Path.of("./target/generated-sources/annotations"),
            Path.of("./target/generated-test-sources/test/annotations")
    };

    static @Nullable Iterable<? extends CompilationUnitTree> parse(Path name) {
        for (var path : SEARCH_PATHS) {
            Path sourceFile = path.resolve(name);
            if(Files.exists(sourceFile)) {
                var compiler = ToolProvider.getSystemJavaCompiler();
                var fileManager = compiler.getStandardFileManager(null, null, null);
                var task = (JavacTask) compiler.getTask(
                        null, fileManager, null, null, null, fileManager.getJavaFileObjects(sourceFile));
                try {
                    return task.parse();
                } catch (IOException e) {
                    throw new UncheckedIOException(e.getMessage(), e);
                }
            }
        }
        return null;
    }

    public static class KaumeiSource {
        private final CompilationUnitTree unit;

        KaumeiSource(CompilationUnitTree unit) {
            this.unit = unit;
        }

        @CanIgnoreReturnValue
        public KaumeiClass hasClass(String className) {
            var names = new TreeSet<String>();
            for (var typeDecl : unit.getTypeDecls()) {
                if(typeDecl instanceof ClassTree ct) {
                    var name = ct.getSimpleName().toString();
                    if(name.equals(className)) {
                        return new KaumeiClass(ct);
                    }
                    names.add(name);

                    for (var m : ct.getMembers()) {
                        if(m instanceof ClassTree mt) {
                            var mName = mt.getSimpleName().toString();
                            if(mName.equals(className)) {
                                return new KaumeiClass(mt);
                            }
                            names.add(mName);
                        }
                    }
                }
            }
            throw new AssertionError("Class '" + className + "' not found in " + names);
        }
    }

    public static class KaumeiClass {
        private final ClassTree classTree;
        private final Set<String> annotations = new TreeSet<>();

        KaumeiClass(ClassTree classTree) {
            this.classTree = classTree;
            for (var annotation : classTree.getModifiers().getAnnotations()) {
                annotations.add(annotation.toString());
            }
        }

        @CanIgnoreReturnValue
        public KaumeiClass hasAnnotation(String given) {
            Assertions.assertThat(annotations).contains(given);
            return this;
        }

        public KaumeiClass hasAnnotation(Pattern given) {
            Assertions.assertThat(annotations)
                    .anySatisfy(s -> Assertions.assertThat(s).matches(given));
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod hasMethod(String methodName) {
            var names = new TreeSet<String>();
            for (Tree member : this.classTree.getMembers()) {
                if(member instanceof MethodTree mt) {
                    var name = mt.getName().toString();
                    names.add(name);
                    if(mt.getName().contentEquals(methodName)) {
                        return new KaumeiMethod(mt); // found the method
                    }
                }
            }
            throw new AssertionError("Method '" + methodName + "' not found in " + names);
        }

    }

    public static class KaumeiMethod {
        private final Set<String> calls = new TreeSet<>();
        private final Set<String> annotations = new TreeSet<>();
        private final String body;

        KaumeiMethod(MethodTree methodTree) {
            this.body = methodTree.toString();
            //this.methodTree = methodTree;
            for (var annotation : methodTree.getModifiers().getAnnotations()) {
                annotations.add(annotation.toString());
            }
            for (MethodInvocationTree call : MethodCallFinder.findCalls(methodTree)) {
                calls.add(call.toString().replaceAll(" ", ""));
            }
        }

        @CanIgnoreReturnValue
        public KaumeiMethod bodyContains(String given) {
            Assertions.assertThat(body).contains(given);
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod bodyDoesNotContain(String given) {
            Assertions.assertThat(body).doesNotContain(given);
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod hasAnnotation(String given) {
            Assertions.assertThat(annotations).contains(given);
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod hasAnnotations(String... given) {
            Assertions.assertThat(annotations).containsExactlyInAnyOrder(given);
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod doesNotHaveAnnotation(String given) {
            Assertions.assertThat(annotations).doesNotContain(given);
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod hasCall(String given) {
            Assertions.assertThat(calls).contains(given);
            return this;
        }

        @CanIgnoreReturnValue
        public KaumeiMethod hasCalls(String... given) {
            Assertions.assertThat(calls).containsExactly(given);
            return this;
        }
    }

    // ------------------------------------------------------------------------

    public static class MethodCallFinder extends TreeScanner<Void, Void> {
        public static List<MethodInvocationTree> findCalls(MethodTree method) {
            MethodCallFinder finder = new MethodCallFinder();
            if(method.getBody() != null) {
                method.getBody().accept(finder, null);
            }
            return finder.getCalls();
        }

        // --------------------------------------------------------------------

        private final List<MethodInvocationTree> calls = new ArrayList<>();

        @Override
        public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
            calls.add(node); // collect the call
            return super.visitMethodInvocation(node, p); // continue scanning
        }

        public List<MethodInvocationTree> getCalls() {
            return calls;
        }

    }

    // ------------------------------------------------------------------------

    public static <T> AssertKaumeiJdbcIterable<T> assertThat(JdbcIterable<T> current) {
        return new AssertKaumeiJdbcIterable<>(current);
    }

    public static class AssertKaumeiJdbcIterable<T> {
        private final JdbcIterable<T> current;

        public AssertKaumeiJdbcIterable(JdbcIterable<T> current) {
            this.current = current;
        }

        List<@Nullable T> toList() {
            var result = new ArrayList<@Nullable T>();
            for (T item : current) {
                result.add(item);
            }
            return result;
        }

        public void isEmpty() {
            try {
                Assertions.assertThat(toList()).isEmpty();
            } finally {
                current.close();
            }
        }

        @SafeVarargs
        public final void containsExactly(T... values) {
            try {
                Assertions.assertThat(toList()).containsExactly(values);
            } finally {
                current.close();
            }
        }
    }

    // ------------------------------------------------------------------------

    public static <T> AssertKaumeiJdbcResultSet<T> assertThat(JdbcResultSet<T> current) {
        return new AssertKaumeiJdbcResultSet<>(current);
    }

    public static class AssertKaumeiJdbcResultSet<T> {
        private final JdbcResultSet<T> current;

        public AssertKaumeiJdbcResultSet(JdbcResultSet<T> current) {
            this.current = current;
        }

        List<@Nullable T> toList() {
            var result = new ArrayList<@Nullable T>();
            while (current.next()) {
                result.add(current.getRowOpt());
            }
            return result;
        }

        public void isEmpty() {
            try {
                Assertions.assertThat(toList()).isEmpty();
            } finally {
                current.close();
            }
        }

        @SafeVarargs
        public final void containsExactly(T... values) {
            try {
                Assertions.assertThat(toList()).containsExactly(values);
            } finally {
                current.close();
            }
        }
    }

    // ------------------------------------------------------------------------

    public static <T> AssertKaumeiStream<T> assertThat(Stream<T> current) {
        return new AssertKaumeiStream<>(current);
    }

    public static class AssertKaumeiStream<T> {
        private final Stream<T> current;

        public AssertKaumeiStream(Stream<T> current) {
            this.current = current;
        }

        public void isEmpty() {
            try {
                Assertions.assertThat(current.toList()).isEmpty();
            } finally {
                current.close();
            }
        }

        public void hasSize(int expected) {
            try {
                Assertions.assertThat(current.toList()).hasSize(expected);
            } finally {
                current.close();
            }
        }

        @SafeVarargs
        public final void containsExactly(T... values) {
            try {
                Assertions.assertThat(current.toList()).containsExactly(values);
            } finally {
                current.close();
            }
        }
    }

    // ------------------------------------------------------------------------

}
