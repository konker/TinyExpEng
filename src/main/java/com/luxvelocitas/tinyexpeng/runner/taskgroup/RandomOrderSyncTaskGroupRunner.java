package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinydatautils.Util;
import org.slf4j.Logger;


public class RandomOrderSyncTaskGroupRunner extends SequentialSyncTaskGroupRunner implements ITaskGroupRunner {
    public RandomOrderSyncTaskGroupRunner(Logger logger) {
        super(logger);
    }

    @Override
    public int[] initIndex(int numToExecute) {
        // Initialize to default sequential index
        int[] ret = super.initIndex(numToExecute);

        // Shuffle the index
        Util.shuffleIntArrayInPlace(ret);

        return ret;
    }
}
