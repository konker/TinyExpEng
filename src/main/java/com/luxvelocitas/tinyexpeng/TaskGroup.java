package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.datautils.MetadataObject;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TaskGroup extends MetadataObject {
    protected List<Task> mTasks;
    protected DataBundle mEventData;

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

    private void _init() {
        setUuid();
        mTasks = new ArrayList<Task>();
        mEventData = new DataBundle();
        mEventData.put(Experiment.DATA_KEY_TARGET, this);
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

    public void start(ExperimentRunContext experimentRunContext) {
        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEventType.TASK_GROUP_START, mEventData);
    }

    public void complete(ExperimentRunContext experimentRunContext) {
        // Broadcast the event to the run context
        experimentRunContext.notifyRunContextEvent(ExperimentEventType.TASK_GROUP_END, mEventData);
    }

    public TaskGroup addResult(final ExperimentRunContext experimentRunContext, final Result result) throws DataException {
        // Add the result to the experiment result set
        experimentRunContext.addResult(result);

        return this;
    }
}
