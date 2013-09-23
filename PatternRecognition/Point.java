import java.util.Comparator;

public class Point implements Comparable<Point> {

	// compare points by slope
	public final Comparator<Point> SLOPE_ORDER; // YOUR DEFINITION HERE

	private final int x; // x coordinate
	private final int y; // y coordinate

	// create the point (x, y)
	public Point(int x, int y) {
		/* DO NOT MODIFY */
		this.x = x;
		this.y = y;
		this.SLOPE_ORDER = new BySlope(this);
	}

	// plot this point to standard drawing
	public void draw() {
		/* DO NOT MODIFY */
		StdDraw.point(x, y);
	}

	// draw line between this point and that point to standard drawing
	public void drawTo(Point that) {
		/* DO NOT MODIFY */
		StdDraw.line(this.x, this.y, that.x, that.y);
	}

	// slope between this point and that point
	public double slopeTo(Point that) {
		/* YOUR CODE HERE */
		if (that == null)
			throw new NullPointerException();

		double dy = that.y - this.y;
		double dx = that.x - this.x;

		if (that.x == this.x)
			return that.y != this.y ? Double.POSITIVE_INFINITY
					: Double.NEGATIVE_INFINITY;
		else {
			double k = dy / dx;
			if (Math.abs(k) == Math.abs(0.0))
				return Math.abs(k);
			else
				return k;
		}
	}

	// is this point lexicographically smaller than that one?
	// comparing y-coordinates and breaking ties by x-coordinates
	public int compareTo(Point that) {
		/* YOUR CODE HERE */
		if (this.y == that.y) {
			if (this.x == that.x)
				return 0;
			else
				return this.x < that.x ? -1 : 1;
		} else {
			return this.y < that.y ? -1 : 1;
		}
	}

	// return string representation of this point
	public String toString() {
		/* DO NOT MODIFY */
		return "(" + x + ", " + y + ")";
	}

	private static class BySlope implements Comparator<Point> {
		private Point pointBase = null;

		public BySlope(Point pointBase) {
			this.pointBase = pointBase;
		}

		@Override
		public int compare(Point o1, Point o2) {
			double slope1 = pointBase.slopeTo(o1);
			double slope2 = pointBase.slopeTo(o2);
			if (slope1 == slope2)
				return 0;
			else {
				if (slope1 == Double.POSITIVE_INFINITY
						|| slope2 == Double.NEGATIVE_INFINITY)
					return 1;
				else if (slope2 == Double.POSITIVE_INFINITY
						|| slope1 == Double.NEGATIVE_INFINITY)
					return -1;
				else
					return slope1 < slope2 ? -1 : 1;
			}
		}
	}

	// unit test
	public static void main(String[] args) {
		/* YOUR CODE HERE */
		Point a = new Point(0, 0);
		Point b = new Point(1, 1);
		StdOut.print(a.slopeTo(b));
	}
}