public class PointSET {
	private RedBlackBST<Point2D, Integer> pointSet = null;

	public PointSET() { // construct an empty set of points
		pointSet = new RedBlackBST<Point2D, Integer>();
	}

	public boolean isEmpty() { // is the set empty?
		return pointSet.size() == 0;
	}

	public int size() { // number of points in the set
		return pointSet.size();
	}

	public void insert(Point2D p) { // add the point p to the set (if it is not
									// already in the set)
		if (!pointSet.contains(p))
			pointSet.put(p, Integer.valueOf(0));
	}

	public boolean contains(Point2D p) { // does the set contain the point p?
		return pointSet.contains(p);
	}

	public void draw() { // draw all of the points to standard draw
		for (Point2D point : pointSet.keys())
			point.draw();
	}

	public Iterable<Point2D> range(RectHV rect) { // all points in the set that
													// are inside the rectangle
		Queue<Point2D> queue = new Queue<Point2D>();
		for (Point2D point : pointSet.keys()) {
			if (rect.contains(point))
				queue.enqueue(point);
		}
		return queue;
	}

	public Point2D nearest(Point2D p) { // a nearest neighbor in the set to p;
										// null if set is empty
		if (pointSet.isEmpty())
			return null;
		double distance = Double.POSITIVE_INFINITY;
		Point2D nearestPoint = null;
		for (Point2D point : pointSet.keys()) {
			if (p.distanceTo(point) < distance) {
				nearestPoint = point;
				distance = p.distanceTo(point);
			}
		}
		return nearestPoint;
	}
}
