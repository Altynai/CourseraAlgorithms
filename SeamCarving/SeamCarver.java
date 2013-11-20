import java.awt.Color;

public class SeamCarver {
	private Picture picture = null;
	private int[] toX = null;
	private int[] toY = null;
	private int w = 0;
	private int h = 0;

	public SeamCarver(Picture picture) {
		this.picture = picture;
		this.w = picture.width();
		this.h = picture.height();
		toX = new int[w * h];
		toY = new int[w * h];

		int x = 0, y = 0;
		for (int i = 0; i < w * h; i++) {
			toX[i] = x;
			toY[i] = y;
			x++;
			if (x == w) {
				y++;
				x = 0;
			}
		}
	}

	public Picture picture() { // current picture
		return this.picture;
	}

	public int width() { // width of current picture
		return this.w;
	}

	public int height() { // height of current picture
		return this.h;
	}

	public double energy(int x, int y) { // energy of pixel at column x and row
											// y in current picture
		if (!checkBounds(x, y))
			throw new IndexOutOfBoundsException();

		if (isAtBorder(x, y))
			return 195075.0;

		Color left = getColor(x - 1, y);
		Color right = getColor(x + 1, y);
		double dx = square(left.getRed() - right.getRed());
		dx += square(left.getBlue() - right.getBlue());
		dx += square(left.getGreen() - right.getGreen());

		Color up = getColor(x, y - 1);
		Color down = getColor(x, y + 1);
		double dy = square(up.getRed() - down.getRed());
		dy += square(up.getBlue() - down.getBlue());
		dy += square(up.getGreen() - down.getGreen());

		return dx + dy;
	}

	public int[] findHorizontalSeam() { // sequence of indices for horizontal
										// seam in current picture
		int V = this.w * this.h + 2;
		int start = V - 2;
		int end = V - 1;
		EdgeWeightedDigraph graph = buildHorizontalGraph(V, start, end);
		DijkstraSP spt = new DijkstraSP(graph, start);
		Stack<DirectedEdge> path = (Stack<DirectedEdge>) spt.pathTo(end);

		int[] y = new int[this.w];
		path.pop();
		int index = 0;
		while (index < this.w && !path.isEmpty()) {
			DirectedEdge edge = path.pop();
			y[index] = back2Y(edge.from());
			index++;
		}
		return y;
	}

	public int[] findVerticalSeam() { // sequence of indices for vertical seam
										// in current picture
		int V = this.w * this.h + 2;
		int start = V - 2;
		int end = V - 1;
		EdgeWeightedDigraph graph = buildVerticalGraph(V, start, end);
		DijkstraSP spt = new DijkstraSP(graph, start);
		Stack<DirectedEdge> path = (Stack<DirectedEdge>) spt.pathTo(end);

		int[] x = new int[this.h];
		DirectedEdge edge = path.pop();
		int index = 0;
		while (index < this.h && !path.isEmpty()) {
			edge = path.pop();
			x[index] = back2X(edge.from());
			index++;
		}
		return x;
	}

	public void removeHorizontalSeam(int[] a) { // remove horizontal seam from
												// current picture
		checkArray(a, this.w);
		Picture newPicture = new Picture(w, h - 1);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				if (y == a[x])
					continue;
				else if (y < a[x])
					newPicture.set(x, y, picture.get(x, y));
				else
					newPicture.set(x, y - 1, picture.get(x, y));
			}
		}
		this.picture = newPicture;
		this.w = newPicture.width();
		this.h = newPicture.height();
	}

	public void removeVerticalSeam(int[] a) { // remove vertical seam from
												// current picture
		checkArray(a, this.h);
		Picture newPicture = new Picture(w - 1, h);
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				if (x == a[y])
					continue;
				else if (x < a[y])
					newPicture.set(x, y, picture.get(x, y));
				else
					newPicture.set(x - 1, y, picture.get(x, y));
			}
		}
		this.picture = newPicture;
		this.w = newPicture.width();
		this.h = newPicture.height();
	}

	private boolean checkBounds(int x, int y) {
		return x >= 0 && x < this.w && y >= 0 && y < this.h;
	}

	private boolean isAtBorder(int x, int y) {
		return x == 0 || x == this.w - 1 || y == 0 || y == this.h - 1;
	}

	private Color getColor(int x, int y) {
		return picture.get(x, y);
	}

	private double square(double x) {
		return x * x;
	}

	private int id(int x, int y) {
		return y * this.w + x;
	}

	private EdgeWeightedDigraph buildHorizontalGraph(int V, int start, int end) {
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(V);

		// leftest column connects to the `start` point
		int x = 0, y = 0;
		for (y = 0; y < this.h; y++)
			graph.addEdge(new DirectedEdge(start, id(x, y), energy(x, y)));

		// normal edges
		for (x = 0; x < this.w; x++) {
			for (y = 0; y < this.h; y++) {
				for (int d = -1; d <= 1; d++) {
					if (checkBounds(x + 1, y + d)) {
						int u = id(x, y);
						int v = id(x + 1, y + d);
						double w = energy(x + 1, y + d);
						graph.addEdge(new DirectedEdge(u, v, w));
					}
				}
			}
		}

		// rightest column connects to the `end` point
		x = this.w - 1;
		for (y = 0; y < this.h; y++) {
			graph.addEdge(new DirectedEdge(id(x, y), end, 0));
		}

		return graph;
	}

	private EdgeWeightedDigraph buildVerticalGraph(int V, int start, int end) {
		EdgeWeightedDigraph graph = new EdgeWeightedDigraph(V);

		// upest column connects to the `start` point
		int x = 0, y = 0;
		for (x = 0; x < this.w; x++)
			graph.addEdge(new DirectedEdge(start, id(x, y), 0));

		// normal edges
		for (x = 0; x < this.w; x++) {
			for (y = 0; y < this.h; y++) {
				for (int d = -1; d <= 1; d++) {
					if (checkBounds(x + d, y + 1)) {
						int u = id(x, y);
						int v = id(x + d, y + 1);
						double w = energy(x + d, y + 1);
						graph.addEdge(new DirectedEdge(u, v, w));
					}
				}
			}
		}

		// downest column connects to the `end` point
		y = this.h - 1;
		for (x = 0; x < this.w; x++) {
			graph.addEdge(new DirectedEdge(id(x, y), end, energy(x, y)));
			// StdOut.printf("width=%d (%d,%d)=%d\n", this.w, x, y, id(x, y));
		}

		return graph;
	}

	private int back2Y(int id) {
		return toY[id];
	}

	private int back2X(int id) {
		return toX[id];
	}

	private void checkArray(int[] a, int length) {
		if (this.w <= 1 || this.h <= 1)
			throw new IllegalArgumentException();
		if (a.length != length)
			throw new IllegalArgumentException();
		for (int i = 1; i < length; i++) {
			if (Math.abs(a[i] - a[i - 1]) > 1)
				throw new IllegalArgumentException();
		}
		for (int i = 0; i < length; i++) {
			if (length == this.w && (a[i] < 0 || a[i] >= this.h))
				throw new IllegalArgumentException();
			if (length == this.h && (a[i] < 0 || a[i] >= this.w))
				throw new IllegalArgumentException();
		}
	}

	public static void main(String[] args) {
		String file = "6x5.png";
		SeamCarver seam = new SeamCarver(new Picture(file));
		int[] a = seam.findVerticalSeam();
		seam.removeVerticalSeam(a);
		seam.energy(5, 3);
	}
}