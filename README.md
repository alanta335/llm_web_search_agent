# ✨ LLM Web Search Agent

## 🚀 Overview
This project enables a Language Model (LLM) to search the web and scrape data from the search results to build a knowledge base. The LLM then uses this enriched knowledge base to answer user queries with greater accuracy and context.

The **LLM Web Search Agent** is built using **Java (Spring Boot)** and **SearxNG** as the web search engine. It combines advanced AI-powered semantic understanding with robust web search functionalities, creating a powerful and efficient solution for handling user queries.

---

## ✨ Features
- **Seamless Language Model Integration:**
    - Support for configurable LLM APIs (e.g., OpenAI).
    - Embedding-based processing for improved query understanding.
- **Powerful Search Engine Support:**
    - SearxNG ensures reliable, privacy-respecting web searches.
- **Comprehensive Logging:**
    - Track requests and responses for debugging and analysis.
    - Debug-level logs for detailed troubleshooting.

---

## 📜 Requirements
To get started, make sure you have the following:
1. **Java 21 or later**
2. **Maven** for dependency management
3. **Docker** and **Docker Compose** to run SearxNG

---

## ⚙️ Configuration
### Application Properties
Prepare the `application.properties` file with the required configurations:

#### 🌱 Spring Configuration
```properties
spring.application.name=smartAgent
```

#### 🤖 LLM Configuration
- `model-url`: URL of the LLM API.
- `model-api-key`: Your API key for the LLM.
- `embedding-api-key`: API key for embeddings.
- `model-name`: The name of the LLM to use.
- `embedding-model-name`: Name of the embedding model.

#### 🔍 Search Engine Configuration
- `search-engine-url`: The URL for the SearxNG search engine.

#### 📜 Logging Configuration
Enable detailed request and response logging:
```properties
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true
logging.level.dev.langchain4j=DEBUG
logging.level.dev.ai4j.openai4j=DEBUG
```

### 🛠️ SearxNG Configuration
1. Navigate to the `searxng-docker` directory.
2. Ensure the necessary environment variables are set in the Docker Compose file.
3. Start SearxNG using:
   ```bash
   docker-compose up -d
   ```

---

## 🛠️ Build and Run
1. **Build the Application:**
   ```bash
   mvn clean package
   ```

2. **Run the Application:**
   ```bash
   java -jar target/<your-jar-name>.jar
   ```

3. **Test the Endpoint:**
   Use the following example `curl` command to test:
   ```bash
   curl --location 'http://localhost:8080/web-search-agent?question=weatherin2025-1-1' \
   --data ''
   ```

---

## 📦 Dependencies
- **Spring Boot:** Framework for building modern web applications.
- **LangChain4j:** For seamless LLM and embedding integration.
- **Lombok:** Simplify Java development with powerful annotations.
- **SearxNG:** A metasearch engine respecting your privacy.

---

## 🔖 Logging
The application offers detailed logging capabilities:
- **Requests & Responses:** Logged by LangChain4j for transparency.
- **Debugging:** Enabled for in-depth troubleshooting.

---

## 🤝 Contributing
We welcome contributions from the open-source community! Here’s how you can help:
1. Fork the repository and create a new branch.
2. Submit feature requests, report bugs, or suggest enhancements.
3. Open pull requests with your improvements.

Together, we can make this project even better! 💪

---

## 📜 License
This project is licensed under the MIT License. Feel free to use, modify, and distribute it as you see fit.

---

## ❤️ Acknowledgments
We’d like to thank the creators of these amazing technologies:
- [Spring Boot](https://spring.io/projects/spring-boot)
- [LangChain4j](https://github.com/langchain4j)
- [SearxNG](https://github.com/searxng/searxng)

---

## 🌟 Join Us!
Help us grow the LLM Web Search Agent into the ultimate search and AI tool. Let’s build something incredible together!

---

## 🔦 Swagger API Documentation
1. After starting the Spring Boot application, visit [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) to access the Swagger API documentation.
2. Alternatively, the pre-generated Swagger documentation is located in the `swagger` folder. Open `index.html`(https://github.com/alanta335/llm_web_search_agent_java/blob/main/swagger/index.html) to view it locally.
