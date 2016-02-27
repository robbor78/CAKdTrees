import java.util.Vector;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root;
    private int size = 0;

    // construct an empty set of points
    public KdTree() {
        root = null;
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

        return new Vector<Point2D>();
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
        testContains();
    }

    private static void testContains() {
        KdTree tree = new KdTree();

        tree.insert(new Point2D(0.5d, 0.5d));
        
        boolean isContains = tree.contains(new Point2D(0.5d, 0.5d));
        
        assert isContains;
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

    private Node insert(Node x, Point2D point, boolean isVertDiv) {

        if (x == null) {
            Node root = new Node();
            root.p = point;
            root.rect = new RectHV(0d, 0d, 1d, 1d);
            root.lb = null;
            root.rt = null;
            size++;
            return root;
        }

        double cmp1 = point.x();
        double cmp2 = x.p.x();
        if (!isVertDiv) {
            cmp1 = point.y();
            cmp2 = x.p.y();
        }

        if (cmp1 < cmp2) {
            boolean isLBNull = x.lb == null;
            x.lb = insert(x.lb, point, !isVertDiv);
            if (isLBNull) {
                if (isVertDiv) {
                    x.lb.rect = new RectHV(x.rect.xmin(), x.rect.ymax(),
                            x.p.x(), x.rect.ymin());
                } else {
                    x.lb.rect = new RectHV(x.rect.xmin(), x.lb.p.y(), x.p.x(),
                            x.rect.ymin());
                }
            }
        } else if (cmp1 > cmp2) {
            boolean isRTNull = x.lb == null;
            x.rt = insert(x.rt, point, !isVertDiv);
            if (isRTNull) {
                if (isVertDiv) {
                    x.rt.rect = new RectHV(x.p.x(), x.rect.ymax(),
                            x.rect.xmax(), x.rect.ymin());
                } else {
                    x.rt.rect = new RectHV(x.rect.xmin(), x.rect.ymax(),
                            x.p.x(), x.rt.p.y());
                }
            }
        }

        return x;
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
