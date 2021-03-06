package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunnableItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TaskGroup extends AbstractRunnableItem implements IRunnableItem {
    protected List<Task> mTasks;
    protected boolean mEnded;

    public TaskGroup() {
        super();

        mTasks = new ArrayList<Task>();
    }

    @Override
    public void start(IRunContext runContext, int order, int total) {
        super.start(runContext, order, total);

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_GROUP_START, mEventData);
    }

    @Override
    public void end(IRunContext runContext) {
        super.end(runContext);

        // Broadcast the event to the run context
        runContext.notifyRunContextEvent(ExperimentEvent.TASK_GROUP_END, mEventData);
    }

    @Override
    public int size() {
        return mTasks.size();
    }

    public void add(Task task) {
        mTasks.add(task);
    }

    public void remove(Task task) {
        mTasks.remove(task);
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
}
