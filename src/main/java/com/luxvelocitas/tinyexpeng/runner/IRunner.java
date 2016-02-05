package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public interface IRunner {
    /** Perform one execute tick */
    void execute(final ExperimentRunContext experimentRunContext);

    /** Initialize the index */
    int[] initIndex(int numToExecute);

    /** Set the next index value */
    int getNextIndexPos(int currentIndexPos, int numExecuted);

    /** Set the first index value */
    int getFirstIndexPos(int currentIndexPos);
}
