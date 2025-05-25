import unittest
from app import tokenize, rank_documents, document_store, inverted_index, term_frequencies, document_lengths
from collections import defaultdict

class TestSubsystemFunctions(unittest.TestCase):

    def test_tokenize(self):
        input_text = "Running in the rain is fun and exciting!"
        tokens = tokenize(input_text)
        # 'in', 'the', 'is', 'and' are stopwords and should be removed
        self.assertIn("run", tokens)
        self.assertNotIn("in", tokens)
        self.assertNotIn("is", tokens)
        self.assertIsInstance(tokens, list)

    def test_rank_documents(self):
        # Set up test documents manually
        document_store.clear()
        term_frequencies.clear()
        document_lengths.clear()
        inverted_index.clear()

        document_store[1] = {"title": "Python Guide", "author": "Alice", "file_url": "#", "source": "Python Subsystem"}
        document_store[2] = {"title": "Java Reference", "author": "Bob", "file_url": "#", "source": "Java Subsystem"}

        term_frequencies["python"][1] = 3
        document_lengths[1] = 3
        term_frequencies["java"][2] = 2
        document_lengths[2] = 2

        inverted_index["python"].add(1)
        inverted_index["java"].add(2)

        result = rank_documents("python")
        self.assertGreater(result[0]['score'], 0)
        self.assertEqual(result[0]['title'], "Python Guide")

if __name__ == '__main__':
    unittest.main()
