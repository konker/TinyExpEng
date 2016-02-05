package com.luxvelocitas.tinyexpeng.runner.experiment;


public class SequentialSyncExperimentRunner extends BaseSyncExperimentRunner implements IExperimentRunner {
    @Override
    protected int[] initTaskGroupIndex(int numTasksGroupsToExecute) {
        int[] ret = new int[numTasksGroupsToExecute];

        // Initialize index to sequential order by default
        for (int i=0; i<ret.length; i++) {
            ret[i] = i;
        }

        return ret;
    }
}
