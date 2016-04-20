package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinydatautils.Util;
import com.luxvelocitas.tinyexpeng.runner.experiment.RandomOrderSyncExperimentRunner;
import org.slf4j.Logger;


public class ChooseMRandomOrderSyncTaskGroupRunner extends RandomOrderSyncTaskGroupRunner implements ITaskGroupRunner {
    protected final int mM;

    public ChooseMRandomOrderSyncTaskGroupRunner(Logger logger, int m) {
        super(logger);
        mM = m;
    }

    @Override
    public int[] initIndex(int numToExecute) {
        // Initialize to default sequential index
        int[] ret = super.initIndex(numToExecute);

        // Shuffle the index
        Util.shuffleIntArrayInPlace(ret);

        return ret;
    }

    @Override
    public boolean hasStep() {
        return mNumExecuted < mM;
    }
}
