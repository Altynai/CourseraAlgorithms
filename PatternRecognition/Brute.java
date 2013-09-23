import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Brute {

	public static void main(String[] args) {
		Scanner stdin = null;
		try {
			stdin = new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int n = stdin.nextInt();
		Point[] points = new Point[n];
		boolean [] drawed = new boolean[n];
		for (int i = 0; i < n; i++) {
			drawed[i] = false;
			int x = stdin.nextInt();
			int y = stdin.nextInt();
			points[i] = new Point(x, y);
		}
		Arrays.sort(points);

		StdDraw.setXscale(0, 32768);
		StdDraw.setYscale(0, 32768);

		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				for (int k = j + 1; k < n; k++) {
					for (int l = k + 1; l < n; l++) {
						double slopej = points[i].slopeTo(points[j]);
						double slopek = points[i].slopeTo(points[k]);
						double slopel = points[i].slopeTo(points[l]);
						if (slopej == slopek && slopek == slopel) {
							StdOut.printf("%s -> %s -> %s -> %s\n", points[i],
									points[j], points[k], points[l]);
							if (!drawed[i]) {
								drawed[i] = true;
								points[i].draw();
								points[i].drawTo(points[j]);
							}
							if (!drawed[j]) {
								drawed[j] = true;
								points[j].draw();
								points[j].drawTo(points[k]);
							}
							if (!drawed[k]) {
								drawed[k] = true;
								points[k].draw();
								points[k].drawTo(points[l]);
							}
						}
					}
				}
			}
		}
	}
}