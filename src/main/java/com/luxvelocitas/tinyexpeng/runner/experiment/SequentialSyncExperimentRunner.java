package com.luxvelocitas.tinyexpeng.runner.experiment;


public class SequentialSyncExperimentRunner extends BaseSyncExperimentRunner implements IExperimentRunner {
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
