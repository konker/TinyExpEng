package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.datautils.Util;


/**
 */
public class RandomOrderConcurrentTaskGroupRunner extends SequentialConcurrentTaskGroupRunner implements ITaskGroupRunner {
    @Override
    protected int[] initTaskIndex(int numTasksToExecute) {
        // Initialize to default sequential index
        int[] ret = super.initTaskIndex(numTasksToExecute);

        // Shuffle the index
        Util.shuffleIntArrayInPlace(ret);

        return ret;
    }
}
