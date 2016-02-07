package com.luxvelocitas.tinyexpeng.runner;

/**
 */
public abstract class AbstractRunner implements IRunner, ISteppable {
    protected static final int START_INDEX = -1;

    protected int mCurrentIndexPos;
    protected int[] mIndex;
    protected int mNumToExecute;
    protected int mNumExecuted;
    protected boolean mAutoStep;

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

            if (_isAutoStep()) {
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

    protected boolean _isAutoStep() {
        return mAutoStep;
    }

    protected void _setAutoStep(boolean autoStep) {
        mAutoStep = autoStep;
    }
}
