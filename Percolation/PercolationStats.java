public class PercolationStats {
	private double threshold;
	private double deviation;
	private double confidenceLo;
	private double confidenceHi;

	public PercolationStats(int N, int T) {
		// perform T independent computational experiments on an N-by-N grid
		if (N <= 0 || T <= 0)
			throw new java.lang.IllegalArgumentException("illegal argument.");

		double fraction[] = new double[T];
		for (int test = 0; test < T; test++) {
			Percolation percolation = new Percolation(N);
			int[] inds = new int[N * N + 1];

			for (int i = 1; i <= N * N; i++)
				inds[i] = i;

			int openedCount = 0;
			while (!percolation.percolates()) {
				int blockedCount = N * N - openedCount;
				int i = StdRandom.uniform(1, blockedCount + 1);
				int x = 0, y = 0;
				if (inds[i] % N == 0) {
					x = inds[i] / N;
					y = N;
				} else {
					x = inds[i] / N + 1;
					y = inds[i] % N;
				}
				openedCount++;
				percolation.open(x, y);

				// swap inds[i], inds[blockedCount]
				int t = inds[i];
				inds[i] = inds[blockedCount];
				inds[blockedCount] = t;

			}
			fraction[test] = (double) openedCount / (N * N);
		}
		threshold = StdStats.mean(fraction);
		deviation = StdStats.stddev(fraction);

		confidenceLo = threshold - 1.96 * deviation / Math.sqrt((double) T);
		confidenceHi = threshold + 1.96 * deviation / Math.sqrt((double) T);
	}

	public double mean() {
		// sample mean of percolation threshold
		return this.threshold;
	}

	public double stddev() {
		// sample standard deviation of percolation threshold
		return this.deviation;
	}

	public double confidenceLo() {
		// returns lower bound of the 95% confidence interval
		return this.confidenceLo;
	}

	public double confidenceHi() {
		// returns upper bound of the 95% confidence interval
		return this.confidenceHi;
	}

	public static void main(String[] args) {
		// test client, described below
		int N = Integer.parseInt(args[0]);
		int T = Integer.parseInt(args[1]);

		PercolationStats stats = new PercolationStats(N, T);
		StdOut.println(String.format("mean                    = %f",
				stats.mean()));
		StdOut.println(String.format("stddev                  = %.16f",
				stats.stddev()));
		StdOut.println(String.format("95%% confidence interval = %.16f, %.16f",
				stats.confidenceLo(), stats.confidenceHi()));
	}
}