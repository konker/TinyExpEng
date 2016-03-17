package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinydatautils.Util;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import org.slf4j.Logger;

/**
 */
public class FirstNThenRestRandomOrderSyncExperimentRunner extends AbstractExperimentRunner implements IRunner, IExperimentRunner {
    protected int mFirstN;

    public FirstNThenRestRandomOrderSyncExperimentRunner(Logger logger, int firstN) {
        super(logger);

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
