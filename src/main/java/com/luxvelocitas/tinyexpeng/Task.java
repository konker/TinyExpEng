package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;


public class Task extends AbstractRunnableItem implements IRunnableItem {
    protected DataBundle mDefinition;
    protected DataBundle mEventData;

    public Task() {
        setUuid();
        mDefinition = new DataBundle();
        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);
    }

    public DataBundle getDefinition() {
        return mDefinition;
    }

    @Override
    public void start(IRunContext runContext) {
        super.start(runContext);

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_START, mEventData);
    }

    @Override
    public void end(IRunContext runContext) {
        super.end(runContext);

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_END, mEventData);
    }
}
