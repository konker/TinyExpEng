package com.luxvelocitas.tinyexpeng.runner.taskgroup;


import org.slf4j.Logger;

public class SequentialSyncTaskGroupRunner extends AbstractTaskGroupRunner implements ITaskGroupRunner {
    public SequentialSyncTaskGroupRunner(Logger logger) {
        super(logger);
    }

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
