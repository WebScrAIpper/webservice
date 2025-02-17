# WebScrAIpper Backend

## Requirements
- Java 21
- A valid OpenAI API key.

## Environment Setup

### OpenAI Api Key setup

Register the key in your environment variables.

Linux and MacOS:
```bash
export OPENAI_API_KEY="sk-XXXXXXXXXXX"
```

Windows:
```cmd
setx OPENAI_API_KEY "sk-XXXXXXXXXXX"
```

## Starting the application
Run the following command to start the application:
```
./gradlew bootRun
```

### Langfuse setup 

Linux and MacOS:
```bash
export LANGFUSE_API_SK="sk-XXXXXXXXXXX"
export LANGFUSE_API_PK "pk-XXXXXXXXXXX"

```

Windows:
```cmd
setx LANGFUSE_API_SK "sk-XXXXXXXXXXX"
setx LANGFUSE_API_PK "pk-XXXXXXXXXXX"
```

### Formatting

We use spotless for formatting. Run the following command to format the code:
```bash
./gradlew spotlessApply
```

To automatically format the code on push, run the following command:
```bash
git config core.hooksPath .githooks 
```

### Logging with SLF4J in the WebScraper Project

In the WebScraper project, we use SLF4J (Simple Logging Facade for Java) for logging. To facilitate consistent logging across the application, we have created an abstract base class `BaseLogger` that initializes the logger.


1. **Extend the `BaseLogger` Class**: Whenever you need to use logging in your class, extend the `BaseLogger` class. This will provide you with a `logger` instance that you can use for logging.

2. **Logging Commands**: Use the `logger` instance to log messages at various levels (e.g., `logger.info`, `logger.debug`, `logger.error`).

#### Example

Here is an example of how to use the `BaseLogger` class in your code:

```java
package com.polytech.webscraipper;

public class MyClass extends BaseLogger {

    public void myMethod() {
        // Log an info message
        logger.info("This is an info message.");

        // Log a debug message
        logger.debug("This is a debug message.");

        // Log an error message
        logger.error("This is an error message.");
    }
}
```

All log messages will be output to the console and also written to the file logs/webscraipper.log. This ensures that you can review the logs both in real-time and retrospectively.