public class KdTree {
	private Node root = null;
	private int size = 0;
	private Point2D nearest = null;
	private double distance = 0;

	public KdTree() { // construct an empty set of points
		this.size = 0;
		this.root = null;
	}

	public boolean isEmpty() { // is the set empty?
		return size == 0;
	}

	public int size() { // number of points in the set
		return size;
	}

	public void insert(Point2D p) { // add the point p to the set (if it is not
									// already in the set)
		if (!contains(p))
			root = insert(root, p, 0);
	}

	public boolean contains(Point2D p) { // does the set contain the point p?
		return contains(root, p, 0);
	}

	public void draw() { // draw all of the points to standard draw

	}

	public Iterable<Point2D> range(RectHV rect) { // all points in the set that
													// are inside the rectangle
		Queue<Point2D> queue = new Queue<Point2D>();
		queue = range(root, rect, 0, queue);
		return queue;
	}

	public Point2D nearest(Point2D p) { // a nearest neighbor in the set to p;
										// null if set is empty
		if (isEmpty())
			return null;
		nearest = null;
		distance = Double.POSITIVE_INFINITY;
		nearest(root, p, 0);
		return nearest;
	}

	private class Node {
		private Point2D point = null;
		private Node left = null;
		private Node right = null;

		public Node(Point2D p) {
			this.point = p;
		}
	}

	private Node insert(Node root, Point2D p, int level) {
		if (root == null) {
			size++;
			return new Node(p);
		}

		Point2D currentPoint = root.point;
		if (level % 2 == 0) {
			if (p.x() <= currentPoint.x())
				root.left = insert(root.left, p, level + 1);
			else
				root.right = insert(root.right, p, level + 1);
		} else {
			if (p.y() <= currentPoint.y())
				root.left = insert(root.left, p, level + 1);
			else
				root.right = insert(root.right, p, level + 1);
		}
		return root;
	}

	private boolean contains(Node root, Point2D p, int level) {
		if (root == null)
			return false;

		Point2D currentPoint = root.point;
		if (currentPoint.equals(p))
			return true;

		if (level % 2 == 0) {
			if (p.x() <= currentPoint.x())
				return contains(root.left, p, level + 1);
			else
				return contains(root.right, p, level + 1);
		} else {
			if (p.y() <= currentPoint.y())
				return contains(root.left, p, level + 1);
			else
				return contains(root.right, p, level + 1);
		}
	}

	private Queue<Point2D> range(Node root, RectHV rect, int level,
			Queue<Point2D> queue) {
		if (root == null)
			return queue;

		Point2D currentPoint = root.point;
		if (rect.contains(currentPoint)) {
			queue.enqueue(currentPoint);
		}

		if (level % 2 == 0) {
			// go left
			if (rect.xmin() <= currentPoint.x())
				queue = range(root.left, rect, level + 1, queue);
			// go right
			if (rect.xmax() > currentPoint.x())
				queue = range(root.right, rect, level + 1, queue);
		} else {
			// go down
			if (rect.ymin() <= currentPoint.y())
				queue = range(root.left, rect, level + 1, queue);
			// go up
			if (rect.ymax() > currentPoint.y())
				queue = range(root.right, rect, level + 1, queue);
		}
		return queue;
	}

	private void nearest(Node root, Point2D p, int level) {
		if (root == null)
			return;
		Point2D currentPoint = root.point;
		double currentDistance = p.distanceTo(currentPoint);
		if (currentDistance < distance) {
			nearest = currentPoint;
			distance = currentDistance;
		}

		if (level % 2 == 0) {
			// 1. go left
			// 2. go right
			if (p.x() <= currentPoint.x()) {
				nearest(root.left, p, level + 1);
				if (Math.abs(p.x() - currentPoint.x()) < distance)
					nearest(root.right, p, level + 1);
			}
			// 1. go right
			// 2. go left
			else {
				nearest(root.right, p, level + 1);
				if (Math.abs(p.x() - currentPoint.x()) < distance)
					nearest(root.left, p, level + 1);
			}
		} else {
			// 1. go down
			// 2. go up
			if (p.y() <= currentPoint.y()) {
				nearest(root.left, p, level + 1);
				if (Math.abs(p.y() - currentPoint.y()) < distance)
					nearest(root.right, p, level + 1);
			}
			// 1. go up
			// 2. go down
			else {
				nearest(root.right, p, level + 1);
				if (Math.abs(p.y() - currentPoint.y()) < distance)
					nearest(root.left, p, level + 1);
			}
		}
	}

	public static void main(String[] args) {
		String filename = "in.txt";
		In in = new In(filename);
		// initialize the data structures with N points from standard input
		KdTree kdtree = new KdTree();
		while (!in.isEmpty()) {
			double x = in.readDouble();
			double y = in.readDouble();
			kdtree.insert(new Point2D(x, y));
		}
		for (Point2D point : kdtree.range(new RectHV(0, 0, 100, 100)))
			StdOut.println(point);
	}
}
