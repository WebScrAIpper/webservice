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
