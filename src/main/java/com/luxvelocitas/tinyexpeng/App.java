package com.luxvelocitas.tinyexpeng;

import java.util.ArrayList;
import java.util.List;

import com.luxvelocitas.datautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.data.csv.CsvResultDataSink;
import com.luxvelocitas.tinyexpeng.data.csv.CsvSubjectDataSink;
import com.luxvelocitas.tinyexpeng.data.dummy.csv.DummyResultDataSink;
import com.luxvelocitas.tinyexpeng.data.dummy.csv.DummySubjectDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEventType;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.experiment.IExperimentRunner;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.*;
import com.luxvelocitas.tinyexpeng.runner.experiment.SequentialSyncExperimentRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {
    public static void main( String[] args ) {
        final Logger logger = LoggerFactory.getLogger(App.class);

        // Create an experiment
        Experiment experiment1 = new Experiment("Exp1");
        experiment1.setName("Experiment 1");
        experiment1.getMetadata().putFloat("FooF", 0.5f);

        // Create a TaskGroup
        TaskGroup taskGroup1 = new TaskGroup("tg-t1");
        taskGroup1.setName("Training Tasks");
        taskGroup1.getMetadata().putChar("BarC", 'K');

        // Create and add some tasks
        for (int i=0; i<5; i++) {
            Task t = new Task("t" + i);
            t.setName("Task " + i);
            t.getMetadata().putBoolean("QuxB", true);
            t.getDefinition().putInt("dummy_param", i);
            taskGroup1.add(t);
        }

        // Add the TaskGroup to the Experiment
        experiment1.add(taskGroup1);

        /*
        // Create a TaskGroup
        TaskGroup taskGroup2 = new TaskGroup("tg-r1");
        taskGroup2.setName("Real Tasks");

        // Create and add some tasks
        for (int i=0; i<6; i++) {
            Task t = new Task("r" + i);
            t.setName("Real Task " + i);
            t.getDefinition().putInt("dummy_param", i);
            taskGroup2.add(t);
        }

        // Add the TaskGroup to the Experiment
        experiment1.addTaskGroup(taskGroup2);

        // Create a TaskGroup
        TaskGroup taskGroup3 = new TaskGroup("tg-r2");
        taskGroup3.setName("Real Tasks 2");

        // Create and add some tasks
        for (int i=0; i<6; i++) {
            Task t = new Task("r" + i);
            t.setName("Real Task 2" + i);
            t.getDefinition().putInt("dummy_param", i);
            taskGroup3.add(t);
        }

        // Add the TaskGroup to the Experiment
        experiment1.addTaskGroup(taskGroup3);
*/
        
        // Add some event handlers to the experiment
        experiment1.addEventListener(ExperimentEventType.EXPERIMENT_START, new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                Experiment target = (Experiment)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("Experiment Start: " + target.getName());
            }
        });

        experiment1.addEventListener(ExperimentEventType.EXPERIMENT_END, new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                Experiment target = (Experiment)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("Experiment End: " + target.getName());
                //[TODO: print out result set]
            }
        });

        experiment1.addEventListener(ExperimentEventType.TASK_GROUP_START, new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                TaskGroup target = (TaskGroup)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("\tTaskGroup Start: " + target.getName());
            }
        });

        experiment1.addEventListener(ExperimentEventType.TASK_GROUP_END, new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                TaskGroup target = (TaskGroup)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("\tTaskGroup End: " + target.getName());
            }
        });

        experiment1.addEventListener(ExperimentEventType.TASK_START, new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                Task target = (Task)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                TaskGroup parent = (TaskGroup)tinyEvent.getEventData().get(Experiment.DATA_KEY_PARENT);
                ExperimentRunContext experimentRunContext =
                        (ExperimentRunContext)tinyEvent.getEventData().get("experimentRunContext");

                System.out.println("\t\tTask start(" + Thread.currentThread().getId() + "): " + target.getName());
                // HERE IS WHERE YOU WOULD ACTUAL PRESENT THE TASK, ETC
                // ...

                // create a dummy result
                Result result = new Result(parent, target);
                result.getData().putString("dummy_result", "FOO");

                // finish the task
                try {
                    experimentRunContext.addResult(result);

                    target.complete(experimentRunContext, parent);
                }
                catch (DataException ex) {
                    ex.printStackTrace();
                }
            }
        });

        experiment1.addEventListener(ExperimentEventType.TASK_END, new ITinyEventListener<ExperimentEventType, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEventType, DataBundle> tinyEvent) {
                Task target = (Task)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("\t\tTask end: " + target.getName());
            }
        });


        // -----------------------------------------------------------------------------
        // Set up an experiment runner
        List<ITaskGroupRunner> taskGroupRunners1 = new ArrayList<ITaskGroupRunner>();

        // A Task runner which runs each the task sequentially
        //taskGroupRunners1.add(new SequentialSyncTaskGroupRunner());
        //taskGroupRunners1.add(new RandomOrderSyncTaskGroupRunner());
        taskGroupRunners1.add(new StaggeredSequentialConcurrentTaskGroupRunner(1000));
        //taskGroupRunners1.add(new RandomOrderConcurrentTaskGroupRunner());
        //taskGroupRunners1.add(new SequentialConcurrentTaskGroupRunner());

        /*
        // A Task runner which runs tasks in a random order
        taskRunners1.add(new RandomOrderSyncTaskRunner());

        // A Task runner which runs each the task sequentially
        taskRunners1.add(new SequentialSyncTaskRunner());
*/
        
        // An Experiment runner which runs each task group sequentially
        IExperimentRunner experimentRunner1 = new SequentialSyncExperimentRunner();
        experimentRunner1.setTaskGroupRunners(taskGroupRunners1);

        /*
        // -----------------------------------------------------------------------------
        // Set up an experiment runner 2
        List<ITaskRunner> taskRunners2 = new ArrayList<ITaskRunner>();

        // A Task runner which runs each the task sequentially
        taskRunners2.add(new SequentialConcurrentTaskRunner());

        // A Task runner which runs tasks in a random order
        taskRunners2.add(new RandomOrderConcurrentTaskRunner());

        // A TaskGroup runner which runs each task group sequentially
        ITaskGroupRunner taskGroupRunner2 = new SequentialSyncTaskGroupRunner(taskRunners2);

        // A simple Experiment runner that starts the experiment and applies the task group runner
        IExperimentRunner experimentRunner2 = new SimpleExperimentRunner(taskGroupRunner2);
        */

        // Create an ExperimentRunContext
        Subject subject1 = new Subject("subject1");
        subject1.setName("Subject 1");
        subject1.getData().putString("foo", "bar1");
        String runId1 = "run1";
        ExperimentRunContext experimentRunContext1 = new ExperimentRunContext(logger, experiment1, subject1, runId1);

        /*
        Subject subject2 = new Subject("subject2");
        subject2.setName("Subject 2");
        subject2.getData().putString("foo", "bar2");
        String runId2 = "run2";
        ExperimentRunContext experimentRunContext2 = new ExperimentRunContext(experiment1, subject2, runId2);

        Subject subject3 = new Subject();
        subject3.setName("Subject 3");
        String runId3 = "run3";
        ExperimentRunContext experimentRunContext3 = new ExperimentRunContext(experiment1, subject3, runId3);
        */

        try {
            // Create a pair of data sinks
            IResultDataSink csvResultDataSink1 = new DummyResultDataSink();
            csvResultDataSink1.init("./", experimentRunContext1, experiment1);

            ISubjectDataSink csvSubjectDataSink1 = new DummySubjectDataSink();
            csvSubjectDataSink1.init("./", experimentRunContext1, experiment1);

            // Add a pair of data sinks to the run context
            experimentRunContext1.addResultDataSink(csvResultDataSink1);
            experimentRunContext1.addSubjectDataSink(csvSubjectDataSink1);

            // Write Subject data to the data sink
            experimentRunContext1.writeSubjectData();

            // Run the experiment
            experimentRunner1.start(logger, experimentRunContext1, experiment1);

            /*
            // Create a pair of data sinks
            IResultDataSink csvResultDataSink2 = new CsvResultDataSink("./");
            ISubjectDataSink csvSubjectDataSink2 = new CsvSubjectDataSink("./");

            // Add a data sink to the run context
            experimentRunContext2.addResultDataSink(csvResultDataSink2);
            experimentRunContext2.addSubjectDataSink(csvSubjectDataSink2);

            // Write Subject data to the data sink
            experimentRunContext2.writeSubjectData();

            // Try to run the experiment again
            experimentRunner1.start(experimentRunContext2);

            // Try to run the experiment again
            experimentRunner2.start(experimentRunContext3);

            // Try to run the experiment again (should fail)
            experimentRunner1.start(experimentRunContext2);
            */
        }
        catch (DataException ex1) {
            ex1.printStackTrace();
        }
        catch (StaleExperimentRunContextException ex) {
            ex.printStackTrace();
        }

        /*[XXX: needs further work]
        // Serialize Experiment 1
        ByteArrayStreamSerializationSink sink = new ByteArrayStreamSerializationSink();
        ISerializer serializer = new DummySerializer();

        serializer.init(sink);

        try {
            serializer.serialize(experiment1);
            System.out.println(new String(sink.toByteArray()));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        IVisitor visitor = new JsonVisitor();
        experiment1.accept(visitor, "Experiment");
        System.out.println(new String(visitor.getBytes()));
        */
    }
}
