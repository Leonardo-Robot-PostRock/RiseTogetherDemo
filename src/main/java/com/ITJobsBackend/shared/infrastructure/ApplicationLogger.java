package com.ITJobsBackend.shared.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplicationLogger {

    private ApplicationLogger() {}

    public static Logger forClass(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
