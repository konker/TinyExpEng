package com.luxvelocitas.tinyexpeng.runner.experiment;


import org.slf4j.Logger;

public class SequentialSyncExperimentRunner extends AbstractExperimentRunner implements IExperimentRunner {
    public SequentialSyncExperimentRunner(Logger logger) {
        super(logger);
    }

    @Override
    public int[] initIndex(int numToExecute) {
        int[] ret = new int[numToExecute];

        // Initialize index to sequential order by default
        for (int i=0; i<ret.length; i++) {
            ret[i] = i;
        }

        return ret;
    }
}
