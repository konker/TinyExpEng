package com.luxvelocitas.tinyexpeng.data;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.Experiment;
import com.luxvelocitas.tinyexpeng.Subject;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;


public interface IEventLogDataSink {
    void init(String baseDir, IRunContext runContext, Experiment experiment) throws DataException;
    void writeEventLog(TinyEvent<ExperimentEvent, DataBundle> event) throws DataException;
    void close() throws DataException;
}
