package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;
import com.luxvelocitas.tinyfsm.ITinyStateMachine;


public class Task extends MetadataObject implements IRunnableItem {
    protected DataBundle mDefinition;
    protected DataBundle mEventData;
    protected ITinyStateMachine mStateMachine;
    protected Enum mTerminalState;
    protected boolean mEnded;

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
        if (mStateMachine != null) {
            mStateMachine.restart();
        }

        mEnded = false;

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

    public void triggerState(IRunContext runContext, Enum eventType) {
        mStateMachine.trigger(eventType);
        if (mStateMachine.getCurrentState().equals(mTerminalState)) {
            // End the Task
            end(runContext);
        }
    }

    public Enum getCurrentState() {
        return mStateMachine.getCurrentState();
    }
}
