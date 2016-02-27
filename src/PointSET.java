import java.util.TreeSet;
import java.util.Vector;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

    private TreeSet<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new TreeSet<Point2D>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.NullPointerException();
        }
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new java.lang.NullPointerException();
        }
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.show(0);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        for (Point2D p : set) {
            p.draw();
        }
        StdDraw.show();
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.NullPointerException();
        }

        Vector<Point2D> points = new Vector<Point2D>();
        for (Point2D p : set) {
            if (rect.distanceSquaredTo(p) == 0) {
                points.add(p);
            }
        }
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D query) {
        if (query == null) {
            throw new java.lang.NullPointerException();
        }

        Point2D nearest = null;
        double bestDist = Double.MAX_VALUE;
        for (Point2D p : set) {
            double dist = query.distanceSquaredTo(p);
            if (dist < bestDist) {
                bestDist = dist;
                nearest = p;
            }
        }
        return nearest;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {

    }
}
