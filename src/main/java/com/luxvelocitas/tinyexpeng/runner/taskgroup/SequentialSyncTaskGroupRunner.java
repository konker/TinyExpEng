package com.luxvelocitas.tinyexpeng.runner.taskgroup;



public class SequentialSyncTaskGroupRunner extends BaseSyncTaskGroupRunner implements ITaskGroupRunner {
    @Override
    protected int[] initTaskIndex(int numTasksToExecute) {
        int[] ret = new int[numTasksToExecute];

        // Initialize index to sequential order by default
        for (int i=0; i<numTasksToExecute; i++) {
            ret[i] = i;
        }

        return ret;
    }
}
