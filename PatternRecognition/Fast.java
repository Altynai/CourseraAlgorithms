import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;

public class Fast {

	private static void swap(Point[] points, int i, int j) {
		Point swap = points[i];
		points[i] = points[j];
		points[j] = swap;
	}

	public static void main(String[] args) {
		Scanner stdin = null;
		try {
			stdin = new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int n = stdin.nextInt();
		int lineCount = 0;
		// point[i] belongs to which line
		ArrayList<HashSet<Integer>> lines = new ArrayList<HashSet<Integer>>();
		Point[] points = new Point[n];
		// index points
		TreeMap<Point, Integer> hashPoints = new TreeMap<Point, Integer>();
		
		for (int i = 0; i < n; i++) {
			lines.add(new HashSet<Integer>());
			int x = stdin.nextInt();
			int y = stdin.nextInt();
			points[i] = new Point(x, y);
			hashPoints.put(points[i], i);
		}

		StdDraw.setXscale(0, 32768);
		StdDraw.setYscale(0, 32768);

		for (int i = 0; i < n; i++) {
			swap(points, 0, i);
			Arrays.sort(points, 1, n, points[0].SLOPE_ORDER);
			int j = 1, sameSlope = 1, currentIndex = 1;
			while (currentIndex < n) {
				sameSlope = 1;
				j = currentIndex + 1;
				while (j < n) {
					if (points[0].slopeTo(points[j - 1]) != points[0]
							.slopeTo(points[j]))
						break;
					j++;
					sameSlope++;
				}
				if (sameSlope >= 3) {
					int tailId = hashPoints.get(points[currentIndex]);
					int headId = hashPoints.get(points[j - 1]);
					boolean hasCommonLine = false;
					for (Integer x : lines.get(headId)) {
						if (lines.get(tailId).contains(x)) {
							hasCommonLine = true;
							break;
						}
					}
					if (!hasCommonLine) {
						// print and draw
						Point[] temp = new Point[j - currentIndex + 1];
						lineCount++;
						for (int k = currentIndex; k < j; k++)
							temp[k - currentIndex] = points[k];
						temp[j - currentIndex] = points[0];
						Arrays.sort(temp);
						for (int k = 0; k < temp.length; k++) {
							int id = hashPoints.get(temp[k]);
							lines.get(id).add(lineCount);
							if (k != 0)
								StdOut.print(" -> ");
							StdOut.print(temp[k]);
							temp[k].draw();
							if (k + 1 < temp.length)
								temp[k].drawTo(temp[k + 1]);
						}
						StdOut.print("\n");
					}
				}
				currentIndex = j;
			}
		}
	}
}
