import java.util.Iterator;
import java.util.Vector;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root;
    private int size;
    private ThreadLocal<Vector<Point2D>> rangePoints;
    private ThreadLocal<RectHV> query;

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

        query = new ThreadLocal<RectHV>() {
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

        return new Point2D(0, 0);
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        System.out.println("Running tests...");
        testContains();
        testRangeSearch();
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

    private void range(Node parent, boolean isVertDiv) {
        if (parent == null) {
            return;
        }

        RectHV queryRect = query.get();
        if (queryRect.intersects(parent.rect)) {
            if (queryRect.contains(parent.p)) {
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
            Node root = new Node();
            root.p = point;
            root.rect = new RectHV(0d, 0d, 1d, 1d);
            root.lb = null;
            root.rt = null;
            size++;
            return root;
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
        } else if (cmp1 > cmp2) {
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
