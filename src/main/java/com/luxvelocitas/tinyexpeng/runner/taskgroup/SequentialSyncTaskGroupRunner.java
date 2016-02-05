package com.luxvelocitas.tinyexpeng.runner.taskgroup;



public class SequentialSyncTaskGroupRunner extends BaseSyncTaskGroupRunner implements ITaskGroupRunner {
    @Override
    public int[] initIndex(int numToExecute) {
        int[] ret = new int[numToExecute];

        // Initialize index to sequential order by default
        for (int i=0; i< numToExecute; i++) {
            ret[i] = i;
        }

        return ret;
    }
}
