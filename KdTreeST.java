/* *****************************************************************************
 *  Name:    Joshua Drossman
 *  NetID:   drossman
 *  Precept: P02
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description:  A mutable data type that uses a 2d-tree to implement the same
 *                API as PointST. The result is a BST with points in the nodes,
 *                using the x- and y-coordinates of the points as keys in
 *                strictly alternating sequence, starting with the x-coordinates.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class KdTreeST<Value> {

    // indicates an x-coordinate comparison, horizontal otherwise
    private static final int VERTICAL = 1;
    // size of the ST
    private int size;
    // root node of the ST
    private Node root;
    // rect of the root node (x-y plane)
    private final RectHV xyplane = new RectHV(Double.NEGATIVE_INFINITY,
                                              Double.NEGATIVE_INFINITY,
                                              Double.POSITIVE_INFINITY,
                                              Double.POSITIVE_INFINITY);

    // construct an empty symbol table of points
    public KdTreeST() {
        root = null;
        size = 0;
    }

    private class Node {
        // the point
        private final Point2D p;
        // the symbol table maps the point to this value
        private Value value;
        // the axis-aligned rectangle corresponding to this node
        private final RectHV rect;
        // the left/bottom subtree
        private Node lb;
        // the right/top subtree
        private Node rt;

        // construct a node with point p, value value, and rect rect
        private Node(Point2D p, Value value, RectHV rect) {
            this.p = p;
            this.value = value;
            this.rect = rect;
            this.lb = null;
            this.rt = null;
        }
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points
    public int size() {
        return size;
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null)
            throw new IllegalArgumentException("argument to put() is null");
        root = put(root, p, val, VERTICAL, xyplane);
    }

    // helper method for put, determines node placement of newest point,
    // alternating comparison on each level between x- and y-coordinates,
    // as well as node rect, based on its placement in the tree
    private Node put(Node x, Point2D point, Value val, int orientation, RectHV rect) {
        if (x == null) {
            size++;
            return new Node(point, val, rect);
        }

        int cmp = compare(point, x.p, orientation);
        RectHV newRect;

        // point to be added is to the left/below the current comparison point
        if (cmp < 0) {
            if (orientation == VERTICAL)
                newRect = new RectHV(x.rect.xmin(), x.rect.ymin(), x.p.x(),
                                     x.rect.ymax());
            else
                newRect = new RectHV(x.rect.xmin(), x.rect.ymin(), x.rect.xmax(),
                                     x.p.y());

            x.lb = put(x.lb, point, val, orientation * -1, newRect);
        }
        // point to be added is to the right/above the current comparison point
        else if (cmp > 0 || !x.p.equals(point)) {
            if (orientation == VERTICAL)
                newRect = new RectHV(x.p.x(), x.rect.ymin(), x.rect.xmax(),
                                     x.rect.ymax());
            else
                newRect = new RectHV(x.rect.xmin(), x.p.y(), x.rect.xmax(),
                                     x.rect.ymax());

            x.rt = put(x.rt, point, val, orientation * -1, newRect);
        }
        // point to be added is already in the KdTree, replace its current value
        else {
            x.value = val;
        }

        return x;
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("calls get() with a null key");
        return get(root, p, VERTICAL);
    }

    // helper method for get, traverses nodes, alternating between x- and y-
    // coordinate comparison to find parameter point
    private Value get(Node x, Point2D p, int orientation) {
        if (x == null) return null;
        int cmp = compare(p, x.p, orientation);
        if (cmp < 0)
            return get(x.lb, p, orientation * -1);
        else if (cmp > 0 || !x.p.equals(p))
            return get(x.rt, p, orientation * -1);
        else
            return x.value;
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("argument to contains() is null");
        return get(p) != null;
    }

    // all points in the symbol table in level-order
    public Iterable<Point2D> points() {
        Queue<Node> nodes = new Queue<Node>();
        Queue<Point2D> points = new Queue<Point2D>();
        if (root == null)
            return points;
        nodes.enqueue(root);
        while (!nodes.isEmpty()) {
            Node temp = nodes.dequeue();
            points.enqueue(temp.p);
            if (temp.lb != null)
                nodes.enqueue(temp.lb);
            if (temp.rt != null)
                nodes.enqueue(temp.rt);
        }
        return points;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("argument to range() is null");
        Queue<Point2D> points = new Queue<Point2D>();
        range(root, rect, points);
        return points;
    }

    // helper method for range, only searches nodes if their rect intersects
    // the parameter rect (pruning), checks if their points are within parameter
    // rect
    private void range(Node x, RectHV rect, Queue<Point2D> queue) {

        if (x == null) return;

        if (rect.contains(x.p))
            queue.enqueue(x.p);

        if (x.lb != null && x.lb.rect.intersects(rect))
            range(x.lb, rect, queue);

        if (x.rt != null && x.rt.rect.intersects(rect))
            range(x.rt, rect, queue);

    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null || size == 0)
            throw new IllegalArgumentException("argument to nearest() is null");
        return nearest(root, p, root.p);
    }

    // nearest helper method, prioritizes searching for nearest neighbor in
    // the node with the rect closest to parameter point, prunes if rect is
    // further from the parameter point than the nearest neighbor found so far
    private Point2D nearest(Node x, Point2D p, Point2D champion) {

        double minDist = p.distanceSquaredTo(champion);

        // update champion
        if (x.p.distanceSquaredTo(p) < minDist) {
            champion = x.p;
            minDist = p.distanceSquaredTo(champion);
        }

        // determines if lb or rt should be checked for nearest neighbbor first
        boolean lbFirst = false;
        if (x.lb != null) {
            if (x.rt != null)
                lbFirst = x.lb.rect.distanceSquaredTo(p) <
                        x.rt.rect.distanceSquaredTo(p);
            else
                lbFirst = true;
        }

        if (lbFirst) {
            if (x.lb.rect.distanceSquaredTo(p) < minDist)
                champion = nearest(x.lb, p, champion);

            if (x.rt != null && x.rt.rect.distanceSquaredTo(p) <
                    p.distanceSquaredTo(champion))
                champion = nearest(x.rt, p, champion);

        }
        else {
            if (x.rt != null && x.rt.rect.distanceSquaredTo(p) < minDist)
                champion = nearest(x.rt, p, champion);

            if (x.lb != null && x.lb.rect.distanceSquaredTo(p) <
                    p.distanceSquaredTo(champion))
                champion = nearest(x.lb, p, champion);
        }

        return champion;
    }

    // compares two points based on orientation parameter; i.e. compares x-
    // coordinates if orientation is vertical, compares y-coordinates otherwise
    // (orientation is horizontal)
    private int compare(Point2D p1, Point2D p2, int orientation) {
        if (orientation == VERTICAL) {
            if (p1.x() > p2.x())
                return 1;
            else if (p1.x() < p2.x())
                return -1;
            else
                return 0;
        }
        else {
            if (p1.y() > p2.y())
                return 1;
            else if (p1.y() < p2.y())
                return -1;
            else
                return 0;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

        // initialize the data structures with n points from file
        String filename = args[0];
        In in = new In(filename);
        KdTreeST<Integer> kdtree = new KdTreeST<Integer>();
        for (int i = 0; !in.isEmpty(); i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.put(p, i);
            // tests contains()
            StdOut.println("Contains " + p + ": " + kdtree.contains(p));
        }

        // tests points() iterator
        Iterable<Point2D> points = kdtree.points();
        for (Point2D p : points) {
            StdOut.print(p + " | ");
            StdOut.println("Value of point: " + kdtree.get(p));
        }

        // tests range() iterator
        RectHV middleSquare = new RectHV(0.25, 0.25, 0.75, 0.75);
        Iterable<Point2D> range = kdtree.range(middleSquare);
        StdOut.println("The following points are in range of " + middleSquare);
        for (Point2D p : range) {
            StdOut.println(p);
        }

        // tests nearest()
        int nearestCalls = 0;
        Stopwatch timer = new Stopwatch();
        while (timer.elapsedTime() < 1.0) {
            double x = ((int) (1000000 * StdRandom.uniform(0.0, 1.0))) / 1000000.0;
            double y = ((int) (1000000 * StdRandom.uniform(0.0, 1.0))) / 1000000.0;
            Point2D point = new Point2D(x, y);
            kdtree.nearest(point);
            nearestCalls++;

        }

        double time = timer.elapsedTime();
        StdOut.println(nearestCalls + " calls to nearest in " + time + " seconds");

        // tests isEmpty() and size()
        StdOut.println("ST is empty: " + kdtree.isEmpty());
        StdOut.println("ST Size: " + kdtree.size());
    }

}
