package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public interface ISteppable {
    boolean hasStep();
    void nextStep(final ExperimentRunContext experimentRunContext);
    void finalStep(final ExperimentRunContext experimentRunContext);

    boolean isAutoStep();
    void setAutoStep(boolean autoStep);
}
