import java.util.TreeSet;

public class BoggleSolver {

	private TrieST<Integer> trie = null;
	private int max_link_length = 0;
	private int dx[] = { -1, -1, -1, 0, 1, 1, 1, 0 };
	private int dy[] = { -1, 0, 1, 1, 1, 0, -1, -1 };
	private boolean used[][] = null;
	
	// Initializes the data structure using the given array of strings as the
	// dictionary.
	// (You can assume each word in the dictionary contains only the uppercase
	// letters A through Z.)
	public BoggleSolver(String[] dictionary) {
		trie = new TrieST<Integer>();
		for (String word : dictionary) {
			word = word.toUpperCase();
			for (int i = 1; i < word.length(); i++) {
				String sub_word = formatQu2Q(word.substring(0, i));
				Integer sub_word_score = trie.get(sub_word);
				if (sub_word_score == null)
					trie.put(sub_word, 0);
			}
			word = formatQu2Q(word);
			int score = calculateScore(word);
			trie.put(word, score);
			if (word.length() > max_link_length)
				max_link_length = word.length();
		}
	}

	// Returns the set of all valid words in the given Boggle board, as an
	// Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		TreeSet<String> result_set = new TreeSet<String>();
		return collect(board, result_set);
	}

	private Iterable<String> collect(BoggleBoard board,
			TreeSet<String> result_set) {
		int M = board.rows(), N = board.cols();
		used = new boolean[M][N];
		
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				used[i][j] = true;
				dfs_collect(board, result_set, i, j, "" + board.getLetter(i, j));
				used[i][j] = false;
			}
		}
		return result_set;
	}

	private void dfs_collect(BoggleBoard board, TreeSet<String> result_set,
			int i, int j, String current_word) {

		if (current_word.length() > max_link_length)
			return;
		if (!trie.longestPrefixOf(current_word).equals(current_word))
			return;
		
		Integer current_word_score = trie.get(current_word);
		if (current_word_score != null && current_word_score > 0)
			result_set.add(formatQ2Qu(current_word));
		int M = board.rows(), N = board.cols();

		for (int k = 0; k < dx.length; k++) {
			int _i = i + dx[k];
			int _j = j + dy[k];
			if (!check2DPosition(_i, _j, M, N) || used[_i][_j])
				continue;
			/*
			 * Optimization If we get trie.longestPrefixOf(new_word) !=
			 * new_word, it means the sub-tree of that node is null, so there is
			 * no need to continue searching.
			 */
			
			used[_i][_j] = true;
			dfs_collect(board, result_set, _i, _j, current_word + board.getLetter(_i, _j));
			used[_i][_j] = false;
		}
	}

	private boolean check2DPosition(int i, int j, int M, int N) {
		return (i >= 0 && i < M) && (j >= 0 && j < N);
	}

	// Returns the score of the given word if it is in the dictionary, zero
	// otherwise.
	// (You can assume the word contains only the uppercase letters A through
	// Z.)
	public int scoreOf(String word) {
		if (trie == null)
			return 0;
		else {
			word = formatQu2Q(word);
			Integer score = trie.get(word);
			return (score == null ? 0 : score.intValue());
		}
	}

	private String formatQu2Q(String word) {
		return word.replaceAll("QU", "Q");
	}

	private String formatQ2Qu(String word) {
		return word.replaceAll("Q", "QU");
	}
	
	private int calculateScore(String word) {
		int len = 0;
		for (int i = 0; i < word.length(); i++) {
			if (word.charAt(i) == 'Q')
				len += 2;
			else
				len++;
		}
		if (len >= 8)
			return 11;
		else {
			int score[] = { 0, 0, 0, 1, 1, 2, 3, 5 };
			return score[len];
		}
	}

	public static void main(String[] args) {
		In in = new In(args[0]);
		String[] dictionary = in.readAllStrings();
		BoggleSolver solver = new BoggleSolver(dictionary);
		BoggleBoard board = new BoggleBoard(args[1]);
		int score = 0;
		for (String word : solver.getAllValidWords(board)) {
			StdOut.println(word);
			score += solver.scoreOf(word);
		}
		StdOut.println("Score = " + score);
	}
}
