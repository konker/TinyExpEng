package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public interface IRunnableItem {
    void start(IRunContext runContext);
    void end(IRunContext runContext);
    boolean isEnded();
    boolean hasFsm();
}
