package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;
import com.luxvelocitas.tinyfsm.ITinyStateMachine;

/**
 */
public abstract class AbstractRunnableItem extends MetadataObject implements IRunnableItem {
    public static final String KEY_DATA_FSM_EVENT_STATE = "fsm_state";

    protected ITinyStateMachine mStateMachine;
    protected Enum mTerminalState;
    protected boolean mEnded;
    protected DataBundle mEventData;

    @Override
    public void start(IRunContext runContext) {
        mEventData = new DataBundle();

        if (mStateMachine != null) {
            mStateMachine.restart();
        }

        mEnded = false;
    }

    @Override
    public void end(IRunContext runContext) {
        mEnded = true;
    }

    @Override
    public boolean isEnded() {
        return mEnded;
    }

    @Override
    public IRunnableItem addFsm(ITinyStateMachine stateMachine, Enum terminalState) {
        mStateMachine = stateMachine;
        mTerminalState = terminalState;
        return this;
    }

    @Override
    public boolean hasFsm() {
        return (mStateMachine != null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void triggerFsmEvent(IRunContext runContext, Enum eventType) {
        if (!hasFsm()) {
            throw new RuntimeException("Call to triggerFsmEvent without an Task FSM defined");
        }

        mStateMachine.trigger(eventType);

        // Trigger a event for the "internal" FSM state transition
        mEventData.put(KEY_DATA_FSM_EVENT_STATE, getFsmCurrentState());
        runContext.notifyRunContextEvent(ExperimentEvent.FSM_EVENT, mEventData);

        if (mStateMachine.getCurrentState().equals(mTerminalState)) {
            // End the Task
            end(runContext);
        }
    }

    @Override
    public Enum getFsmCurrentState() {
        return mStateMachine.getCurrentState();
    }

    @Override
    public Enum getFsmTerminalState() {
        return mTerminalState;
    }
}
