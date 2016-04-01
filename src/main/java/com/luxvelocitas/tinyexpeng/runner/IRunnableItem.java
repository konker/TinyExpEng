package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.tinyfsm.ITinyStateMachine;

/**
 */
public interface IRunnableItem {
    void start(IRunContext runContext);
    void end(IRunContext runContext);
    boolean isEnded();

    IRunnableItem setFsm(ITinyStateMachine stateMachine, Enum terminalState);
    boolean hasFsm();
    IRunnableItem removeFsm();
    void triggerFsmEvent(IRunContext runContext, Enum eventType);
    void restartFsm(IRunContext runContext);
    Enum getFsmCurrentState();
    Enum getFsmTerminalState();

}
