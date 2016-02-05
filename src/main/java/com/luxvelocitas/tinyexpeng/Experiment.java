package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.datautils.MetadataObject;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.SimpleTinyEventDispatcher;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Experiment
 *
 * The fundamental representation of an experiment.
 * <p>
 * An Experiment is a container for one or more TaskGroups.
 * <p>
 * An application using an Experiment object can register listeners for
 * events which are triggered during the life cycle of the Experiment.
 * <p>
 * An Experiment is typically used in conjunction with an ExperimentRunner.
 *
 * @author Konrad Markus <konker@luxvelocitas.com>
 *
 */
public class Experiment extends MetadataObject {
    public static final String DATA_KEY_TARGET = "target";
    public static final String DATA_KEY_PARENT = "parent";

    protected List<TaskGroup> mTaskGroups;
    protected SimpleTinyEventDispatcher<ExperimentEventType, DataBundle> mEventDispatcher;

    protected boolean mStarted;
    protected DataBundle mEventData;

    public Experiment() {
        _init();
    }

    public Experiment(long id) {
        _init();
        setId(id);
    }

    public Experiment(String name) {
        _init();
        setName(name);
    }

    /**
     * Common initialization tasks
     */
    private void _init() {
        setUuid();
        mTaskGroups = new ArrayList<TaskGroup>();
        mEventDispatcher = new SimpleTinyEventDispatcher<ExperimentEventType, DataBundle>();

        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);

    }

    public void start(ExperimentRunContext experimentRunContext) throws StaleExperimentRunContextException {
        if (experimentRunContext.isEnded()) {
            throw new StaleExperimentRunContextException();
        }

        mStarted = true;

        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEventType.EXPERIMENT_START, mEventData);
    }

    public void complete(ExperimentRunContext experimentRunContext) {
        experimentRunContext.notifyRunContextEvent(ExperimentEventType.EXPERIMENT_END, mEventData);
    }

    public void add(TaskGroup taskGroup) {
        mTaskGroups.add(taskGroup);
    }

    public void remove(TaskGroup taskGroup) {
        mTaskGroups.remove(taskGroup);
    }

    public void clearTaskGroups() {
        mTaskGroups.clear();
    }

    public int size() {
        return mTaskGroups.size();
    }

    public boolean isEmpty() {
        return mTaskGroups.isEmpty();
    }

    public boolean contains(TaskGroup taskGroup) {
        return mTaskGroups.contains(taskGroup);
    }

    public int indexOf(TaskGroup taskGroup) {
        return mTaskGroups.indexOf(taskGroup);
    }

    public int lastIndexOf(TaskGroup taskGroup) {
        return mTaskGroups.lastIndexOf(taskGroup);
    }

    public TaskGroup get(int i) {
        return mTaskGroups.get(i);
    }

    public void set(int i, TaskGroup taskGroup) {
        mTaskGroups.set(i, taskGroup);
    }

    public Iterator<TaskGroup> taskGroupIterator() {
       return mTaskGroups.iterator();
    }

    public Experiment addResult(final ExperimentRunContext experimentRunContext, final Result result) throws DataException {
        // Add the result to the experiment result set
        experimentRunContext.addResult(result);

        return this;
    }

    public void addEventListener(ExperimentEventType eventType, ITinyEventListener<ExperimentEventType, DataBundle> eventListener) {
        mEventDispatcher.addListener(eventType, eventListener);
    }

    public void removeEventListener(ExperimentEventType eventType, ITinyEventListener<ExperimentEventType, DataBundle> eventListener) {
        mEventDispatcher.addListener(eventType, eventListener);
    }

    public void notifyEvent(ExperimentEventType eventType, DataBundle eventData) {
        mEventDispatcher.notify(eventType, eventData);
    }
}
