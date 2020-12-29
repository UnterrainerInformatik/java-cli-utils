![GitHub forks](https://img.shields.io/github/forks/UnterrainerInformatik/java-cli-utils?style=social) ![GitHub stars](https://img.shields.io/github/stars/UnterrainerInformatik/java-cli-utils?style=social) ![GitHub repo size](https://img.shields.io/github/repo-size/UnterrainerInformatik/java-cli-utils) [![GitHub issues](https://img.shields.io/github/issues/UnterrainerInformatik/java-cli-utils)](https://github.com/UnterrainerInformatik/java-cli-utils/issues)

[![license](https://img.shields.io/github/license/unterrainerinformatik/FiniteStateMachine.svg?maxAge=2592000)](http://unlicense.org) [![Travis-build](https://travis-ci.org/UnterrainerInformatik/java-cli-utils.svg?branch=master)](https://travis-ci.org/github/UnterrainerInformatik/java-cli-utils) [![Maven Central](https://img.shields.io/maven-central/v/info.unterrainer.commons/cli-utils)](https://search.maven.org/artifact/info.unterrainer.commons/cli-utils) [![Twitter Follow](https://img.shields.io/twitter/follow/throbax.svg?style=social&label=Follow&maxAge=2592000)](https://twitter.com/throbax)



# java-cli-utils
A collection of useful tools if you want to make a command line interface of some sorts.

## Console-Progressbar

The console-progressbar is designed to show progress when writing long-running console-applications.  
It was designed to be used with consoles that support control-characters (like cmd) or that don't (Eclipse console implementation before Mars (4.5)).  

It comes with three flavors:

#### SimpleInsertBar

Works with non-control-character enabled consoles.
Uses a completely new line for adding progress-characters instead.

```java
prefix: [>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>]
         ##################################################
```

#### ProgressBar

Your ASCII-progressbar.
Uses the same line, requiring ASCII escape characters.

```java
prefix: [########------------------------------------------]
```

#### PercentGauge

Displays the percentage.
Needs ASCII escape characters as well.

```java
prefix: [ 72%]
```



You may extend the visual representations by implementing new graphical variants. 

### Example

```java
ConsoleProgressBar bar = ConsoleProgressBar.builder().maxValue((double)list.size()).
    controlCharacterSupport(!isForFileOut).build();

int count = 0;
for (String s : list) {
    doSomething(s);

    bar.updateValue(++count).redraw(System.out);
}

bar.complete().redraw(System.out);
System.out.println("\n");
```

## CliParser

This is a fluent wrapper over the Apache Commons CLI library.

### Usage

```java
Cli cli = CliParser
	.cliFor(args, "ServerBrowser", "a small tool to help validate some things")
    .addArg(Arg.String("server").shortName("s")
	.description("the server instance to connect to (http://<ip>.<port>)")
	.defaultValue(SERVER).optional())
	.addArg(Arg.String("user").shortName("u").description("the user to use when connecting to the server")
	.defaultValue(USER).optional())
	.addArg(Arg.String("password").shortName("p")
	.description("the password used when connecting to the server").defaultValue(PASSWORD)
	.optional())
	.addFlag(Flag.builder("list").shortName("l")
	.description("browses and lists all REST-API methods of this server instance"))
	.addMinRequired(1, "list").create();
if (cli.isHelpSet()) {
	System.exit(0);
}
String endpointUrl = cli.getArgValue("server");
String user = cli.getArgValue("user");
String password = cli.getArgValue("password");

if (cli.isFlagSet("list")) {
    // do something...
}
```

