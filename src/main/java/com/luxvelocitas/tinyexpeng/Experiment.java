package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.datautils.MetadataObject;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.SimpleTinyEventDispatcher;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;

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
public class Experiment extends MetadataObject implements IRunnableItem {
    public static final String DATA_KEY_TARGET = "__target__";
    public static final String DATA_KEY_PARENT = "__parent__";
    public static final String DATA_KEY_CONTEXT = "__experimentRunContext__";

    protected List<TaskGroup> mTaskGroups;
    protected SimpleTinyEventDispatcher<ExperimentEvent, DataBundle> mEventDispatcher;

    protected DataBundle mEventData;
    protected boolean mEnded;

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

    @Override
    public void start(ExperimentRunContext experimentRunContext) {
        mEnded = false;

        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEvent.EXPERIMENT_START, mEventData);
    }

    @Override
    public void end(ExperimentRunContext experimentRunContext) {
        mEnded = true;

        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEvent.EXPERIMENT_END, mEventData);
    }

    @Override
    public boolean hasFsm() {
        return false;
    }

    @Override
    public boolean isEnded() {
        return mEnded;
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

    public void addEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDispatcher.addListener(eventType, eventListener);
    }

    public void removeEventListener(ExperimentEvent eventType, ITinyEventListener<ExperimentEvent, DataBundle> eventListener) {
        mEventDispatcher.addListener(eventType, eventListener);
    }

    public void notifyEvent(ExperimentEvent eventType, DataBundle eventData) {
        mEventDispatcher.notify(eventType, eventData);
    }

    /**
     * Common initialization tasks
     */
    private void _init() {
        setUuid();
        mTaskGroups = new ArrayList<TaskGroup>();
        mEventDispatcher = new SimpleTinyEventDispatcher<ExperimentEvent, DataBundle>();

        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);

    }
}
