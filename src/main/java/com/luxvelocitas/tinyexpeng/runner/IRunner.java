package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public interface IRunner {
    void init(final IRunContext runContext, final IRunnableItem item);
    void deinit(final IRunContext runContext);

    void start(final IRunContext runContext, final IRunnableItem item);

    /** Perform one execute tick */
    void execute(final IRunContext runContext);

    /** Initialize the index */
    int[] initIndex(int numToExecute);

    /** Set the next index value */
    int getNextIndexPos(int currentIndexPos, int numExecuted);

    /** Set the first index value */
    int getFirstIndexPos(int currentIndexPos);

    boolean hasStep();

    void nextStep(final IRunContext runContext);

    void finalStep(final IRunContext runContext);
}
