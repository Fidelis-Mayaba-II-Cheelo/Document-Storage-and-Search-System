# 📄 Document Storage and Search System

A full-stack system composed of three independently built and integrated subsystems for document storage, retrieval, and full-text search with relevance ranking. Built using **Java**, **Python**, **MySQL**, and **RESTful APIs**, this project demonstrates multi-language microservice integration and search engine implementation.

## 🔧 System Overview

### 🔹 Subsystem 1 – Java + MySQL
- Handles document upload, metadata storage, and retrieval.
- REST API supports querying documents by metadata (e.g., title, author).
- Stores document metadata and file paths in MySQL.

**Database Structure**:
- `documents` – doc_id, title, author, upload_date, file path
- `content` – actual document content or reference link

### 🔹 Subsystem 2 – Python (Flask or FastAPI) + MySQL
- Same functionality as Subsystem 1, implemented in Python.
- RESTful API for document and metadata access.
- Uses the same database schema as Subsystem 1 for consistency.

### 🔹 Subsystem 3 – Python (Search Engine)
- Aggregates documents from Subsystems 1 and 2.
- Builds an **inverted index** to support fast keyword search.
- Ranks search results using:
  - Term Frequency (TF)
  - Document Frequency (DF)
  - Cosine Similarity or BM25

**Key Features**:
- Periodically fetches documents from Subsystems 1 and 2
- Offers a `/search` API endpoint with ranked results
- Unit-tested ranking and search logic

---

## 🧪 Testing Overview

- **Unit Tests**:
  - Ranking functions (BM25, TF-IDF)
  - Inverted index generation
  - Search result accuracy

- **Integration Tests**:
  - API communication between subsystems
  - End-to-end document ingestion and search flow

- **Performance Tests**:
  - Response time with large datasets
  - Scalability assessment by adding more documents

---

## 🛠️ Tech Stack

| Subsystem      | Language | Database | Frameworks / Tools            |
|----------------|----------|----------|-------------------------------|
| Subsystem 1    | Java     | MySQL    | REST API (Spring Boot)        |
| Subsystem 2    | Python   | MySQL    | Flask                         |
| Subsystem 3    | Python   | —        | NLTK / Whoosh / REST API      |

---


## 🚀 Features

- REST APIs for document upload and metadata querying
- Full-text keyword search across multiple sources
- Ranked search results based on term relevance
- Multi-language interoperability (Java ↔ Python)
- Modular design for scalability and maintainability

---

## 📌 Future Improvements

- Add support for image and OCR-based document indexing
- Implement authentication for API access
- Deploy with Docker and orchestrate with Kubernetes
- Switch to Elasticsearch for production-scale indexing

---
