package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.datautils.Util;


public class RandomOrderSyncExperimentRunner extends SequentialSyncExperimentRunner implements IExperimentRunner {
    @Override
    public int[] initIndex(int numToExecute) {
        // Initialize to default sequential index
        int[] ret = super.initIndex(numToExecute);

        // Shuffle the index
        Util.shuffleIntArrayInPlace(ret);

        return ret;
    }
}
