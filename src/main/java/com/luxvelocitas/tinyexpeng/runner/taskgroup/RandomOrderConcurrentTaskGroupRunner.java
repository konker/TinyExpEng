package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.datautils.Util;


/**
 */
public class RandomOrderConcurrentTaskGroupRunner extends SequentialConcurrentTaskGroupRunner implements ITaskGroupRunner {
    @Override
    public int[] initIndex(int numToExecute) {
        // Initialize to default sequential index
        int[] ret = super.initIndex(numToExecute);

        // Shuffle the index
        Util.shuffleIntArrayInPlace(ret);

        return ret;
    }
}
