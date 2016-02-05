package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.datautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;


public class Task extends MetadataObject {
    protected DataBundle mDefinition;
    protected DataBundle mEventData;

    public Task() {
        _init();
    }

    public Task(long id) {
        _init();
        setId(id);
    }

    public Task(String name) {
        _init();
        setName(name);
    }

    private void _init() {
        setUuid();
        mDefinition = new DataBundle();
        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);
    }

    public DataBundle getDefinition() {
        return mDefinition;
    }

    public void start(ExperimentRunContext experimentRunContext, TaskGroup taskGroup) {
        // Broadcast the event to the run context
        mEventData.put(Experiment.DATA_KEY_PARENT, taskGroup);
        experimentRunContext.notifyRunContextEvent(ExperimentEvent.TASK_START, mEventData);
    }

    public void complete(ExperimentRunContext experimentRunContext, TaskGroup taskGroup) {
        // Broadcast the event to the run context
        mEventData.put(Experiment.DATA_KEY_PARENT, taskGroup);
        experimentRunContext.notifyRunContextEvent(ExperimentEvent.TASK_END, mEventData);
    }

    public Task addResult(final ExperimentRunContext experimentRunContext, final Result result) throws DataException {
        // Add the result to the experiment result set
        experimentRunContext.addResult(result);

        return this;
    }
}
