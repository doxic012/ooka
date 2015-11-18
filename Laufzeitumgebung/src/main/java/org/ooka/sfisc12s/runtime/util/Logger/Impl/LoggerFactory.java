package org.ooka.sfisc12s.runtime.util.Logger.Impl;

import org.ooka.sfisc12s.runtime.util.Logger.Logger;

/**
 * Created by steve on 16.11.15.
 */
public final class LoggerFactory {

    private LoggerFactory() {}

    public static Logger getRuntimeLogger(Class<?> logClass) {
        return new RuntimeLogger(logClass);
    }

    public static Logger getRuntimeLogger() {
        return new RuntimeLogger();
    }
}
