package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinydatautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TaskGroup extends MetadataObject implements IRunnableItem {
    protected List<Task> mTasks;
    protected DataBundle mEventData;
    protected boolean mEnded;

    public TaskGroup() {
        _init();
    }

    public TaskGroup(long id) {
        _init();
        setId(id);
    }

    public TaskGroup(String name) {
        _init();
        setName(name);
    }

    @Override
    public void start(IRunContext runContext) {
        mEnded = false;

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_GROUP_START, mEventData);
    }

    @Override
    public void end(IRunContext runContext) {
        mEnded = true;

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_GROUP_END, mEventData);
    }

    @Override
    public boolean hasFsm() {
        return false;
    }

    @Override
    public boolean isEnded() {
        return mEnded;
    }

    public void add(Task task) {
        mTasks.add(task);
    }

    public void remove(Task task) {
        mTasks.remove(task);
    }

    public int size() {
        return mTasks.size();
    }

    public boolean isEmpty() {
        return mTasks.isEmpty();
    }

    public void clear() {
        mTasks.clear();
    }

    public boolean contains(Task task) {
        return mTasks.contains(task);
    }

    public int indexOf(Task task) {
        return mTasks.indexOf(task);
    }

    public int lastIndexOf(Task task) {
        return mTasks.lastIndexOf(task);
    }

    public Task get(int i) {
        return mTasks.get(i);
    }

    public void set(int i, Task task) {
        mTasks.set(i, task);
    }

    public Iterator<Task> taskIterator() {
       return mTasks.iterator();
    }

    private void _init() {
        setUuid();
        mTasks = new ArrayList<Task>();
        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);
    }
}
