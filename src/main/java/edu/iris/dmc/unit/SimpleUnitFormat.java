package edu.iris.dmc.unit;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.iris.dmc.fdsn.station.model.Units;

public class SimpleUnitFormat {
	private static final SimpleUnitFormat instance;
	static {
		try {
			instance = new SimpleUnitFormat("units.properties");
		} catch (IOException e) {
			throw new RuntimeException("Could not start validator since unit file was not found.");
		}
	}
	private Map<String,String> dictionary = new HashMap<>();

	private SimpleUnitFormat(String name) throws IOException {
		loadProperties(name);
	}


	private void loadProperties(String location) throws IOException {
		Objects.requireNonNull(location, "Cannot find unit resource file ");

		try (InputStream is = SimpleUnitFormat.class.getClassLoader().getResourceAsStream(location)) {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, List<String>> map = mapper.readValue(is, new TypeReference<Map<String, List<String>>>() {
			});
			for (Entry<String, List<String>> e : map.entrySet()) {
				for(String s:e.getValue()) {
					dictionary.put(s.toLowerCase(), e.getKey());
				}
			}
		}
	}


	public Units parse(String text) throws InvalidUnitException {
		if(text==null) {
			return null;
		}
		String key = dictionary.get(text.toLowerCase());
		if (key == null) {
			throw new InvalidUnitException("Unkown unit :" + text);
		}
		Units u = new Units();
		u.setName(key);
		return u;
	}

	public static SimpleUnitFormat getInstance() {
		return instance;
	}

	class Dictionary {
		private HashMap<Character, Node> roots = new HashMap<>();

		public String search(String string) {
			if (string == null || string.isEmpty()) {
				string = "null";
			}
			string = string.toLowerCase();
			if (roots.containsKey(string.charAt(0))) {
				Node n = roots.get(string.charAt(0));
				if (string.length() == 1 && n.endOfWord) {
					return n.key;
				}
				return searchFor(string.substring(1), roots.get(string.charAt(0)));
			} else {
				return null;
			}
		}
		private String searchFor(String string, Node node) {
			if (string.length() == 0) {
				if (node.endOfWord) {
					return node.key;
				} else {
					return null;
				}
			}

			if (node.children.containsKey(string.charAt(0))) {
				return searchFor(string.substring(1), node.children.get(string.charAt(0)));
			} else {
				return node.key;
			}
		}
		
		
		public void insert(String key, List<String> array) {
			for (String s : array) {
				insert(key, s);
			}
		}

		/**
		 * Insert a word into the dictionary.
		 * 
		 * @param string The word to insert.
		 */
		public void insert(String key, String string) {
			if (string == null || string.isEmpty()) {
				string = "null";
			}
			string = string.toLowerCase();
			Node r = roots.get(string.charAt(0));
			if (r == null) {
				r = new Node(key);
				roots.put(string.charAt(0), r);
			}
			if (string.length() == 1) {
				r.endOfWord = true;
				return;
			}
			insertWord(key, string.substring(1), roots.get(string.charAt(0)));
		}

		// Recursive method that inserts a new word into the trie tree.
		private void insertWord(String key, String string, Node node) {
			final Node nextChild;
			if (node.children.containsKey(string.charAt(0))) {
				nextChild = node.children.get(string.charAt(0));
			} else {
				nextChild = new Node(key);
				node.children.put(string.charAt(0), nextChild);
			}

			if (string.length() == 1) {
				nextChild.endOfWord = true;
				return;
			} else {
				insertWord(key, string.substring(1), nextChild);
			}
		}

		

		public void print() {
			print(roots);
		}

		private void print(Map<Character, Node> nodes) {
			if (nodes == null || nodes.isEmpty()) {
				return;
			}
			Set<Entry<Character, Node>> set = nodes.entrySet();
			for (Entry<Character, Node> e : set) {
				Node n =e.getValue();
				if(n.endOfWord) {
					System.out.println(e.getKey() + "  " + n);
				}
				
				print(e.getValue().children);
			}
		}

		class Node {
			public Node parent;
			public String key;
			public Boolean endOfWord = false;
			public Map<Character, Node> children = new HashMap<Character, Node>();

			Node(String key) {
				this.key = key;
			}

			@Override
			public String toString() {
				return "Node [key=" + key + ", endOfWord=" + endOfWord + "]";
			}

		}
	}

}
