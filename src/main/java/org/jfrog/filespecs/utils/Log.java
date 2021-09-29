package org.jfrog.filespecs.utils;

import java.io.Serializable;

/**
 * An interface that wraps a provided logger. Used to delegate logging to the runtime environment logger.
 *
 * @author Noam Y. Tenne
 */
public interface Log extends Serializable {
    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void error(String message, Throwable e);
}