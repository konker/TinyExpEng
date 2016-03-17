package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.*;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IEventLogDataSink;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import org.slf4j.Logger;

import java.util.List;


public interface IRunContext {
    IRunContext init(Logger logger, final Experiment experiment, final String runId);
    String getRunId();
    boolean isEnded();

    IRunContext addResultDataSink(IResultDataSink dataSink) throws DataException;
    void removeResultDataSink(IResultDataSink dataSink);

    IRunContext addSubjectDataSink(ISubjectDataSink dataSink) throws DataException;
    void removeSubjectDataSink(ISubjectDataSink dataSink);

    IRunContext addEventLogDataSink(IEventLogDataSink dataSink) throws DataException;
    void removeEventLogDataSink(IEventLogDataSink dataSink);

    IRunContext addEventLog(TinyEvent<ExperimentEvent, DataBundle> event) throws DataException;
    IRunContext addResult(Result result) throws DataException;
    IRunContext addSubject(Subject subject) throws DataException;
    List<Subject> getSubjects();

    IRunContext closeDataSinks() throws DataException;

    Experiment getExperiment();
    TaskGroup getCurrentTaskGroup();
    void setCurrentTaskGroup(TaskGroup taskGroup);
    Task getCurrentTask();
    void setCurrentTask(Task task);

    IRunContext addRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener);
    IRunContext removeRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener);

    IRunContext addRunContextPriorityEventListener(ITinyEventListener<ExperimentEvent, DataBundle> eventListener);
    IRunContext removeRunContextPriorityEventListener(ITinyEventListener<ExperimentEvent, DataBundle> eventListener);

    IRunContext notifyRunContextEvent(ExperimentEvent eventType, DataBundle eventData);
}
