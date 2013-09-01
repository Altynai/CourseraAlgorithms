public class Percolation {
	private static int[] x = { 0, 1, 0, -1 };
	private static int[] y = { 1, 0, -1, 0 };

	private WeightedQuickUnionUF quickUnionData;
	private boolean[][] isBlocked;
	private int N;

	public Percolation(int N) {
		// create N-by-N grid, with all sites blocked
		quickUnionData = new WeightedQuickUnionUF(N * N + 4);
		isBlocked = new boolean[N + 1][N + 1];
		for (int i = 0; i <= N; i++) {
			for (int j = 0; j <= N; j++) {
				isBlocked[i][j] = true;
			}
		}
		this.N = N;
	}

	private int getGradIndex(int i, int j) {
		return (i - 1) * N + j;
	}

	public void open(int i, int j) {
		// open site (row i, column j) if it is not already
		if (i <= 0 || i > N || j <= 0 || j > N)
			throw new IndexOutOfBoundsException("row index i out of bounds");
		if (isBlocked[i][j]) {
			if (i == 1)
				quickUnionData.union(0, getGradIndex(i, j));
			if (i == N)
				quickUnionData.union(N * N + 1, getGradIndex(i, j));
			isBlocked[i][j] = false;
			for (int k = 0; k < 4; k++) {
				int nx = i + x[k];
				int ny = j + y[k];
				if (nx >= 1 && nx <= N && ny >= 1 && ny <= N
						&& !isBlocked[nx][ny]) {
					quickUnionData.union(getGradIndex(i, j),
							getGradIndex(nx, ny));
				}
			}
		}
	}

	public boolean isOpen(int i, int j) {
		// is site (row i, column j) open?
		if (i <= 0 || i > N || j <= 0 || j > N)
			throw new IndexOutOfBoundsException("row index i out of bounds");
		return !isBlocked[i][j];
	}

	public boolean isFull(int i, int j) {
		// is site (row i, column j) full?
		if (i <= 0 || i > N || j <= 0 || j > N)
			throw new IndexOutOfBoundsException("row index i out of bounds");
		int virtualTopNode = 0;
		return isOpen(i, j)
				&& quickUnionData.connected(virtualTopNode, getGradIndex(i, j));
	}

	public boolean percolates() {
		// does the system percolate?
		int virtualTopNode = 0;
		int virtualBottomNode = N * N + 1;
		return quickUnionData.connected(virtualTopNode, virtualBottomNode);
	}
}