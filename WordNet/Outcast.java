public class Outcast {
	private WordNet wordnet = null;

	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		this.wordnet = wordnet;
	}

	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {
		int nounCount = nouns.length;
		int maxDistSum = 0;
		int id = 0;
		for (int i = 0; i < nounCount; i++) {
			int sum = 0;
			for (int j = 0; j < nounCount; j++) {
				int dist = wordnet.distance(nouns[i], nouns[j]);
				sum += dist;
			}
			if (sum > maxDistSum) {
				maxDistSum = sum;
				id = i;
			}
		}
		return nouns[id];
	}

	// for unit testing of this class (such as the one below)
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for (int t = 2; t < args.length; t++) {
			String[] nouns = In.readStrings(args[t]);
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}
	}
}