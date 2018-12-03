package com.danikula;

public final class LoggerFactory {

    public static Logger getLogger(String name) {
        Logger logger = new Logger();
        return logger;
    }

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = getLogger(clazz.getName());
        return logger;
    }

}
