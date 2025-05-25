from flask import Flask, request, jsonify, render_template
import requests
from collections import defaultdict
from math import log
from threading import Thread
import time
import re
import nltk
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer

# NLTK setup
nltk.download('stopwords', quiet=True)
stop_words = set(stopwords.words('english'))
stemmer = PorterStemmer()

app = Flask(__name__)

# Tokenization function
def tokenize(text):
    """Clean, lowercase, remove punctuation, stopwords and stem."""
    words = re.findall(r'\b\w+\b', text.lower())
    return [stemmer.stem(word) for word in words if word not in stop_words]

# Inverted index and document store
inverted_index = defaultdict(set)
document_store = {}
term_frequencies = defaultdict(lambda: defaultdict(int))
document_frequencies = defaultdict(int)
document_lengths = defaultdict(int)
total_documents = 0

# Background thread function to fetch data
SUBSYSTEMS = [
    {
        "documents": "http://localhost:5000/api/documents/",
        "contents": "http://localhost:5000/api/contents/",
        "source": "Python Subsystem"
    },
    {
        "documents": "http://localhost:8080/api/view_documents",
        "contents": "http://localhost:8080/api/view_contents",
        "source": "Java Subsystem"
    }
]

def fetch_data():
    global total_documents
    while True:
        all_docs = {}
        all_contents = []
        source_map = {}

        for subsystem in SUBSYSTEMS:
            try:
                docs_resp = requests.get(subsystem["documents"])
                print("DOCS STATUS:", docs_resp.status_code)
                docs = docs_resp.json()

                contents_resp = requests.get(subsystem["contents"])
                print("CONTENTS STATUS:", contents_resp.status_code)
                contents = contents_resp.json()

                for doc in docs:
                    orig_id = doc.get("doc_id")
                    if not orig_id:
                        continue
                    new_id = f"{subsystem['source']}#{orig_id}"
                    doc["doc_id"] = new_id
                    source_map[new_id] = subsystem["source"]
                    all_docs[new_id] = doc

                for content in contents:
                    orig_id = content.get("doc_id")
                    if not orig_id:
                        continue
                    new_id = f"{subsystem['source']}#{orig_id}"
                    content["doc_id"] = new_id
                    all_contents.append(content)

            except Exception as e:
                print(f"Failed to fetch from {subsystem['source']}: {e}")

        # Reset index structures
        inverted_index.clear()
        document_store.clear()
        term_frequencies.clear()
        document_frequencies.clear()
        document_lengths.clear()
        total_documents = 0

        for content in all_contents:
            doc_id = content.get("doc_id")
            if not doc_id:
                continue
            text = content.get("content", "")
            tokens = tokenize(text)
            total_documents += 1

            for term in tokens:
                inverted_index[term].add(doc_id)
                term_frequencies[term][doc_id] += 1
            document_lengths[doc_id] = len(tokens)

            doc_data = all_docs.get(doc_id, {})
            document_store[doc_id] = {
                "title": doc_data.get("title", "Unknown"),
                "author": doc_data.get("author", "Unknown"),
                "file_url": doc_data.get("file_url", "#"),
                "source": source_map.get(doc_id, "Unknown Subsystem")
            }

        for term, docs in term_frequencies.items():
            document_frequencies[term] = len(docs)

        time.sleep(60)

# Start background fetching
Thread(target=fetch_data, daemon=True).start()

# TF-IDF ranking
def rank_documents(query):
    tokens = tokenize(query)
    scores = defaultdict(float)

    for term in tokens:
        if term not in inverted_index:
            continue
        idf = log((1 + total_documents) / (1 + document_frequencies[term])) + 1
        for doc_id in inverted_index[term]:
            tf = term_frequencies[term][doc_id] / document_lengths[doc_id]
            scores[doc_id] += tf * idf

    for doc_id, doc in document_store.items():
        combined = f"{doc['title']} {doc['author']}".lower()
        combined_tokens = tokenize(combined)
        if any(term in combined_tokens for term in tokens) and doc_id not in scores:
            scores[doc_id] = 0.1  # fallback score

    ranked_docs = sorted(scores.items(), key=lambda x: x[1], reverse=True)
    return [document_store[doc_id] | {"score": round(score, 2)} for doc_id, score in ranked_docs]

# API Endpoint
@app.route("/api/search")
def search_api():
    query = request.args.get("q", "")
    if not query:
        return jsonify([])
    results = rank_documents(query)
    return jsonify(results)

# UI Endpoint
@app.route("/", methods=["GET", "POST"])
@app.route("/search", methods=["GET", "POST"])
def search_ui():
    query = ""
    results = []
    if request.method == "POST":
        query = request.form.get("query", "")
        results = rank_documents(query)
    return render_template("search.html", query=query, results=results)

if __name__ == '__main__':
    app.run(port=9000, debug=True)
