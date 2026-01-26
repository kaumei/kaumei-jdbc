/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc.anno;

import io.kaumei.jdbc.anno.annotool.Anno;
import io.kaumei.jdbc.annotation.config.JdbcLogLevel;
import org.jspecify.annotations.Nullable;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.tools.Diagnostic;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.kaumei.jdbc.anno.Processor.OPTION_KEY_LOG_LEVEL;

public class JavaAnnoMessenger {

    // ----- service
    private final Messager messager;
    private @Nullable JavaAnnoElements elements;

    // ----- state
    private JdbcLogLevel.LogLevel logState = JdbcLogLevel.LogLevel.ERROR;

    JavaAnnoMessenger(ProcessingEnvironment env) {
        this.messager = env.getMessager();
        this.logState = getLogLevel(env.getOptions());
    }

    void updateElements(JavaAnnoElements elements) {
        this.elements = elements;
    }

    // ------------------------------------------------------------------------

    public boolean isDebugEnabled() {
        return this.logState == JdbcLogLevel.LogLevel.DEBUG;
    }

    public <T extends Element> void acceptWithDebugFlag(T elem, Consumer<T> consumer) {
        var oldLoggerState = enableDebug(elem);
        try {
            consumer.accept(elem);
        } finally {
            this.logState = oldLoggerState;
        }
    }

    public <T extends Element, R> R applyWithDebugFlag(T elem, Function<T, R> func) {
        var oldLoggerState = enableDebug(elem);
        try {
            return func.apply(elem);
        } finally {
            this.logState = oldLoggerState;
        }
    }

    private JdbcLogLevel.LogLevel enableDebug(Element context) {
        var element = context;
        while (element != null && element.getKind() != ElementKind.PACKAGE) {
            if (Anno.JDBC_DEBUG.hasAnno(element)) {
                var old = logState;
                logState = JdbcLogLevel.LogLevel.DEBUG;
                return old;
            }
            element = element.getEnclosingElement();
        }
        return logState;
    }

    // ------------------------------------------------------------------------

    public void allways(CharSequence msg, Object... args) {
        this.messager.printMessage(Diagnostic.Kind.NOTE, format(msg, args));
    }

    // ------------------------------------------------------------------------

    /**
     * @param msg the message
     * @param args key value pairs
     */
    public void error(CharSequence msg, Object... args) {
        error(null, msg, args);
    }

    public void error(@Nullable Element e, CharSequence msg, Object... args) {
        this.messager.printError(format(msg, args), e);
    }

    public void error(Exception exp) {
        var expStr = formatException(exp);
        this.messager.printError(expStr, exp instanceof ProcessorException pe ? pe.element() : null);
    }

    // ------------------------------------------------------------------------

    public void warn(CharSequence msg, Object... args) {
        warn(null, msg, args);
    }

    public void warn(@Nullable Element e, CharSequence msg, Object... args) {
        if (JdbcLogLevel.LogLevel.WARN.isEnabled(logState)) {
            this.messager.printMessage(Diagnostic.Kind.WARNING, format(msg, args), e);
        }
    }

    // ------------------------------------------------------------------------

    //public void warnMandatory(CharSequence msg, Object... args) {
    //    if (LogState.WARN.isEnabled(logState)) {
    //            this.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, format(msg, args));
    //    }
    //}

    //public void warnMandatory(@Nullable Element e, CharSequence msg, Object... args) {
    //    if (LogState.WARN.isEnabled(logState)) {
    //            this.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, format(msg, args), e);
    //    }
    //}

    // ------------------------------------------------------------------------

    public void info(CharSequence msg, Object... args) {
        info(null, msg, args);
    }

    public void info(@Nullable Element e, CharSequence msg, Object... args) {
        if (JdbcLogLevel.LogLevel.INFO.isEnabled(logState)) {
            this.messager.printMessage(Diagnostic.Kind.NOTE, format(msg, args), e);
        }
    }

    // ------------------------------------------------------------------------

    public void debug(CharSequence msg, Object... args) {
        debug(null, msg, args);
    }

    public void debug(@Nullable Element e, CharSequence msg, Object... args) {
        if (JdbcLogLevel.LogLevel.DEBUG.isEnabled(logState)) {
            this.messager.printMessage(Diagnostic.Kind.NOTE, format(msg, args), e);
        }
    }

    // ------------------------------------------------------------------------

    public String formatException(Exception exception) {
        var sb = new StringBuilder();
        addExceptionTo(sb, exception);
        return sb.toString();
    }

    public String formatString(CharSequence msg, @Nullable Object... args) {
        return format(msg, args).toString();
    }

    public CharSequence format(CharSequence msg, @Nullable Object... args) {
        if (args.length == 0) {
            return msg;
        }
        var kv = new StringBuilder(msg);
        kv.append(' ');

        var sep = false;
        int i = 0;
        while (i + 1 < args.length) {
            addKeyValue(kv, sep, args[i], args[i + 1]);
            i += 2;
            sep = true;
        }

        if (i < args.length) {
            if (args[i] instanceof Exception e) {
                kv.append(": ");
                addExceptionTo(kv, e);
            } else if (args[i] != null) {
                addKeyValue(kv, sep, null, args[i]);
            }
        }
        return kv;
    }

    // ------------------------------------------------------------------------

    public JdbcLogLevel.LogLevel getLogLevel(Map<String, String> map) {
        var value = map.get(OPTION_KEY_LOG_LEVEL);
        if (value != null) {
            try {
                return JdbcLogLevel.LogLevel.valueOf(value.toUpperCase());
            } catch (Exception e) {
                this.warn("Found invalid log level. Use default ERROR.", "value", value);
            }
        }
        return JdbcLogLevel.LogLevel.ERROR;
    }

    private void addKeyValue(StringBuilder sb, boolean sep, @Nullable Object key, @Nullable Object value) {
        if (sep) {
            sb.append(", ");
        }
        if (key != null) {
            sb.append(key);
            sb.append('=');
        }
        addValueTo(sb, value);
    }

    private void addValueTo(StringBuilder sb, @Nullable Object value) {
        if (value instanceof Iterable<?> iterable) {
            boolean sep = false;
            sb.append('[');
            if (value instanceof Collection<?> c) {
                sb.append("size:");
                sb.append(c.size());
                sep = true;
            }
            for (Object item : iterable) {
                if (sep) {
                    sb.append(", ");
                } else {
                    sep = true;
                }
                if (item instanceof Element e) {
                    sb.append(e.getSimpleName());
                } else {
                    sb.append(item);
                }
            }
            sb.append(']');
        } else if (value instanceof Element e) {
            if (this.elements != null) {
                this.elements.addQualifiedName(sb, e, true);
            } else {
                sb.append(e.getSimpleName());
            }
        } else {
            sb.append(value);
        }
    }

    private void addExceptionTo(StringBuilder sb, Exception exception) {
        var list = exception.getStackTrace();
        int lastIndex = list.length - 1;
        while (lastIndex > 0 && !list[lastIndex - 1].getClassName().startsWith("io.kaumei")) {
            lastIndex--;
        }
        sb.append(exception);
        for (int i = 0; i <= lastIndex; i++) {
            sb.append("\n\tat ");
            sb.append(list[i]);
        }
        sb.append('\n');
    }

}
