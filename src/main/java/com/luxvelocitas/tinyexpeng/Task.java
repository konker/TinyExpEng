package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;
import com.luxvelocitas.tinyfsm.ITinyStateMachine;


public class Task extends MetadataObject implements IRunnableItem {
    protected DataBundle mDefinition;
    protected DataBundle mEventData;
    protected ITinyStateMachine mStateMachine;
    protected Enum mTerminalState;
    protected boolean mEnded;

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

    public DataBundle getDefinition() {
        return mDefinition;
    }

    @Override
    public void start(ExperimentRunContext experimentRunContext) {
        if (mStateMachine != null) {
            mStateMachine.restart();
        }

        mEnded = false;

        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEvent.TASK_START, mEventData);
    }

    @Override
    public void end(ExperimentRunContext experimentRunContext) {
        mEnded = true;

        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEvent.TASK_END, mEventData);
    }

    @Override
    public boolean hasFsm() {
        return (mStateMachine != null);
    }

    @Override
    public boolean isEnded() {
        return mEnded;
    }

    public Task addStateMachine(ITinyStateMachine stateMachine, Enum terminalState) {
        mStateMachine = stateMachine;
        mTerminalState = terminalState;
        return this;
    }

    public void triggerState(ExperimentRunContext experimentRunContext, Enum eventType) {
        mStateMachine.trigger(eventType);
        if (mStateMachine.getCurrentState().equals(mTerminalState)) {
            // End the Task
            end(experimentRunContext);
        }
    }

    public Enum getCurrentState() {
        return mStateMachine.getCurrentState();
    }

    private void _init() {
        setUuid();
        mDefinition = new DataBundle();
        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);
    }

    /*[XXX: remove? ]
    public Task addResult(final ExperimentRunContext experimentRunContext, final Result result) throws DataException {
        // Add the result to the experiment result set
        experimentRunContext.addResult(result);

        return this;
    }
     */
}
