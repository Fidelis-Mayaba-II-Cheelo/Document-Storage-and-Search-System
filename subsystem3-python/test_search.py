import unittest
from app import build_inverted_index, rank_documents

class TestSearchFunctions(unittest.TestCase):

    def setUp(self):
        self.documents = [
            {'doc_id': 1, 'title': 'Python Basics', 'author': 'Alice'},
            {'doc_id': 2, 'title': 'Advanced Python', 'author': 'Bob'}
        ]
        self.contents = [
            {'doc_id': 1, 'content': 'Learn Python programming.'},
            {'doc_id': 2, 'content': 'Deep dive into Python.'}
        ]
        self.index = build_inverted_index(self.documents, self.contents)

    def test_inverted_index(self):
        self.assertIn('python', self.index)
        self.assertIn(1, self.index['python'])
        self.assertIn(2, self.index['python'])

    def test_ranking(self):
        results = rank_documents('python', self.index, self.documents)
        self.assertEqual(len(results), 2)
        self.assertEqual(results[0]['doc_id'], 1)

if __name__ == '__main__':
    unittest.main()
