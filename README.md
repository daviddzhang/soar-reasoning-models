# Soar Reasoning Models

This project contains Java implementations of various reasoning models. Along with the Java code for the models themselves, the project also contains [SML (Soar Markup Language)](https://soar.eecs.umich.edu/articles/articles/soar-markup-language-sml) code for output handlers for usage with Soar agents.

#### Supported Models

As of now, the current models of implemented and supported:

* Bayes Net
* Naive Bayes
* K-Nearest Neighbor

Users have the option to create, train, and query the model through each operation's respective output handler.

## Getting Started

These instructions will explain how to create and set up an agent to work with Soar Reasoning Models as well as how to run tests and the built in demos.

### Defining a Compatible Soar Agent

The agent's rules must have a specific structure in order for the output handlers to properly recognize parameters and information. The following section will be split into the three possible operations, with further specifications for each type of model if necessary. For further clarification, demo agents for each model and each operation are in the source resources package.

#### Rules for: Model Creation

General structure for a model-creating rule:

```
(<output-link> ^creating-output-handler-wme <wme>)
(<wme> ^model <model> ^name <name>)
(<model> ^<model-type> <type>)
(<type> ^parameters <p>)
...
```

The `parameters` attribute differs for each type of model. They are specified below.

##### Bayes Net

### Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags).

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone whose code was used
* Inspiration
* etc
