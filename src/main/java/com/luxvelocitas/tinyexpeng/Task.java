package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;


public class Task extends AbstractRunnableItem implements IRunnableItem {
    protected DataBundle mDefinition;

    public Task() {
        super();

        mDefinition = new DataBundle();
    }

    public DataBundle getDefinition() {
        return mDefinition;
    }

    @Override
    public void start(IRunContext runContext, int order, int total) {
        super.start(runContext, order, total);

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_START, mEventData);
    }

    @Override
    public void end(IRunContext runContext) {
        super.end(runContext);

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_END, mEventData);
    }

    @Override
    public int size() {
        // Task is not a composite object, so it's size defaults to 1
        return 1;
    }
}
