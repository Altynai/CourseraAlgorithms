import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Solver {
	private final static int MAX_MOVES = (1 << 10);
	private int moves = 0;
	private boolean isSolvable = false;
	private MinPQ<QueueNode> queue = null;
	private MinPQ<QueueNode> twinQueue = null;
	private List<QueueNode> path = null;

	private class SolverIterator implements Iterable<Board>, Iterator<Board> {
		private int currentIndex = 0;

		public SolverIterator() {
			if (path == null)
				currentIndex = 0;
			else
				currentIndex = path.size() - 1;
		}

		@Override
		public boolean hasNext() {
			return currentIndex >= 0;
		}

		@Override
		public Board next() {
			if (currentIndex < 0)
				throw new IndexOutOfBoundsException();
			return path.get(currentIndex--).board;
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

	private class QueueNode implements Comparable<QueueNode> {
		private Board board = null;
		private int moves = 0;
		private QueueNode previousNode = null;

		public QueueNode(Board board, int moves, QueueNode previousNode) {
			this.board = board;
			this.previousNode = previousNode;
			this.moves = moves;
		}

		@Override
		public int compareTo(QueueNode another) {
			return (moves + board.manhattan())
					- (another.moves + another.board.manhattan());
		}

		public boolean equals(Object y) { // does this QueueNode equal y?
			if (this == y)
				return true;
			if (y == null || y.getClass() != this.getClass())
				return false;
			QueueNode another = (QueueNode) y;
			return board.equals(another.board);
		}
	}

	private void findWayBack(QueueNode currentNode) {
		if (path == null)
			path = new ArrayList<QueueNode>();
		while (true) {
			path.add(currentNode);
			currentNode = currentNode.previousNode;
			if (currentNode == null)
				break;
		}
	}

	private QueueNode findSolution(Board initial) {
		queue.insert(new QueueNode(initial, 0, null));
		twinQueue.insert(new QueueNode(initial.twin(), 0, null));
		
		while (!queue.isEmpty() && !twinQueue.isEmpty()) {
			QueueNode current = queue.delMin();
			if (current.board.isGoal())
				return current;
			for (Board nextBoard : current.board.neighbors()) {
				QueueNode nextNode = new QueueNode(nextBoard,
						current.moves + 1, current);
				if (nextNode.equals(current.previousNode))
					continue;
				queue.insert(nextNode);
			}
			
			current = twinQueue.delMin();
			if (current.board.isGoal())
				return null;
			for (Board nextBoard : current.board.neighbors()) {
				QueueNode nextNode = new QueueNode(nextBoard,
						current.moves + 1, current);
				if (nextNode.equals(current.previousNode)) {
					continue;
				}
				twinQueue.insert(nextNode);
			}
		}
		return null;
	}

	public Solver(Board initial) { // find a solution to the initial board
									// (using the A* algorithm)
		int initSize = 1;
		for (int i = 1; initSize * i <= Solver.MAX_MOVES; i++)
			initSize = initSize * i;
		
		queue = new MinPQ<QueueNode>(initSize);
		twinQueue = new MinPQ<QueueNode>(initSize);
		QueueNode resultNode = findSolution(initial);
		if (resultNode == null) {
			isSolvable = false;
			moves = -1;
		} else {
			findWayBack(resultNode);
			isSolvable = true;
			moves = path.size() - 1;
		}
	}

	public boolean isSolvable() { // is the initial board solvable?
		return isSolvable;
	}

	public int moves() { // min number of moves to solve initial board; -1 if no
							// solution
		return isSolvable() ? moves : -1;
	}

	public Iterable<Board> solution() { // sequence of boards in a shortest
										// solution; null if no solution
		if (!isSolvable())
			return null;
		else
			return new SolverIterator();
	}

	public static void main(String[] args) { // solve a slider puzzle (given
												// below)
		// create initial board from file
		In in = new In(args[0]);
		int N = in.readInt();
		int[][] blocks = new int[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				blocks[i][j] = in.readInt();
		Board initial = new Board(blocks);
		
		// solve the puzzle
		Solver solver = new Solver(initial);
		StdOut.println(initial.manhattan());
		// print solution to standard output
		if (!solver.isSolvable())
			StdOut.println("No solution possible");
		else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution())
				StdOut.println(board);
		}
	}
}