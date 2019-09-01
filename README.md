# Soar Reasoning Models

This project contains Java implementations of various reasoning models. Along with the Java code for the models themselves, the project also contains [SML (Soar Markup Language)](https://soar.eecs.umich.edu/articles/articles/soar-markup-language-sml) code for output handlers for usage with Soar agents.

#### Supported Models

As of now, the current models are implemented and supported:

* Bayes Net (BN)
* Naive Bayes (NB)
* K-Nearest Neighbor (KNN)

Users have the option to create, train, and query the model through each operation's respective output handler. Since Naive Bayes and KNN are both flat-featured classifiers, they are often similar in rules and structure.

## Getting Started

These instructions will explain how to create and set up an agent to work with Soar Reasoning Models as well as how to run tests and the built in demos.

### Defining a Compatible Soar Agent

The agent's rules must have a specific structure in order for the output handlers to properly recognize parameters and information. The following section will be split into the three possible operations, with further specifications for each type of model if necessary. For further clarification, demo agents for each model and each operation are in the source resources package.

*Note: Boolean features are assigned values through floats/doubles, where 1.0 is true and 0.0 is false*

#### Rules for: Model Creation

General structure for a model-creating rule:

```
(<output-link> ^creating-output-handler-wme <wme>)
(<wme> ^model <model> ^name <name>)
(<model> ^<model-type> <type>)
(<type> ^parameters <p>)
...
```

**Upon creation of a model, the output handler will return an ID (aka the index of the model out of all the models created so far) on the output-handler WME. It is advisable to store this ID within the agent as it's required for training and querying.**  The `parameters` attribute differs for each type of model. They are specified below.

**Bayes Net**

The `parameters` attribute holds information about the net structure. Key identifiers are the `nodes` attribute and the `edge` attribute. These help to describe nodes and edges. The following is an example of how a net with the node A has an edge to the node B.

```
(<bayes-net> ^parameters <p>)
(<p> ^graph <g>)
(<g> ^nodes <n> ^edge <atob>)
(<n> ^A <a> ^B <b>)
(<atob> ^from <fromA> ^to <toB>)
(<fromA> ^A <a>)
(<toB> ^B <b>)
```

The identifiers that branch from `nodes` represent the names of the nodes, and each edge is split into a `from` and a `to` which should lead to the same node identifiers. With this format, the output handlers will be able to parse this information and construct a Bayes Net structure.

**Naive Bayes & KNN**

The `parameters` attribute holds information about the features that each classifier uses. Features can be of three types: numerical, categorical, and boolean. This information, along with the feature name must be supplied under parameters as follows:

```
(<a-classifier> ^parameters <p>)
(<p> ^features <f>)
(<f> ^feature1 <f1> ^feature2 <f2> ^feature3 <f3>)
(<f1> ^categorical <f1-vals>)
(<f1-vals> ^val1 <v1> ^val2 <v2> ^val3 <v3>)
(<f2> ^numerical <num>)
(<f3> ^boolean <bool>)
```
Additionally, the target feature/class must also be specified: *(Note: target class must be categorical, and the value for the WME must be the name of a valid feature)*

```
(<p> ^target feature1)
```

#### Rules for: Training

General structure for training rules:

```
(<output-link> ^training-output-handler-wme <wme>)
(<wme> ^id <id>)
(<wme> ^train <train>)
(<train> ^boolean <bool> ^numerical <num> ^categorical <cat>)
(<bool> ^Boolean-Feature-Name <bool-val>)
(<num> ^Numerical-Feature-Name <num-val>)
(<cat> ^Categorical-Feature-Name <cat-val>)
```

As evident, the agent should have some way of knowing the ID of the model that the user wants to train. To specify the features to train, the WME structure must specify the feature-type, then the name of the feature as a WME attribute whose value is the value of the feature.

#### Rules for: Querying

General structure for querying rules:

```
(<output-link> ^query-output-handler-wme <wme>)
(<wme> ^id <id>)
(<wme> ^query <q>)
(<q> ^boolean <bool> ^numerical <num>)
(<bool> ^Boolean-Feature-Name <bool-val>)
(<num> ^Numerical-Feature-Name <num-val>)
(<q> ^parameters <p>)
```

The query rule structure is very similar to that of training, with the removal of the target feature, and the addition of a parameters WME. Parameters will be different for each model, which will be specified below:

**Bayes Net**

The `parameters` attribute holds information about the target variable(s) in the query. The features shown in the general structure are the evidence variables. Target variables should be specified with the attribute name "target-vars." Below is an example for a query that asks the likelihood of B occurring given J and M:

```
(<ol> ^query-wme <qh>)
(<qh> ^id <id> ^query <q>)
(<q> ^features <f>)
(<f> ^boolean <j1> ^boolean <m1>)
(<j1> ^j <j>)
(<m1> ^m <m>)
(<q> ^parameters <p>)
(<p> ^target-vars <tv>)
(<tv> ^b 1.0)
```

**KNN**

The `parameters` attribute holds information about the K value and the distance function to use. The k value must be positive and the only supported distance function currently is euclidean. Below is an example of how to structure the parameters WME:

```
(<p> ^k 1)
(<p> ^distance euclidean)
```

**Naive Bayes**

The `parameters` attribute holds information about the smoothing value. The value must be a positive double/float. Below is an example of how to structure the parameters WME:

```
(<p> ^smoothing 1.0)
```

### Registering Your Agent to the Models

Everything your agent needs from this project can be found as static methods of the ReasoningModels class. It provides methods to register an agent to all the output handlers, saving and loading, and printing the current state of all the models so far. Check the documentation for ReasoningModels for more details.

### Tests

To run the test, use the Maven surefire plugin's `test` goal. In the Maven command line, enter `surefire:test@___` where the underline will either be `mac` for Mac or `windows` for Windows.

### Running the Demos

There are four demos in the project: three for each model, and one that combines all of them (to be referred to as the "main" demo).

#### Mac

To run the main demo, use the Maven exec plugin's exec goal. To run the other demos, set up your run configurations as follows:

In the VM arguments section, specify the following: `Djava.library.path=/path/to/this/project/src/main/resources`

#### Windows

To run any of the demos, set an environment variable in the run configuration:
* Name = `PATH`
* Value = `/path/to/this/project/src/main/resources`


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
