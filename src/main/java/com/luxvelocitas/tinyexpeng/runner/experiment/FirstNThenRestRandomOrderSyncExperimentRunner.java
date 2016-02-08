package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinydatautils.Util;
import com.luxvelocitas.tinyexpeng.runner.IRunner;

/**
 */
public class FirstNThenRestRandomOrderSyncExperimentRunner extends BaseSyncExperimentRunner implements IRunner, IExperimentRunner {
    protected int mFirstN;

    public FirstNThenRestRandomOrderSyncExperimentRunner(int firstN) {
        mFirstN = firstN;
    }

    @Override
    public int[] initIndex(int numToExecute) {
        int[] ret = new int[numToExecute];

        // Initialize index to sequential order by default
        for (int i=0; i<ret.length; i++) {
            ret[i] = i;
        }

        // Shuffle the index after offset-th item
        Util.shuffleIntSubArrayInPlace(ret, mFirstN);

        return ret;
    }
}
