package com.luxvelocitas.tinyexpeng.runner;

import org.slf4j.Logger;

/**
 */
public abstract class AbstractRunner implements IRunner, ISteppable {
    protected static final int START_INDEX = -1;

    protected Logger mLogger;
    protected int mCurrentIndexPos;
    protected int[] mIndex;
    protected int mNumToExecute;
    protected int mNumExecuted;
    protected boolean mAutoStep;

    @Override
    public boolean isAutoStep() {
        return mAutoStep;
    }

    @Override
    public void setAutoStep(boolean autoStep) {
        mAutoStep = autoStep;
    }

    @Override
    public boolean hasStep() {
        return mNumExecuted < mNumToExecute;
    }

    @Override
    public void nextStep(ExperimentRunContext experimentRunContext) {
        // Get the
        if (hasStep()) {
            // Start the next task group
            mCurrentIndexPos = getNextIndexPos(mCurrentIndexPos, mNumExecuted);
            execute(experimentRunContext);

            if (isAutoStep()) {
                nextStep(experimentRunContext);
            }
        }
        else {
            finalStep(experimentRunContext);
        }
    }

    /** Set the next index value */
    @Override
    public int getNextIndexPos(int currentIndexPos, int numExecuted) {
        if (currentIndexPos == START_INDEX) {
            return getFirstIndexPos(currentIndexPos);
        }
        return currentIndexPos + 1;
    }

    /** Set the first index value */
    @Override
    public int getFirstIndexPos(int currentIndexPos) {
        return 0;
    }
}
