package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.MetadataObject;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.SimpleTinyEventDispatcher;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
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
    public static final String DATA_KEY_RUN_CONTEXT = "__runContext__";

    protected List<TaskGroup> mTaskGroups;
    protected SimpleTinyEventDispatcher<ExperimentEvent, DataBundle> mEventDispatcher;

    protected DataBundle mEventData;
    protected boolean mEnded;

    public Experiment() {
        setUuid();

        mTaskGroups = new ArrayList<TaskGroup>();
        mEventDispatcher = new SimpleTinyEventDispatcher<ExperimentEvent, DataBundle>();

        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);
    }

    @Override
    public void start(IRunContext runContext) {
        mEnded = false;

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.EXPERIMENT_START, mEventData);
    }

    @Override
    public void end(IRunContext runContext) {
        mEnded = true;

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.EXPERIMENT_END, mEventData);
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
}
