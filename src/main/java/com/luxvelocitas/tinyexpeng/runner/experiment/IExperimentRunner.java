package com.luxvelocitas.tinyexpeng.runner.experiment;

import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunner;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;

import java.util.List;


public interface IExperimentRunner extends IRunner {
    IExperimentRunner setItemRunners(List<ITaskGroupRunner> taskGroupRunners);

    void start(final ExperimentRunContext experimentRunContext, final Experiment experiment);
}
