package com.luxvelocitas.tinyexpeng.runner.taskgroup;

import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.TaskGroup;
import com.luxvelocitas.tinyexpeng.runner.ISteppable;
import org.slf4j.Logger;


/**
 * Run all the tasks in a group
 */
public interface ITaskGroupRunner extends ISteppable {
    void start(Logger logger, final ExperimentRunContext experimentRunContext, final TaskGroup taskGroup);
}
