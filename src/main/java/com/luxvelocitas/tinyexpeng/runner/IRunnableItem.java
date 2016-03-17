package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.tinyfsm.ITinyStateMachine;

/**
 */
public interface IRunnableItem {
    void start(IRunContext runContext);
    void end(IRunContext runContext);
    boolean isEnded();

    IRunnableItem addFsm(ITinyStateMachine stateMachine, Enum terminalState);
    boolean hasFsm();
    void triggerFsmEvent(IRunContext runContext, Enum eventType);
    Enum getFsmCurrentState();

    Enum getFsmTerminalState();
}
