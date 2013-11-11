import java.util.HashSet;

public class SAP {
	private Digraph graph = null;

	private void checkArgs(Iterable<Integer> x, Iterable<Integer> y) {
		for (int v : x) {
			for (int w : y) {
				if (v < 0 || w < 0 || v >= graph.V() || w >= graph.V())
					throw new IndexOutOfBoundsException();
			}
		}
	}

	private void checkArgs(int v, int w) {
		if (v < 0 || w < 0 || v >= graph.V() || w >= graph.V())
			throw new IndexOutOfBoundsException();
	}

	private int[] getShortestPath(Iterable<Integer> w) {
		int[] d = new int[graph.V()];
		for (int i = 0; i < graph.V(); i++)
			d[i] = Integer.MAX_VALUE;
		Queue<Integer> queue = new Queue<Integer>();
		for (int u : w) {
			d[u] = 0;
			queue.enqueue(u);
		}
		while (!queue.isEmpty()) {
			int u = queue.dequeue();
			for (int v : graph.adj(u)) {
				if (d[v] > d[u] + 1) {
					d[v] = d[u] + 1;
					queue.enqueue(v);
				}
			}
		}
		return d;
	}

	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		this.graph = G;
	}

	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		checkArgs(v, w);
		HashSet<Integer> x = new HashSet<Integer>();
		x.add(v);
		HashSet<Integer> y = new HashSet<Integer>();
		y.add(w);
		return length(x, y);
	}

	// a common ancestor of v and w that participates in a shortest ancestral
	// path; -1 if no such path
	public int ancestor(int v, int w) {
		HashSet<Integer> x = new HashSet<Integer>();
		x.add(v);
		HashSet<Integer> y = new HashSet<Integer>();
		y.add(w);
		return ancestor(x, y);
	}

	// length of shortest ancestral path between any vertex in v and any vertex
	// in w; -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		checkArgs(v, w);
		int dv[] = getShortestPath(v);
		int dw[] = getShortestPath(w);
		int minLength = Integer.MAX_VALUE;
		for (int i = 0; i < graph.V(); i++) {
			if (dv[i] == Integer.MAX_VALUE || dw[i] == Integer.MAX_VALUE)
				continue;
			if (minLength > dv[i] + dw[i])
				minLength = dv[i] + dw[i];
		}
		return minLength == Integer.MAX_VALUE ? -1 : minLength;
	}

	// a common ancestor that participates in shortest ancestral path; -1 if no
	// such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		int dv[] = getShortestPath(v);
		int dw[] = getShortestPath(w);
		int minLength = Integer.MAX_VALUE;
		int ancestor = Integer.MAX_VALUE;
		for (int i = 0; i < graph.V(); i++) {
			if (dv[i] == Integer.MAX_VALUE || dw[i] == Integer.MAX_VALUE)
				continue;
			if (minLength > dv[i] + dw[i]) {
				minLength = dv[i] + dw[i];
				ancestor = i;
			}
		}
		return ancestor == Integer.MAX_VALUE ? -1 : ancestor;
	}

	// for unit testing of this class (such as the one below)
	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		while (!StdIn.isEmpty()) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}
	}
}
