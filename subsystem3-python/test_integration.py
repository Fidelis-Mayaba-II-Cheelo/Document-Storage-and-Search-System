import unittest
import requests

class TestIntegration(unittest.TestCase):

    def test_python_subsystem_docs(self):
        resp = requests.get("http://localhost:5000/api/documents/")
        self.assertEqual(resp.status_code, 200)
        self.assertIsInstance(resp.json(), list)

    def test_java_subsystem_docs(self):
        resp = requests.get("http://localhost:8080/api/view_documents")
        self.assertEqual(resp.status_code, 200)
        self.assertIsInstance(resp.json(), list)

    def test_subsystem3_search_api(self):
        resp = requests.get("http://localhost:9000/api/search?q=python")
        self.assertEqual(resp.status_code, 200)
        self.assertIsInstance(resp.json(), list)

if __name__ == '__main__':
    unittest.main()
