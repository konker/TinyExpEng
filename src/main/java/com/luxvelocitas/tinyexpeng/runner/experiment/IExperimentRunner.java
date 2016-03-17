package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;

import java.util.List;


public interface IExperimentRunner extends IRunner {
    IExperimentRunner setTaskGroupRunners(List<ITaskGroupRunner> taskGroupRunners);
}
