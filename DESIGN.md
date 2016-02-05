TinyExpEng Design
==============================================================================

Experiment/Field Test Framework in Java

- Provide a standardized (Java) framework for running experiments or field tests
- Should be flexible to cater for different requirements and scenarios
- Should be extensible for custom logging and metadata requirements


## TODO
- DOCS!
    - Javadocs

- TESTS!
    - Look into best way of doing this
    - Unit tests
        - DataBundle
        - Other classes
    - Integration tests
        - For running combinations of classes
- Name
    - IS TinyExpEng a good name?
    - ExpEng
        - This is better?
        - Also means a bunch of other things
    - ExperimentEngine
        - In use by http://www.experimentengine.com/
    - ExpEngine
        - Kind of in use by http://exp-engine.sourceforge.net
    - EEngine
    - EEng
    - Experimentor

- Logo
    - Gay, I know.

## Project
- An ExpEng "Project" is a container for:
    - 1 or more Experiment objects
        - (these obviously have TaskGroup(s) and Task(s))
        - Does it make sense for a Project to have 0 Experiment objects?

    - 0 or more Subject objects
        - Need to be able to serialize these

    - 0 or more ExperimentRunContext objects?
        - Are these persistent?

    - 0 or more data files?
        - data files can be stored in the project directory
            - viewable as list inside project viewer?

    - 0 or more data sinks?
        - Just need the types here?

    - 0 or more Results?
        - Are these persistent?
            - NO (at the moment at least)
                - Considered ephemeral and must be hadled by result sink

    - TaskRunner objects?
        - Just need the types here?

    - TaskGrouRunner objects?
        - Just need the types here?
        - And associations with TaskRunner objects?

    - ExperimentRunner objects?
        - Just need the types here?
        - And associations with TaskGroupRunner objects?

- A Project is serialized as a directory with a manifest
    - The manifest if the top level description of the child components of the project
    - e.g. An Experiment
        - This is in turn serialized in it's own file in the directory

- File format for project manifest, etc?
    - JSON?
        - Very cross platform, especially with web

    - YAML
        - Allows things such as comments, richer types
        - More complicated?

    - XML
        - The old warhorse
        - Has SAX and DOM parsers
        - Probably ok if the files are mostly machine written/read


## Notes
https://facebook.github.io/planout/
- UniformChoice(choices=['foo', 'bar'])

- Should we be looking at something like combinatronics rather than reinventing it?
    - *LOOK INTO THIS*

- Is TinyExpEng backed by sqlite?
    - the design does seem to resemble a SQL schema
        - not sure if this is a good thing at this stage

- An experiment/tasks should be serializable in some way
    - XML?
    - JSON?
    - YAML?
    - and deserializable
    - serilaization format should allow for metadata, e.g. (x,y) coords of a visual representation
    - serilaization format should be protable, to e.g. web

- Do these various DataBundles need to have some kind of schema aspect to them?
    - something to enforce the integrity of items added to the list?
    - this would be a big overhead of course
    - could be an extension? ScheamDataBundle
    - what form would the schema be?
        - some kind of yaml description?
        - xml schema? schema-ng?
        - custom?
            - maybe not worth it, especially at first

- Event model
    - ExperimentStart
    - ExperimentEnd
    - BeforeTaskStart
    - TaskStart
    - BeforeTaskEnd
    - TaskEnd
    - TaskGroupStart
    - TaskGroupEnd
    - IterationStart
    - IterationEnd
    ...
    - ChooseNextTask
    - ChooseNextGroup


## Basics
- Experiment
    - Holds metadata and and one or more groups of tasks

- Task
    - Represents a unit of work within an experiment

- TaskList
    - A list of tasks to be performed

- TaskGroup
    - A logical group of tasks; e.g. phases, training mode, etc

- Subject
    - A subject performing the experiment

- Result
    - The result of performing a single task

- ResultSet
    - Represents a single run of the experiment by a subject

- DataSink
    - A sink for data, e.g. logging to a file


## Details
Experiment
    - int id
    - List<TaskGroup> taskGroups
    - List<ResultSet> resultSets
    - DataBundle metadata
        - String name
        - String location

Task
    - int id
    - DataBundle definition
        - String name
        - int number_to_show
        - String picture_to_show

TaskList
    - List<Task> tasks

TaskGroup
    - TaskList tasks
    - DataBundle metadata
        - String name

Subject
    - int id
    - DataBundle metadata
        - String name
        - int age, etc

Result
    - int id
    - Task task
    - TaskGroup group
    - Subject subject
    - Date timestamp
    - int iteration (?)
    - DataBundle taskContext
        - int previous_task_id (?)
        - int num_tasks_so_far (?)
        - ???
    - DataBundle data
        - (e.g. data gathered directly as a result of performing the task)
    - DataBundle extraData
        - (e.g. weather conditions when result was obtained)

ResultSet
    - int id
    - Subject subject
    - Date date // some kind of timestamp?
    - TaskGroup tasks
    - List<Result> results


## Events
- EXPERIMENT START
- EXPERIMENT END

- CHOOSE TASK GROUP
    - ideally the listener for this event would be able to affect the decision?
- TASK GROUP START
- TASK GROUP END

- CHOOSE TASK
    - ideally the listener for this event would be able to affect the decision?
- TASK START
- TASK END

- Should event listeners be able to affect the system?


## Iterations?
- Where should iterations fit into this?
- Should this be part of the core?
    - Or should it just be a runner?


## Operational
- e.g. 1
    - 2 TaskGroups, one for training, one for real
    - TaskGroups are presented in the order defined
    - Each TaskGroup has n > 1 Tasks
    - Tasks are performed in the order defined

- e.g. 2
    - 2 TaskGroups, one for training, one for real
    - TaskGroups are presented in the order defined
    - Each TaskGroup has n > 1 Tasks
    - Tasks are presented in a random order to each subject

- e.g. 3
    - 3 TaskGroups
    - TaskGroups are presented in a random order
    - Each TaskGroup has n > 1 Tasks
    - Tasks are presented in a random order to each subject

- e.g. 4
    - 3 TaskGroups, 1 for training
    - Training TaskGroup is presented first, then others are presented in a random order
    - Training tasks are presented in the order defined
    - Other Tasks are presented in a random order to each subject

- e.g. 5
    - 1 TaskGroup with 3 Tasks
    - Tasks are presented in sequential order
    - 9 Tasks are presented
        - Tasks are recycled in the order defined
            - 1,2,3,1,2,3,1,2,3
        - (meaning that the size of the TG is not the same as the number of Tasks to be performed)

- e.g. 6
    - 1 TaskGroup with 3 Tasks
    - Tasks are presented in a random order
    - 10 Tasks are presented
        - Tasks are recycled
            - Each cycle is in a random order within itself?
                - 3,2,1,1,2,3,2,3,1,2
            - Tasks are randomly chosen 10 times from the pool?
                - 3,2,1,3,3,2,2,1,1,2
            - Both should be possible
        - (meaning that the size of the TG is not the same as the number of Tasks to be performed)

- e.g. 7
    - 1 TaskGroup with 12 Tasks
    - 7 Tasks are chosen randomly

- other e.g.
    - Ordering of tasks is fixed, but not in the order defined?
    - The choice of the next task depends on the result of the previous task?

- What is the purpose of TaskGrourRunner?
    - To "choose" the order in which a TaskRunner is applied to the list of TaskGroups?
    - To "choose" a TaskRunner for each TaskGroup in the list?
    - To choose the order and which TaskRunner is applied to the list (i.e. both of hte above)?


## Runners
### Currently
    - ExperimentRunner
        - Has a TaskGroupRunner which it applies to the list of TGs

    - TaskGroupRunner
        - "runs" a TaskGroup
        - Has a TaskRunner which it applies to the list of Ts contained in a TG

    - TaskRuunner
        - runs a Task

### Should be? YES!
    - Move everything "up" one level
    - ExperimentRunner
        - chooses the order in which to run each TaskGroup in the Experiment

    - TaskGroupRunner
        - chooses the order in which to run each Task in the TaskGroup

## Data
- IDataSink
- CsvDataSink


## Serialization
- Need portable format for serializing an Experiment
    - e.g. to save and read from disk for usage
    - e.g. to allow for separate design tools
    - Android app will be a skeleton "experiment runner"
        - reads and executes a given experiment
        - Actual experiment will extend this

## Other
- Some way to save progress of a run in case of crash or something?
- A way to restart the run
    - proceed from where left off
    - start again from the begining
        - preserve original randomized order?


## Archived Notes

Result
    - Is taskId enough? really it should capture everything about
         the task and the context in which it was performed
            - keep a reference to the actual task object?
                - if a task can be shared by multiple task groups,
                  this is not enough!
                - should the task itself be self-contained in this sense?
                    - is a task actually like a class rather than an instance?
                        - i.e. do we need to make the distinction between a TaskTemplate and a Task?
                            - what is the actual distinction then?
                                - TaskTemplate defines the fundamental parameters of the task
                                - Task is an "instance" of a TaskTemplate, but with extra context data
                                        - instance means a reference to a TaskTemplate
                                        - extra context means:
                                            - TaskGroup?
                                            - Subject?
                                                - no, this is in Result
                                            - DataBundle of extra stuff?
                                                - (e.g. pre-condition which triggered task?, previous task?, ..)
                                - THEREFORE:
                                    - just put this extra context data into the Result!
                                    - no need for TaskTemplate

