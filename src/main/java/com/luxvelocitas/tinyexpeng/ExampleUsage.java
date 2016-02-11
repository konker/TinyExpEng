package com.luxvelocitas.tinyexpeng;

import com.luxvelocitas.tinydatautils.DataBundle;
import com.luxvelocitas.tinyevent.ITinyEventListener;
import com.luxvelocitas.tinyevent.TinyEvent;
import com.luxvelocitas.tinyexpeng.data.DataException;
import com.luxvelocitas.tinyexpeng.data.IResultDataSink;
import com.luxvelocitas.tinyexpeng.data.ISubjectDataSink;
import com.luxvelocitas.tinyexpeng.data.csv.CsvResultDataSink;
import com.luxvelocitas.tinyexpeng.data.csv.CsvSubjectDataSink;
import com.luxvelocitas.tinyexpeng.event.ExperimentEvent;
import com.luxvelocitas.tinyexpeng.runner.ExperimentRunContext;
import com.luxvelocitas.tinyexpeng.runner.IRunContext;
import com.luxvelocitas.tinyexpeng.runner.experiment.FirstNThenRestRandomOrderSyncExperimentRunner;
import com.luxvelocitas.tinyexpeng.runner.experiment.IExperimentRunner;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.ITaskGroupRunner;
import com.luxvelocitas.tinyexpeng.runner.taskgroup.SequentialSyncTaskGroupRunner;
import com.luxvelocitas.tinyfsm.ITinyStateMachine;
import com.luxvelocitas.tinyfsm.TinyStateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ExampleUsage {
    // Task FSM states
    enum TaskState {
        IDLE, STARTED, STEP1, STEP2, ENDED
    };
    enum TaskEvent {
        STEP
    }

    public static void main( String[] args ) {
        final Logger logger = LoggerFactory.getLogger(ExampleUsage.class);

        // Create a Task FSM
        ITinyStateMachine<TaskState,TaskEvent,DataBundle> taskFsm =
                        new TinyStateMachine<TaskState,TaskEvent,DataBundle>(logger, TaskState.IDLE);
        taskFsm
           .transition(TaskState.IDLE,    TaskEvent.STEP, TaskState.STARTED)
           .transition(TaskState.STARTED, TaskEvent.STEP, TaskState.STEP1)
           .transition(TaskState.STEP1,   TaskEvent.STEP, TaskState.STEP2)
           .transition(TaskState.STEP2,   TaskEvent.STEP, TaskState.ENDED)
            ;
        taskFsm.setDebugMode(true);


        // Create an experiment
        Experiment experiment1 = new Experiment();
        experiment1.setName("Experiment 1");
        experiment1.getMetadata().putFloat("FooF", 0.5f);

        // Create a TaskGroup
        TaskGroup taskGroup1 = new TaskGroup();
        taskGroup1.setName("Training Tasks");
        taskGroup1.getMetadata().putChar("BarC", 'K');

        // Create and add some tasks
        for (int i=0; i<1; i++) {
            Task t = new Task();
            t.setName("Task " + i);
            t.getMetadata().putBoolean("QuxB", true);
            t.getDefinition().putInt("dummy_param", i);

            t.addFsm(taskFsm, TaskState.ENDED);

            taskGroup1.add(t);
        }

        // Add the TaskGroup to the Experiment
        experiment1.add(taskGroup1);

        /*[EG]
        */
        // Create a TaskGroup
        TaskGroup taskGroup2 = new TaskGroup();
        taskGroup2.setName("Real Tasks");

        // Create and add some tasks
        for (int i=0; i<6; i++) {
            Task t = new Task();
            t.setName("Real Task " + i);
            t.getDefinition().putInt("dummy_param", i);

            t.addFsm(taskFsm, TaskState.ENDED);

            taskGroup2.add(t);
        }

        // Add the TaskGroup to the Experiment
        experiment1.add(taskGroup2);

        // Create a TaskGroup
        TaskGroup taskGroup3 = new TaskGroup();
        taskGroup3.setName("Real Tasks 2");

        // Create and add some tasks
        for (int i=0; i<6; i++) {
            Task t = new Task();
            t.setName("Real Task 2" + i);
            t.getDefinition().putInt("dummy_param", i);

            t.addFsm(taskFsm, TaskState.ENDED);

            taskGroup3.add(t);
        }

        // Add the TaskGroup to the Experiment
        experiment1.add(taskGroup3);

        // Add some event handlers to the experiment
        experiment1.addEventListener(ExperimentEvent.EXPERIMENT_START, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                Experiment target = (Experiment)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("Experiment Start: " + target.getName());
            }
        });

        experiment1.addEventListener(ExperimentEvent.EXPERIMENT_END, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                Experiment target = (Experiment)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("Experiment End: " + target.getName());

                //[TODO: print out result set]
            }
        });

        experiment1.addEventListener(ExperimentEvent.TASK_GROUP_START, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                TaskGroup target = (TaskGroup)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("\tTaskGroup Start: " + target.getName());
            }
        });

        experiment1.addEventListener(ExperimentEvent.TASK_GROUP_END, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                TaskGroup target = (TaskGroup)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                System.out.println("\tTaskGroup End: " + target.getName());
            }
        });

        experiment1.addEventListener(ExperimentEvent.TASK_START, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                Task target = (Task)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                IRunContext runContext =
                        (IRunContext)tinyEvent.getEventData().get(Experiment.DATA_KEY_RUN_CONTEXT);
                System.out.println("\t\tTask start(" + Thread.currentThread().getId() + "): " + target.getName());
                // HERE IS WHERE YOU WOULD ACTUAL PRESENT THE TASK, ETC
                // ...

                // Then, user input, etc. triggers state change in the Task until it ends
                /*[EG]
                */
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("\t\t\tTask State: " + target.getCurrentFsmState());
                target.triggerFsmEvent(runContext, TaskEvent.STEP);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("\t\t\tTask State: " + target.getCurrentFsmState());
                target.triggerFsmEvent(runContext, TaskEvent.STEP);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("\t\t\tTask State: " + target.getCurrentFsmState());
                target.triggerFsmEvent(runContext, TaskEvent.STEP);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("\t\t\tTask State: " + target.getCurrentFsmState());
                target.triggerFsmEvent(runContext, TaskEvent.STEP);

                /*[EG]
                // OR, just end the Task manually
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                target.end(runContext);
                */
            }
        });

        experiment1.addEventListener(ExperimentEvent.TASK_END, new ITinyEventListener<ExperimentEvent, DataBundle>() {
            @Override
            public void receive(TinyEvent<ExperimentEvent, DataBundle> tinyEvent) {
                Task target = (Task)tinyEvent.getEventData().get(Experiment.DATA_KEY_TARGET);
                IRunContext runContext =
                        (IRunContext)tinyEvent.getEventData().get(Experiment.DATA_KEY_RUN_CONTEXT);

                List<Subject> subjects = runContext.getSubjects();

                System.out.println("\t\tTask end: " + target.getName());

                // create a dummy result
                Result result = new Result(subjects.get(0), null, target);
                result.getData().putString("dummy_result", "FOO");

                // Add the result to the result set
                try {
                    runContext.addResult(result);
                }
                catch (DataException ex) {
                    ex.printStackTrace();
                }
            }
        });


        // -----------------------------------------------------------------------------
        // Set up an experiment runner
        List<ITaskGroupRunner> taskGroupRunners1 = new ArrayList<ITaskGroupRunner>();

        // A Task runner which runs each the task sequentially
        taskGroupRunners1.add(new SequentialSyncTaskGroupRunner());
        taskGroupRunners1.add(new SequentialSyncTaskGroupRunner());
        taskGroupRunners1.add(new SequentialSyncTaskGroupRunner());
        //taskGroupRunners1.add(new RandomOrderSyncTaskGroupRunner());
        //taskGroupRunners1.add(new StaggeredSequentialConcurrentTaskGroupRunner(1000));
        //taskGroupRunners1.add(new RandomOrderConcurrentTaskGroupRunner());
        //taskGroupRunners1.add(new SequentialConcurrentTaskGroupRunner());

        // An Experiment runner which runs each task group sequentially
        /*[EG]
        IExperimentRunner experimentRunner1 =
                new SequentialSyncExperimentRunner();
        */
        /*[EG]
        */
        IExperimentRunner experimentRunner1 =
                new FirstNThenRestRandomOrderSyncExperimentRunner(1);
        experimentRunner1.setItemRunners(taskGroupRunners1);

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

        // Create an IRunContext
        String runId1 = "run1";
        IRunContext runContext1 = new ExperimentRunContext();
        runContext1.init(logger, experiment1, runId1);

        // Create a Subject
        Subject subject1 = new Subject("subject1");
        subject1.setName("Subject 1");
        subject1.getData().putString("foo", "bar1");
        /*
        Subject subject2 = new Subject("subject2");
        subject2.setName("Subject 2");
        subject2.getData().putString("foo", "bar2");
        String runId2 = "run2";
        IRunContext runContext2 = new ExperimentRunContext(experiment1, subject2, runId2);

        Subject subject3 = new Subject();
        subject3.setName("Subject 3");
        String runId3 = "run3";
        IRunContext runContext3 = new ExperimentRunContext(experiment1, subject3, runId3);
        */

        try {
            // Create a pair of data sinks
            IResultDataSink csvResultDataSink1 = new CsvResultDataSink();
            csvResultDataSink1.init("./", runContext1, experiment1);

            ISubjectDataSink csvSubjectDataSink1 = new CsvSubjectDataSink();
            csvSubjectDataSink1.init("./", runContext1, experiment1);

            // Add a pair of data sinks to the run context
            runContext1.addResultDataSink(csvResultDataSink1);
            runContext1.addSubjectDataSink(csvSubjectDataSink1);

            // Write Subject data to the data sink
            runContext1.addSubject(subject1);

            // Run the experiment
            experimentRunner1.start(runContext1, experiment1);

            /*
            // Create a pair of data sinks
            IResultDataSink csvResultDataSink2 = new CsvResultDataSink("./");
            ISubjectDataSink csvSubjectDataSink2 = new CsvSubjectDataSink("./");

            // Add a data sink to the run context
            runContext2.addResultDataSink(csvResultDataSink2);
            runContext2.addSubjectDataSink(csvSubjectDataSink2);

            // Write Subject data to the data sink
            runContext2.writeSubjectData();

            // Try to run the experiment again
            experimentRunner1.start(runContext2);

            // Try to run the experiment again
            experimentRunner2.start(runContext3);

            // Try to run the experiment again (should fail)
            experimentRunner1.start(runContext2);
            */

            // Close the data sinks
            runContext1.closeDataSinks();
        }
        catch (DataException ex1) {
            ex1.printStackTrace();
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
