package com.luxvelocitas.tinyexpeng.runner;

import org.slf4j.Logger;

/**
 */
public abstract class AbstractRunner implements IRunner {
    protected static final int START_INDEX = -1;

    protected Logger mLogger;
    protected IRunnableItem mCurRunnableItem;
    protected int mCurrentIndexPos;
    protected int[] mIndex;
    protected int mNumToExecute;
    protected int mNumExecuted;
    protected int mOrder;
    protected int mTotal;

    protected AbstractRunner(Logger logger) {
        mLogger = logger;
    }

    @Override
    public void init(final IRunContext runContext, final IRunnableItem item) {
        mNumToExecute = item.size();
        mNumExecuted = 0;
        mCurrentIndexPos = START_INDEX;

        // Initialize the index, allow subclass to override this
        mIndex = initIndex(mNumToExecute);
    }

    @Override
    public void start(final IRunContext runContext, final IRunnableItem item) {
        start(runContext, item, 1, 1);
    }

    @Override
    public void start(final IRunContext runContext, final IRunnableItem item, int order, int total) {
        mCurRunnableItem = item;
        mOrder = order;
        mTotal = total;

        init(runContext, mCurRunnableItem);

        runContext.pushRunner(this);

        mCurRunnableItem.start(runContext, mOrder, mTotal);

        // Process the first child
        nextStep(runContext);
    }

    @Override
    public boolean hasStep() {
        return mNumExecuted < mNumToExecute;
    }

    @Override
    public void nextStep(IRunContext runContext) {
        if (runContext.isPaused()) {
            return;
        }

        // Get the
        if (hasStep()) {
            // Start the next child item
            mCurrentIndexPos = getNextIndexPos(mCurrentIndexPos, mNumExecuted);
            execute(runContext);
        }
        else {
            finalStep(runContext);
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

    @Override
    public void finalStep(IRunContext runContext) {
        deinit(runContext);
        mCurRunnableItem.end(runContext);

        runContext.popRunner();
    }
}
