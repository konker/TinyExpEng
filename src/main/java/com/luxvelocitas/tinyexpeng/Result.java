package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.datautils.MetadataObject;

import java.util.Date;


public class Result extends MetadataObject {
    protected Subject mSubject;
    protected TaskGroup mTaskGroup;
    protected Task mTask;

    protected Date mTimestamp;
    protected DataBundle mData;

    public Result(Subject subject, TaskGroup taskGroup, Task task) {
        super();

        mSubject = subject;
        mTaskGroup = taskGroup;
        mTask = task;
        mTimestamp = new Date();

        mData = new DataBundle();
    }

    public Subject getSubject() {
        return mSubject;
    }

    public void setSubject(Subject subject) {
        mSubject = subject;
    }

    public TaskGroup getTaskGroup() {
        return mTaskGroup;
    }

    public void setTaskGroup(TaskGroup taskGroup) {
        mTaskGroup = taskGroup;
    }

    public Task getTask() {
        return mTask;
    }

    public void setTask(Task task) {
        mTask = task;
    }

    public Date getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Date timestamp) {
        mTimestamp = timestamp;
    }

    public DataBundle getData() {
        return mData;
    }
}
