package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public interface IRunnableItem {
    void start(ExperimentRunContext experimentRunContext);
    void end(ExperimentRunContext experimentRunContext);
    boolean isEnded();
    boolean hasFsm();
}
