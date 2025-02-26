# WebScrAIpper Backend

## Requirements
- Java 21
- A valid OpenAI API key.

## Environment Setup

### OpenAI & Langfuse Api Keys setup

Register the keys in your environment variables.

Linux and MacOS:
```bash
export OPENAI_API_KEY="sk-XXXXXXXXXXX"
export LANGFUSE_API_SK="sk-XXXXXXXXXXX"
export LANGFUSE_API_PK="pk-XXXXXXXXXXX"
```

Windows:
```cmd
setx OPENAI_API_KEY "sk-XXXXXXXXXXX"
setx LANGFUSE_API_SK "sk-XXXXXXXXXXX"
setx LANGFUSE_API_PK "pk-XXXXXXXXXXX"
```

### Setting up python virtual environment
Linux and MacOS:
```cmd
python3 -m venv src/main/python/.venv
source src/main/python/.venv/bin/activate
pip install --upgrade pip setuptools
pip install youtube_transcript_api yt-dlp
```

Windows:
```cmd
python -m venv src\main\python\.venv
src\main\python\.venv\Scripts\activate
pip install --upgrade pip setuptools
pip install youtube_transcript_api yt-dlp
```

### Setting up python virtual environment
Linux and MacOS:
```cmd
python3 -m venv /python/.venv
/python/.venv/bin/activate
pip install --upgrade pip setuptools
pip install youtube_transcript_api yt-dlp
```

Windows:
```cmd
python -m venv \python\.venv
\python\.venv\Scripts\activate
pip install --upgrade pip setuptools
pip install youtube_transcript_api yt-dlp
```

## Starting the application

### Developper mode
Run the following command to start the application:
```
./gradlew bootRun
```

### Production mode
```
docker compose up --build -d
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

### Zscaler Certificate Setup

If your network uses Zscaler for SSL inspection, you must add the Zscaler Root CA certificate to the JDK keystore to ensure secure connections.
Importing the Certificate

Run the following command to import the certificate into the JDK keystore:

```bash
keytool -import -alias zscaler -keystore "C:\Program Files\OpenJDK\Jdk-21.0.1\lib\security\cacerts" -file "C:\data\tmp\Zscaler Root CA.crt"
```

### Logging with SLF4J in the WebScraper Project

In the WebScraper project, we use SLF4J (Simple Logging Facade for Java) for logging. To ensure consistent logging across the application, we use the `BaseLogger` class, which provides a logger instance.

#### Usage

1. Create an instance of `BaseLogger` inside your class.
2. Use the logger instance to log messages at different levels.

#### Example

```java
package com.polytech.webscraper;

public class MyClass {
    private final BaseLogger logger = new BaseLogger();

    public void myMethod() {
        logger.info("This is an info message.");
        logger.debug("This is a debug message.");
        logger.error("This is an error message.");
    }
}
```

#### Log Output

All log messages are output to the console and written to logs/webscraper.log for real-time and retrospective review.