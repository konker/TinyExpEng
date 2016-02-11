package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;
import com.luxvelocitas.tinyfsm.ITinyStateMachine;

/**
 */
public abstract class AbstractRunnableItem extends MetadataObject implements IRunnableItem {
    protected ITinyStateMachine mStateMachine;
    protected Enum mTerminalState;
    protected boolean mEnded;

    @Override
    public void start(IRunContext runContext) {
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
    public void triggerFsmEvent(IRunContext runContext, Enum eventType) {
        mStateMachine.trigger(eventType);
        if (mStateMachine.getCurrentState().equals(mTerminalState)) {
            // End the Task
            end(runContext);
        }
    }

    @Override
    public Enum getCurrentFsmState() {
        return mStateMachine.getCurrentState();
    }
}
