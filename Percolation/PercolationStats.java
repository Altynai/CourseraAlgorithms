import java.util.ArrayList;

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
			ArrayList<Point2D> blockedPoints = new ArrayList<Point2D>();

			for (int i = 1; i <= N; i++) {
				for (int j = 1; j <= N; j++)
					blockedPoints.add(new Point2D(i, j));
			}
			int openedCount = 0;
			while (!percolation.percolates()) {
				int blockedCount = blockedPoints.size();
				int ind = StdRandom.uniform(0, blockedCount);
				int x = (int) ((Point2D) blockedPoints.get(ind)).x();
				int y = (int) ((Point2D) blockedPoints.get(ind)).y();
				openedCount++;
				percolation.open(x, y);
				blockedPoints.remove(ind);
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