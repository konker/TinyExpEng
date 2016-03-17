package com.luxvelocitas.tinyexpeng.runner;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.SimpleTinyEventDispatcher;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.*;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IEventLogDataSink;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class ExperimentRunContext implements IRunContext {
    protected Logger mLogger;
    protected String mRunId;
    protected final SimpleTinyEventDispatcher<ExperimentEvent, DataBundle> mEventDistpatcher;
    protected final SimpleTinyEventDispatcher<ExperimentEvent, DataBundle> mPriorityEventDistpatcher;
    private Experiment mExperiment;
    private TaskGroup mCurrentTaskGroup;
    private Task mCurrentTask;

    protected final List<Subject> mSubjects;
    protected final Set<IResultDataSink> mResultDataSinks;
    protected final Set<ISubjectDataSink> mSubjectDataSinks;
    protected final Set<IEventLogDataSink> mEventLogDataSinks;
    private boolean mEnded;

    public ExperimentRunContext() {
        mEventDistpatcher = new SimpleTinyEventDispatcher<ExperimentEvent, DataBundle>();
        mPriorityEventDistpatcher = new SimpleTinyEventDispatcher<ExperimentEvent, DataBundle>();
        mResultDataSinks = new CopyOnWriteArraySet<IResultDataSink>();
        mSubjectDataSinks = new CopyOnWriteArraySet<ISubjectDataSink>();
        mEventLogDataSinks = new CopyOnWriteArraySet<IEventLogDataSink>();

        mSubjects = new ArrayList<Subject>();
    }

    @Override
    public IRunContext init(Logger logger, final Experiment experiment, final String runId) {
        mLogger = logger;
        mExperiment = experiment;
        mRunId = runId;
        mEnded = false;

        // Add a priority event listener to log all events
        addRunContextPriorityEventListener(new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
                // Write an entry to the event log for every event
                try {
                    ExperimentRunContext.this.addEventLog(event);
                }
                catch (DataException ex) {
                    mLogger.error("Error writing event log", ex);
                }
            }
        });

        // Add a listener for the start and end of the experiment run
        addRunContextEventListener(ExperimentEvent.EXPERIMENT_START, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
                _start();
            }
        });

        // Add a listener for the start and end of the experiment run
        addRunContextEventListener(ExperimentEvent.EXPERIMENT_END, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> event) {
                _end();
            }
        });

        return this;
    }

    @Override
    public String getRunId() {
        return mRunId;
    }

    @Override
    public boolean isEnded() {
        return mEnded;
    }

    @Override
    public IRunContext addResultDataSink(IResultDataSink dataSink) throws DataException {
        mResultDataSinks.add(dataSink);

        return this;
    }

    @Override
    public void removeResultDataSink(IResultDataSink dataSink) {
        mResultDataSinks.remove(dataSink);
    }

    @Override
    public IRunContext addSubjectDataSink(ISubjectDataSink dataSink) throws DataException {
        mSubjectDataSinks.add(dataSink);

        return this;
    }

    @Override
    public void removeSubjectDataSink(ISubjectDataSink dataSink) {
        mSubjectDataSinks.remove(dataSink);
    }

    @Override
    public IRunContext addEventLogDataSink(IEventLogDataSink dataSink) throws DataException {
        mEventLogDataSinks.add(dataSink);

        return this;
    }

    @Override
    public void removeEventLogDataSink(IEventLogDataSink dataSink) {
        mEventLogDataSinks.remove(dataSink);
    }

    @Override
    public IRunContext addSubject(Subject subject) throws DataException {
        // Add to the list of subjects for this run
        mSubjects.add(subject);

        // Also write it to any Subject data sinks
        for (ISubjectDataSink dataSink : mSubjectDataSinks) {
            dataSink.writeSubject(subject);
        }

        return this;
    }

    @Override
    public List<Subject> getSubjects() {
        return mSubjects;
    }

    @Override
    public IRunContext addEventLog(TinyEvent<ExperimentEvent, DataBundle> event) throws DataException {
        for (IEventLogDataSink dataSink : mEventLogDataSinks) {
            dataSink.writeEventLog(event);
        }
        return this;
    }

    @Override
    public IRunContext addResult(Result result) throws DataException {
        for (IResultDataSink dataSink : mResultDataSinks) {
            dataSink.writeResult(result);
        }

        return this;
    }

    @Override
    public IRunContext addRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDistpatcher.addListener(eventType, eventListener);

        return this;
    }

    @Override
    public IRunContext removeRunContextEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDistpatcher.removeListener(eventType, eventListener);

        return this;
    }

    @Override
    public IRunContext addRunContextPriorityEventListener(ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mPriorityEventDistpatcher.addStarListener(eventListener);
        return this;
    }

    @Override
    public IRunContext removeRunContextPriorityEventListener(ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mPriorityEventDistpatcher.removeStarListener(eventListener);
        return this;
    }

    @Override
    public IRunContext notifyRunContextEvent(ExperimentEvent eventType, DataBundle eventData) {
        // Dispatch to priority listeners first
        mPriorityEventDistpatcher.notify(eventType, eventData);

        // Also forward this event to the Experiment
        eventData.put(Experiment.DATA_KEY_RUN_CONTEXT, this);
        mExperiment.notifyEvent(eventType, eventData);

        // Dispatch to all our listeners
        mEventDistpatcher.notify(eventType, eventData);

        return this;
    }

    @Override
    public IRunContext closeDataSinks() throws DataException {
        // Close any result data sinks
        for (IResultDataSink dataSink : mResultDataSinks) {
            dataSink.close();
        }

        // Close any subject data sinks
        for (ISubjectDataSink dataSink : mSubjectDataSinks) {
            dataSink.close();
        }

        // Close any event log data sinks
        for (IEventLogDataSink dataSink : mEventLogDataSinks) {
            dataSink.close();
        }

        return this;
    }

    @Override
    public Experiment getExperiment() {
        return mExperiment;
    }

    @Override
    public TaskGroup getCurrentTaskGroup() {
        return mCurrentTaskGroup;
    }

    @Override
    public void setCurrentTaskGroup(TaskGroup taskGroup) {
        mCurrentTaskGroup = taskGroup;
    }

    @Override
    public Task getCurrentTask() {
        return mCurrentTask;
    }

    @Override
    public void setCurrentTask(Task task) {
        mCurrentTask = task;
    }

    protected void _start() {
        //[XXX: nothing at the moment]
    }

    protected void _end() {
        mEnded = true;
    }
}
