# DIO Spring Boot — Final Project 05: Spring AI Budgeting API

## About the Project

This project is the final module of the **DIO Spring Boot Learning Track** and demonstrates how to integrate **Spring Boot** with **Spring AI** in a financial budgeting API.

The application can receive voice commands, transcribe audio into text, use an AI model to identify the user's intent, execute real application use cases through **Tool Calling**, persist or query financial transactions, and finally generate an audio response.

The project preserves a layered architecture, keeping AI integration separate from domain and business rules.

---

## Project Goal

The main goal is to demonstrate how AI can interact with a real Java application without bypassing its architecture.

The application supports operations such as:

* Creating financial transactions;
* Querying transactions by category;
* Processing voice commands;
* Executing application use cases through Spring AI Tool Calling;
* Generating spoken responses;
* Calculating the total amount spent in a financial category.

---

## Main AI Flow

```text
Audio File
    ↓
TranscriptionModel
    ↓
Text Command
    ↓
ChatClient
    ↓
Intent Detection
    ↓
Tool Calling
    ↓
Application Use Case
    ↓
TransactionRepository
    ↓
JPA / MySQL
    ↓
AI Response
    ↓
TextToSpeechModel
    ↓
MP3 Audio
```

Example voice command:

```text
"Quanto eu gastei com supermercado?"
```

The model identifies that the user wants the total spent in the `GROCERIES` category and calls:

```text
get-total-spent-by-category
```

The calculation is performed by the Java application using persisted transaction data.

---

# Improvement Implemented

The original project already supported creating transactions and listing transactions by category.

This version adds a new capability:

## Total Spent by Category

A new application use case was created:

```text
GetTotalSpentByCategoryUseCase
```

Its responsibility is to:

1. Receive a financial category;
2. Query the repository for transactions in that category;
3. Sum the transaction amounts;
4. Return the total.

The use case is also exposed as a Spring AI Tool:

```text
get-total-spent-by-category
```

This allows the AI assistant to answer questions such as:

```text
"Quanto eu gastei com supermercado?"
```

without calculating values directly inside the language model.

The real calculation remains inside the Java application.

---

## New REST Endpoint

The improvement is also available through REST:

```http
GET /transactions/{category}/total
```

Example:

```http
GET /transactions/GROCERIES/total
```

Response:

```text
230
```

This allows the business rule to be tested independently from the AI layer.

---

# Validated Example

Three transactions were persisted during validation:

| Category  | Description  | Amount |
| --------- | ------------ | -----: |
| GROCERIES | Supermercado |    150 |
| GROCERIES | Feira        |     80 |
| PHARMA    | Farmacia     |     60 |

The new endpoint returned:

```text
GROCERIES → 230
PHARMA    → 60
AUTO      → 0
```

This validates both normal calculations and categories with no transactions.

---

# Architecture

The project keeps the layered architecture used throughout the DIO track.

```text
src/main/java/dio/budgeting
│
├── domain
│   ├── Domain models
│   └── Repository contracts
│
├── application
│   ├── PersistTransactionUseCase
│   ├── ListTransactionsByCategoryUseCase
│   └── GetTotalSpentByCategoryUseCase
│
└── infrastructure
    ├── HTTP / REST
    ├── JPA persistence
    ├── Spring AI
    └── External integrations
```

### Domain

Contains the core financial model and repository contracts.

### Application

Contains the application use cases.

These use cases can be reused by both:

* REST endpoints;
* Spring AI Tool Calling.

### Infrastructure

Contains implementation details such as:

* REST controllers;
* JPA repositories;
* MySQL integration;
* Spring AI configuration;
* Audio processing.

---

# Technologies

The project uses:

* Java 25
* Spring Boot 4
* Spring AI
* Spring Web
* Spring Data JPA
* OpenAI
* ChatClient
* Tool Calling
* Speech-to-Text
* Text-to-Speech
* MySQL
* Docker
* Docker Compose
* Gradle
* JUnit 5
* Mockito

---

# Spring AI Features

## Speech-to-Text

The application uses:

```text
TranscriptionModel
```

to convert uploaded audio files into text.

---

## ChatClient

`ChatClient` sends the transcribed command to the configured AI model.

The model analyzes the user's intent and can select one of the registered application tools.

---

## Tool Calling

Application use cases are exposed using:

```java
@Tool
```

The AI model does not directly access the database.

Instead, it requests the execution of real Java functions.

Available capabilities include:

```text
persist-transaction
list-transactions-by-category
get-total-spent-by-category
```

---

## Text-to-Speech

After the command has been processed, the application uses:

```text
TextToSpeechModel
```

to generate an MP3 audio response.

---

# Financial Categories

The current domain supports the following categories:

```text
GROCERIES
PHARMA
AUTO
```

---

# REST Endpoints

## Create Transaction

```http
POST /transactions
```

Example body:

```json
{
  "description": "Supermercado",
  "category": "GROCERIES",
  "amount": 150
}
```

---

## List Transactions by Category

```http
GET /transactions/{category}
```

Example:

```http
GET /transactions/GROCERIES
```

---

## Get Total Spent by Category

```http
GET /transactions/{category}/total
```

Example:

```http
GET /transactions/GROCERIES/total
```

Response:

```text
230
```

---

## AI Audio Endpoint

```http
POST /transactions/ai
```

Content type:

```text
multipart/form-data
```

The request must contain an audio file using the `file` field.

The endpoint returns:

```text
audio/mp3
```

---

# Requirements

Before running the project, install:

* Java JDK 25;
* Docker Desktop;
* Docker Compose;
* Git.

Verify Java:

```bash
java --version
```

Verify Docker:

```bash
docker --version
docker compose version
```

---

# Running MySQL with Docker

Start the database:

```bash
docker compose up -d
```

Check its status:

```bash
docker compose ps
```

The database should appear as:

```text
healthy
```

The current Docker Compose configuration exposes MySQL on:

```text
localhost:3307
```

---

# OpenAI Configuration

The API key must be provided through the environment variable:

```text
OPENAI_API_KEY
```

Never commit API keys to the repository.

## PowerShell

```powershell
$env:OPENAI_API_KEY="your_api_key_here"
```

## Linux/macOS

```bash
export OPENAI_API_KEY="your_api_key_here"
```

---

# Database Configuration

When explicit datasource configuration is required, the following environment variables can be used.

## PowerShell

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3307/transaction"
$env:SPRING_DATASOURCE_USERNAME="app"
$env:SPRING_DATASOURCE_PASSWORD="app"
```

## Linux/macOS

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3307/transaction"
export SPRING_DATASOURCE_USERNAME="app"
export SPRING_DATASOURCE_PASSWORD="app"
```

---

# Running the Application

### Windows

```powershell
.\gradlew.bat bootRun
```

### Linux/macOS

```bash
./gradlew bootRun
```

The application runs by default at:

```text
http://localhost:8080
```

---

# Testing the REST API

## Create a Grocery Transaction

PowerShell example:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/transactions" `
  -ContentType "application/json" `
  -Body '{"description":"Supermercado","category":"GROCERIES","amount":150}'
```

Create another:

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/transactions" `
  -ContentType "application/json" `
  -Body '{"description":"Feira","category":"GROCERIES","amount":80}'
```

---

## Query Total

```powershell
Invoke-RestMethod `
  -Method Get `
  -Uri "http://localhost:8080/transactions/GROCERIES/total"
```

Expected response:

```text
230
```

---

# Testing the AI Voice Flow

Record an audio file saying something similar to:

```text
Quanto eu gastei com supermercado?
```

Example request:

```powershell
curl.exe `
  -X POST `
  -F "file=@D:\Projetos\teste-total.m4a" `
  http://localhost:8080/transactions/ai `
  --output D:\Projetos\resposta-total.mp3
```

The application processes:

```text
Audio
 ↓
Transcription
 ↓
ChatClient
 ↓
Tool Calling
 ↓
GetTotalSpentByCategoryUseCase
 ↓
MySQL
 ↓
AI response
 ↓
Speech generation
```

The resulting `resposta-total.mp3` contains the generated voice response.

This complete flow was validated successfully during development.

---

# Automated Tests

The new business rule includes unit tests covering:

* Total calculation with multiple transactions;
* Category with no transactions;
* Repository interaction.

Run the specific test:

### Windows

```powershell
.\gradlew.bat test --tests "dio.budgeting.application.GetTotalSpentByCategoryUseCaseTest"
```

### Linux/macOS

```bash
./gradlew test --tests "dio.budgeting.application.GetTotalSpentByCategoryUseCaseTest"
```

Some integration tests included in the original project communicate with external AI providers and therefore require:

* A valid OpenAI API key;
* Active API access;
* Database availability.

---

# Development History

The improvement was developed incrementally:

```text
feat: add total spent by category use case
feat: expose category spending total
test: cover category spending total use case
style: adjust transaction controller formatting
```

This keeps the business rule, integration, tests, and formatting changes separated and easy to review.

---

# What I Learned

During this project, I practiced and reinforced concepts including:

* Spring Boot application architecture;
* Separation between domain, application, and infrastructure;
* Dependency injection;
* Repository pattern;
* Spring Data JPA;
* MySQL integration with Docker;
* REST API development;
* Spring AI configuration;
* ChatClient usage;
* Tool Calling;
* Speech-to-Text integration;
* Text-to-Speech integration;
* Integration between AI and real application functions;
* Unit testing with JUnit and Mockito;
* Incremental development using Git branches and commits.

One of the most important concepts demonstrated by the project is that the AI model does not need direct access to business logic or persistence.

Instead, the model identifies the user's intention and delegates the real operation to controlled application tools.

---

# Possible Future Improvements

Possible future evolutions include:

* Financial summaries across all categories;
* Filtering transactions by date;
* Monthly spending reports;
* Spending limits and alerts;
* Additional financial categories;
* More advanced validation;
* Improved natural-language responses;
* Additional automated integration tests.

These improvements were intentionally left outside the current scope to keep this version focused, functional, and fully testable.

---

# Spring AI Documentation

* [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html)
* [ChatModel API](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
* [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
* [Tools API](https://docs.spring.io/spring-ai/reference/api/tools.html)
* [Audio Transcriptions API](https://docs.spring.io/spring-ai/reference/api/audio/transcriptions.html)
* [Audio Speech API](https://docs.spring.io/spring-ai/reference/api/audio/speech.html)

---

# Shared Architecture References

Additional architecture concepts are documented in the root README:

* [DDD layered architecture](../README.md#ddd-layered-architecture)
* [Class vs record](../README.md#java-class-vs-java-record-in-domain-modeling)
* [Strong typed identifiers](../README.md#strong-typed-identifiers)
* [Repository pattern](../README.md#repository-pattern)
* [Use cases and Clean Architecture](../README.md#use-cases-and-clean-architecture)
* [Docker Compose support](../README.md#docker-compose-support-in-development)

---

## Project Context

This project was developed as part of the **DIO Spring Boot Learning Track — Spring AI Final Project**, with an additional implementation focused on querying total spending by financial category through both REST and AI Tool Calling.
