package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.datautils.Util;


public class RandomOrderSyncExperimentRunner extends SequentialSyncExperimentRunner implements IExperimentRunner {
    @Override
    protected int[] initTaskGroupIndex(int numTaskGroupsToExecute) {
        // Initialize to default sequential index
        int[] ret = super.initTaskGroupIndex(numTaskGroupsToExecute);

        // Shuffle the index
        Util.shuffleIntArrayInPlace(ret);

        return ret;
    }
}
