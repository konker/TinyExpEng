package com.luxvelocitas.tinyexpeng.runner.taskgroup;

/**
 */
public class SequentialConcurrentTaskGroupRunner extends BaseConcurrentTaskGroupRunner implements ITaskGroupRunner {
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
