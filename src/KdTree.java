import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root;
    private int size;
    private ThreadLocal<Vector<Point2D>> rangePoints;
    private ThreadLocal<RectHV> queryRect;
    private ThreadLocal<Point2D> closestSoFar;
    private ThreadLocal<Point2D> queryPoint;
    private ThreadLocal<Double> distanceBest;

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.NullPointerException();
        }

        root = insert(root, p, true);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new java.lang.NullPointerException();
        }

        return contains(root, p, true);
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.show(0);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        draw(root, true);
        StdDraw.show();
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.NullPointerException();
        }

        rangePoints = new ThreadLocal<Vector<Point2D>>() {
            @Override
            protected Vector<Point2D> initialValue() {
                return new Vector<Point2D>();
            }
        };

        queryRect = new ThreadLocal<RectHV>() {
            @Override
            protected RectHV initialValue() {
                return rect;
            }
        };

        range(root, true);

        return rangePoints.get();
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new java.lang.NullPointerException();
        }

        if (root == null) {
            return null;
        }
        closestSoFar = new ThreadLocal<Point2D>() {
            @Override
            protected Point2D initialValue() {
                return root.p;
            }
        };

        queryPoint = new ThreadLocal<Point2D>() {
            @Override
            protected Point2D initialValue() {
                return p;
            }
        };

        distanceBest = new ThreadLocal<Double>() {
            @Override
            protected Double initialValue() {
                return Double.MAX_VALUE;
            }
        };

        nearest(root, true);

        return closestSoFar.get();
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        System.out.println("Running tests...");
        testContains();
        testRangeSearch();
        testSize1();
        testSize2();
        testCircle();
        testDuplicate();
    }

    private static void testDuplicate() {
        System.out.println("testDuplicate");

        KdTree tree = new KdTree();

        double x = 0.65743d;
        double y = 0.95637d;

        tree.insert(new Point2D(x, y));
        tree.insert(new Point2D(x, y));
        int size = tree.size();
        assert size == 1;

        boolean isContains = tree.contains(new Point2D(x, y));
        assert isContains;
    }

    private static void testCircle() {
        System.out.println("testCircle");

        String path = "/run/media/bert/280AC22E0AF59495/coursera/algorithms/1/assignments/5/doc/kdtree/circle10.txt";
        KdTree kdtree = loadFile(path);

        Point2D actual = kdtree.nearest(new Point2D(0.81d, 0.30d));
        Point2D expected = new Point2D(0.975528d, 0.345492d);

        assert expected.equals(actual);

        // System.out.println(actual);
    }

    private static void testSize2() {
        System.out.println("testSize2");
        int numPoints = 100000;
        int dim = 100000;

        Random rnd = new Random();
        HashSet<Point2D> set = new HashSet<Point2D>();

        KdTree tree = new KdTree();

        for (int i = 0; i < numPoints; i++) {
            boolean isUnique = false;

            while (!isUnique) {
                int gridX = rnd.nextInt(Integer.MAX_VALUE) % dim;
                int gridY = rnd.nextInt(Integer.MAX_VALUE) % dim;
                // System.out.println(gridX + " " + gridY);
                double x = (double) gridX / (double) dim;
                double y = (double) gridY / (double) dim;

                Point2D p = new Point2D(x, y);

                isUnique = !set.contains(p);

                if (isUnique) {
                    // System.out.println(p+" "+(i+1)+" "+tree.contains(p));
                    set.add(p);
                    tree.insert(p);
                    // System.out.println("size= "+tree.size());
                    // if (tree.size() != i+1) {
                    // tree.contains(p);
                    // tree.insert(p);
                    // }
                    assert tree.size() == (i + 1);
                }
            }
        }

        assert tree.size() == numPoints;
    }

    private static void testSize1() {
        System.out.println("testSize1");
        KdTree tree = new KdTree();

        int size;

        size = tree.size();
        assert size == 0;

        tree.insert(new Point2D(0.5d, 0.5d));
        size = tree.size();
        assert size == 1;

        tree.insert(new Point2D(0.25d, 0.5d));
        tree.insert(new Point2D(0.75d, 0.5d));
        size = tree.size();
        assert size == 3;

    }

    private static void testRangeSearch() {
        System.out.println("testRangeSearch");
        KdTree tree = new KdTree();

        Iterable<Point2D> iter = tree.range(new RectHV(0d, 0d, 1d, 1d));
        assert !iter.iterator().hasNext();

        tree.insert(new Point2D(0.5d, 0.5d));
        iter = tree.range(new RectHV(0d, 0d, 1d, 1d));
        assert iter.iterator().hasNext();

        tree.insert(new Point2D(0.25d, 0.5d));
        tree.insert(new Point2D(0.75d, 0.5d));

        iter = tree.range(new RectHV(0d, 0d, 1d, 1d));
        int count = 0;
        Iterator<Point2D> i = iter.iterator();
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assert count == 3;

        iter = tree.range(new RectHV(0d, 0.1d, 0.2d, 0.9d));
        assert !iter.iterator().hasNext();

        iter = tree.range(new RectHV(0.6d, 0.4d, 0.78d, 0.6d));
        count = 0;
        i = iter.iterator();
        while (i.hasNext()) {
            count++;
            i.next();
        }
        assert count == 1;

    }

    private static void testContains() {
        System.out.println("testContains");
        KdTree tree = new KdTree();

        tree.insert(new Point2D(0.5d, 0.5d));
        boolean isContains = tree.contains(new Point2D(0.5d, 0.5d));
        assert isContains;

        tree.insert(new Point2D(0.25d, 0.5d));
        isContains = tree.contains(new Point2D(0.25d, 0.5d));
        assert isContains;

        tree.insert(new Point2D(0.75d, 0.5d));
        isContains = tree.contains(new Point2D(0.75d, 0.5d));
        assert isContains;

        isContains = tree.contains(new Point2D(0.0d, 0.0d));
        assert !isContains;

        isContains = tree.contains(new Point2D(0.3d, 0.3d));
        assert !isContains;
    }

    private static KdTree loadFile(String filename) {
        KdTree kdtree = new KdTree();
        In in = new In(filename);

        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        return kdtree;
    }

    private void nearest(Node parent, boolean isVertDiv) {
        if (parent == null) {
            return;
        }

        Point2D query = queryPoint.get();
        double distBest = distanceBest.get();

        // if the closest point discovered so far
        // is closer than the distance between
        // the query point and the rectangle
        // corresponding to a node,
        // there is no need to explore that node
        // (or its subtrees).
        // the closest point found while exploring
        // the previous subtree may enable pruning
        // of the next subtree.
        if (parent.rect.distanceSquaredTo(query) > distBest) {
            return;
        }

        double distNow = query.distanceSquaredTo(parent.p);

        if (distNow < distBest) {
            distanceBest.set(distNow);
            closestSoFar.set(parent.p);
        }

        // first, go towards query point
        // choose the subtree that is on
        // the same side of the splitting line
        // as the query point as the first subtree to explore
        Node first = parent.lb;
        Node second = parent.rt;
        if (isVertDiv) {
            if (query.x() > parent.p.x()) {
                // query point on right on vertical division => go right first
                first = parent.rt;
                second = parent.lb;
            }
        } else {
            if (query.y() > parent.p.y()) {
                // query point on top of horizontal division => go top first
                first = parent.rt;
                second = parent.lb;
            }
        }

        nearest(first, !isVertDiv);
        nearest(second, !isVertDiv);

    }

    private void range(Node parent, boolean isVertDiv) {
        if (parent == null) {
            return;
        }

        RectHV query = queryRect.get();
        if (query.intersects(parent.rect)) {
            if (query.contains(parent.p)) {
                rangePoints.get().add(parent.p);
            }
            range(parent.lb, !isVertDiv);
            range(parent.rt, !isVertDiv);
        }

    }

    private void draw(Node x, boolean isVertDiv) {
        if (x == null) {
            return;
        }

        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        x.p.draw();

        if (isVertDiv) {
            StdDraw.setPenRadius();
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(x.p.x(), x.rect.ymin(), x.p.x(), x.rect.ymax());
        } else {
            StdDraw.setPenRadius();
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(x.rect.xmin(), x.p.y(), x.rect.xmax(), x.p.y());
        }

        draw(x.lb, !isVertDiv);
        draw(x.rt, !isVertDiv);

    }

    private Node insert(Node parent, Point2D point, boolean isVertDiv) {

        if (parent == null) {
            Node newNode = new Node();
            newNode.p = point;
            newNode.rect = new RectHV(0d, 0d, 1d, 1d);
            newNode.lb = null;
            newNode.rt = null;
            size++;
            return newNode;
        }

        if (parent.p.equals(point)) {
            return parent;
        }

        double cmp1 = point.x();
        double cmp2 = parent.p.x();
        if (!isVertDiv) {
            cmp1 = point.y();
            cmp2 = parent.p.y();
        }

        if (cmp1 < cmp2) {
            // left or bottom
            boolean isLBNull = parent.lb == null;
            parent.lb = insert(parent.lb, point, !isVertDiv);
            if (isLBNull) {
                if (isVertDiv) {
                    // parent is vertical division
                    // on the left of the vertical division
                    parent.lb.rect = new RectHV(parent.rect.xmin(),
                            parent.rect.ymin(), parent.p.x(),
                            parent.rect.ymax());
                } else {
                    // parent is horizontal division
                    // on the bottom of the horizontal division
                    parent.lb.rect = new RectHV(parent.rect.xmin(),
                            parent.rect.ymin(), parent.rect.xmax(),
                            parent.p.y());
                }
                // System.out.println(parent.lb.rect);
            }
        } else if (cmp1 >= cmp2) {
            boolean isRTNull = parent.rt == null;
            parent.rt = insert(parent.rt, point, !isVertDiv);

            if (isRTNull) {
                if (isVertDiv) {
                    // parent is vertical division
                    // on the right of the vertical division
                    parent.rt.rect = new RectHV(parent.p.x(),
                            parent.rect.ymin(), parent.rect.xmax(),
                            parent.rect.ymax());
                } else {
                    // parent is horizontal division
                    // on the top of the horizontal division
                    parent.rt.rect = new RectHV(parent.rect.xmin(),
                            parent.p.y(), parent.rect.xmax(),
                            parent.rect.ymax());
                }
                // System.out.println(parent.rt.rect);
            }
        }

        return parent;
    }

    private boolean contains(Node x, Point2D point, boolean isVertDiv) {

        if (x == null) {
            return false;
        }

        if (x.p.equals(point)) {
            return true;
        }

        double cmp1 = point.x();
        double cmp2 = x.p.x();
        if (!isVertDiv) {
            cmp1 = point.y();
            cmp2 = x.p.y();
        }

        if (cmp1 < cmp2) {
            return contains(x.lb, point, !isVertDiv);
        } else {
            return contains(x.rt, point, !isVertDiv);
        }

    }

    private static class Node {
        // the point
        private Point2D p;

        // the axis-aligned rectangle corresponding to this node
        private RectHV rect;

        // the left/bottom subtree
        private Node lb;

        // the right/top subtree
        private Node rt;
    }

}
