# Log4j Migration Plan: cli-utils

**Layer**: 2 — update after `datastructures`.

## Before Starting

Prompt the user for the following version numbers before making any changes:

| Variable | Question |
|----------|----------|
| `NEW_PARENT_POM_VERSION` | What is the new `parent-pom` version? |
| `NEW_DATASTRUCTURES_VERSION` | What is the new `datastructures` version? |
| `OWN_NEW_VERSION` | What version should `cli-utils` be bumped to? (currently `1.0.0`) |

## Context

Part of a migration from Log4j 1.x to Log4j 2.25.3 across all libraries. No `@Slf4j` usage — config file only.

> **IMPORTANT for execution**: This plan should be executed by actually making the file changes described below — create the new `log4j2.xml` / `log4j2-test.xml` files with the content provided, and delete the old `log4j.properties` files. Do not leave the config migration as a manual step.

## Current State

- **Artifact**: `info.unterrainer.commons:cli-utils`
- **Parent**: `parent-pom:1.0.1`
- **In-house dependencies**:
  - `datastructures:1.0.0` — bump to new version
- **log4j.properties**: YES (main + test)
- **@Slf4j usage**: none

### Current `log4j.properties` content:
```properties
log4j.rootLogger=DEBUG, A1
log4j.appender.A1=org.apache.log4j.ConsoleAppender
log4j.appender.A1.layout=org.apache.log4j.PatternLayout
log4j.appender.A1.layout.charset=UTF-8
log4j.appender.A1.layout.ConversionPattern=%-4r [%t] %-5p %c %x - %m%n
log4j.logger.io.netty=WARN
log4j.logger.org.eclipse.milo=WARN
```

## Steps

### 1. Update parent version in `pom.xml`

Change the parent version (line 8) to the new parent-pom version.

### 2. Update in-house dependency versions in `pom.xml`

```xml
<dependency>
    <groupId>info.unterrainer.commons</groupId>
    <artifactId>datastructures</artifactId>
    <version>NEW_DATASTRUCTURES_VERSION</version>
</dependency>
```

### 3. Bump own version

Increment `<version>` (line 12, currently `1.0.0`).

### 4. Create `src/main/resources/log4j2.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout charset="UTF-8"
                           pattern="%-4r [%t] %-5p %c %x - %m%n"/>
        </Console>
    </Appenders>
    <Loggers>
        <Logger name="io.netty" level="WARN"/>
        <Logger name="org.eclipse.milo" level="WARN"/>
        <Root level="DEBUG">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

### 5. Create `src/test/resources/log4j2-test.xml`

Same content as above.

### 6. Delete old config files

- Delete `src/main/resources/log4j.properties`
- Delete `src/test/resources/log4j.properties`

### 7. Build, test, install

```bash
mvn clean install
```

## Files Changed

| File | Action |
|------|--------|
| `pom.xml` | Update parent version, update datastructures version, bump own version |
| `src/main/resources/log4j2.xml` | Create |
| `src/test/resources/log4j2-test.xml` | Create |
| `src/main/resources/log4j.properties` | Delete |
| `src/test/resources/log4j.properties` | Delete |
