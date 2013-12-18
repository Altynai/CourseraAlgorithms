import java.util.TreeSet;

public class BoggleSolver {

	private int max_link_length = 0;
	private int dx[] = { -1, -1, -1, 0, 1, 1, 1, 0 };
	private int dy[] = { -1, 0, 1, 1, 1, 0, -1, -1 };
	private boolean used[][] = null;

	private Node root = new Node();
	private static final int R = 26; // 'A' - 'Z'
	private static final int NULL_VAL = -1;

	private static class Node {
		private int val;
		private Node[] next;
		private boolean has_subtree;

		public Node() {
			val = NULL_VAL;
			has_subtree = false;
			next = new Node[R];
			for (int i = 0; i < R; i++)
				next[i] = null;
		}
	}

	private void put(String word, int val) {
		Node current_node = root;
		for (int i = 0; i < word.length(); i++) {
			int index = word.charAt(i) - 'A';
			if (current_node.next[index] == null)
				current_node.next[index] = new Node();
			current_node.has_subtree = true;
			current_node = current_node.next[index];
		}
		current_node.val = val;
	}

	private Node get(String word) {
		Node current_node = root;
		for (int i = 0; i < word.length(); i++) {
			int index = word.charAt(i) - 'A';
			if (current_node.next[index] == null)
				return null;
			current_node = current_node.next[index];
		}
		return current_node;
	}

	// Initializes the data structure using the given array of strings as the
	// dictionary.
	// (You can assume each word in the dictionary contains only the uppercase
	// letters A through Z.)
	public BoggleSolver(String[] dictionary) {
		for (String word : dictionary) {
			word = formatQu2Q(word.toUpperCase());
			int score = calculateScore(word);
			put(word, score);
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

		/*
		 * Optimization One: If current_word.length() > max_link_length, it
		 * means that any word longer than current_word will never appears in
		 * the dictionary. So there's no need to continue searching.
		 */
		if (current_word.length() > max_link_length)
			return;
		Node deepest_node = get(current_word);
		if (deepest_node == null)
			return;
		if (deepest_node.val > 0)
			result_set.add(formatQ2Qu(current_word));
		/*
		 * Optimization Two: If deepest_node.has_subtree is false, it means that
		 * any word with this prefix never appears in the dictionary, and also,
		 * there's no need to continue searching.
		 */
		if (!deepest_node.has_subtree)
			return;

		int M = board.rows(), N = board.cols();
		for (int k = 0; k < dx.length; k++) {
			int _i = i + dx[k];
			int _j = j + dy[k];
			if (!check2DPosition(_i, _j, M, N) || used[_i][_j])
				continue;
			used[_i][_j] = true;
			dfs_collect(board, result_set, _i, _j,
					current_word + board.getLetter(_i, _j));
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
		word = formatQu2Q(word);
		Node deepest_node = get(word);

		if (deepest_node == null || deepest_node.val == NULL_VAL)
			return 0;
		else
			return deepest_node.val;
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
