import java.util.TreeMap;

public class WordNet {

	private Digraph graph = null;
	private SAP sap = null;
	private TreeMap<String, Integer> synset2int = null;
	private TreeMap<Integer, String> int2synset = null;

	private Integer changeSynset2int(String synset) {
		return synset2int.get(synset);
	}

	private String changeInt2Synset(Integer id) {
		return int2synset.get(id);
	}
	
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		synset2int = new TreeMap<String, Integer>();
		int2synset = new TreeMap<Integer, String>();
		
		// work on synsets
		In in = new In(synsets);
		int nodeCount = 0;
		while (in.hasNextLine()) {
			String[] splitedLine = in.readLine().split(",");
			int id = Integer.parseInt(splitedLine[0]);
			nodeCount = nodeCount > id ? nodeCount : id;
			for (String synset : splitedLine[1].split(" "))
				synset2int.put(synset, id);
			int2synset.put(id, splitedLine[1]);
		}
		nodeCount++;
		graph = new Digraph(nodeCount);
		// work on hypernyms
		in = new In(hypernyms);
		while (in.hasNextLine()) {
			String line = in.readLine();
			String[] splitedLine = line.split(",");
			int u = Integer.parseInt(splitedLine[0]);
			for (int i = 1; i < splitedLine.length; i++) {
				int v = Integer.parseInt(splitedLine[1]);
				graph.addEdge(u, v);
			}
		}
		// check rooted DAG
		DirectedCycle cycle = new DirectedCycle(graph);
		if (cycle.hasCycle())
			throw new IllegalArgumentException();
		Digraph reverseGraph = graph.reverse();
		DirectedDFS dfs = null;
		for (int i = 0; i < graph.V(); i++) {
			if (!graph.adj(i).iterator().hasNext()) {
				dfs = new DirectedDFS(reverseGraph, i);
				break;
			}
		}
		for (int i = 0; i < reverseGraph.V(); i++) {
			if (!dfs.marked(i))
				throw new IllegalArgumentException();
		}
		sap = new SAP(graph);
	}

	// the set of nouns (no duplicates), returned as an Iterable
	public Iterable<String> nouns() {
		if (synset2int.isEmpty())
			return null;
		else
			return synset2int.keySet();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		return synset2int.containsKey(word);
	}

	// distance between nounA and nounB (defined below)
	public int distance(String nounA, String nounB) {
		if(!isNoun(nounA) || !isNoun(nounB))
			throw new IllegalArgumentException();
		return sap.length(changeSynset2int(nounA), changeSynset2int(nounB));
	}

	// a synset (second field of synsets.txt) that is the common ancestor of
	// nounA and nounB
	// in a shortest ancestral path (defined below)
	public String sap(String nounA, String nounB) {
		if(!isNoun(nounA) || !isNoun(nounB))
			throw new IllegalArgumentException();
		int ancestor = sap.ancestor(changeSynset2int(nounA), changeSynset2int(nounB));
		return changeInt2Synset(ancestor);
	}

	// for unit testing of this class
	public static void main(String[] args) {
		WordNet net = new WordNet("synsets.txt", "hypernyms.txt");
		StdOut.println(net.changeSynset2int("strawberry"));
	}
}
