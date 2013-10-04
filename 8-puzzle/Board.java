import java.util.Arrays;
import java.util.Iterator;

public class Board {
	private int[][] blocks = null;
	private int N = 0;

	private class BoardIterator implements Iterator<Board>, Iterable<Board> {
		private Board[] boards = null;
		private int currentIndex = 0;
		private int total = 0;

		public BoardIterator() {
			boards = new Board[4];
			currentIndex = 0;
			total = 0;
			int zeroX = 0, zeroY = 0;
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < N; j++) {
					if (blocks[i][j] == 0) {
						zeroX = i;
						zeroY = j;
						break;
					}
				}
			}
			int swap = 0, dx[] = { 0, 1, 0, -1 }, dy[] = { 1, 0, -1, 0 };
			int x, y;
			for (int k = 0; k < 4; k++) {
				x = zeroX + dx[k];
				y = zeroY + dy[k];
				if (x < 0 || x >= N || y < 0 || y >= N)
					continue;
				Board neighborBoard = new Board(blocks);
				swap = neighborBoard.blocks[zeroX][zeroY];
				neighborBoard.blocks[zeroX][zeroY] = neighborBoard.blocks[x][y];
				neighborBoard.blocks[x][y] = swap;
				boards[total++] = neighborBoard;
			}
		}

		@Override
		public boolean hasNext() {
			return currentIndex != total;
		}

		@Override
		public Board next() {
			if (currentIndex == total)
				throw new IndexOutOfBoundsException();
			return boards[currentIndex++];
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub

		}

		@Override
		public Iterator<Board> iterator() {
			return this;
		}
	}

	public Board(int[][] blocks) { // construct a board from an N-by-N array of
									// blocks
		this.blocks = deepCopy(blocks);
		this.N = blocks[0].length;
	}

	// (where blocks[i][j] = block in row i, column j)
	public int dimension() { // board dimension N
		return N;
	}

	public int hamming() { // number of blocks out of place
		int outOfPlace = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (blocks[i][j] != i * N + j + 1)
					outOfPlace++;
			}
		}
		return outOfPlace - 1; // consider zero
	}

	public int manhattan() { // sum of Manhattan distances between blocks and
								// goal
		int totalDistances = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				int value = blocks[i][j], x = 0, y = 0;
				if(value == 0)
					continue;
				if (value % N == 0) {
					y = N - 1;
					x = value / N - 1;
				} else {
					y = value % N - 1;
					x = value / N;
				}
				totalDistances += Math.abs(x - i) + Math.abs(y - j);
			}
		}
		return totalDistances;
	}

	public boolean isGoal() { // is this board the goal board?
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				int value = i * N + j + 1;
				if (i == N - 1 && j == N - 1)
					value = 0;
				if (blocks[i][j] != value)
					return false;
			}
		}
		return true;
	}

	public Board twin() { // a board obtained by exchanging two adjacent blocks
							// in the same row
		Board twinBoard = new Board(blocks);
		boolean swaped = false;
		for (int i = 0; !swaped && i < N; i++) {
			for (int j = 0; !swaped && j + 1 < N; j++) {
				if (twinBoard.blocks[i][j] != 0
						&& twinBoard.blocks[i][j + 1] != 0) {
					swaped = true;
					int swap = twinBoard.blocks[i][j];
					twinBoard.blocks[i][j] = twinBoard.blocks[i][j + 1];
					twinBoard.blocks[i][j + 1] = swap;
				}
			}
		}
		return twinBoard;
	}

	public boolean equals(Object y) { // does this board equal y?
		if (this == y)
			return true;
		if (y == null || y.getClass() != this.getClass())
			return false;
		Board another = (Board) y;
		if (this.N != another.N)
			return false;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (blocks[i][j] != another.blocks[i][j])
					return false;
			}
		}
		return true;
	}

	public Iterable<Board> neighbors() { // all neighboring boards
		return new BoardIterator();
	}

	public String toString() { // string representation of the board (in the
								// output format specified below)
		StringBuilder boardString = new StringBuilder();
		boardString.append(N + "\n");
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				boardString.append(" " + blocks[i][j]);
			}
			boardString.append("\n");
		}
		return boardString.toString();
	}

	private int[][] deepCopy(int[][] original) {
		if (original == null)
			return null;
		int[][] result = new int[original.length][];
		for (int i = 0; i < original.length; i++) {
			result[i] = Arrays.copyOf(original[i], original[i].length);
		}
		return result;
	}
}