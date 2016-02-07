package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public interface ISteppable {
    boolean hasStep();
    void nextStep(final ExperimentRunContext experimentRunContext);
    void finalStep(final ExperimentRunContext experimentRunContext);

    /*[XXX: remove from public interface]
    boolean _isAutoStep();
    void _setAutoStep(boolean autoStep);
    */
}
