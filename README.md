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
