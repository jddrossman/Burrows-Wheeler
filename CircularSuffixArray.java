/* *****************************************************************************
 *  Name:    Joshua Drossman
 *  NetID:   drossman
 *  Precept: P02
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description:  API for circular suffix array data structure that offers
 *                support for finding the original indices of unsorted
 *                circular suffixes for use in BurrowsWheeler transform.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    // original index of suffix in the ith row
    private final int[] index;
    // length of input string
    private final int length;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null argument to constructor");
        length = s.length();
        index = new int[length];
        for (int i = 0; i < length; i++)
            index[i] = i;

        sort(s + s, index, 0, length - 1, 0);
    }

    // helper method to sort suffixes using 3-way string quicksort
    private static void sort(String a, int[] index, int lo, int hi, int d) {
        if (hi <= lo || d > a.length() / 2 - 1) return;
        int v = a.charAt(index[lo] + d);

        int lt = lo, gt = hi;
        int i = lo + 1;
        while (i <= gt) {
            int c = a.charAt(index[i] + d);
            if (c < v) exch(index, lt++, i++);
            else if (c > v) exch(index, i, gt--);
            else i++;
        }

        sort(a, index, lo, lt - 1, d);
        sort(a, index, lt, gt, d + 1);
        sort(a, index, gt + 1, hi, d);
    }

    // helper method to exchange values at specified indices in an array
    private static void exch(int[] index, int a, int b) {
        int temp = index[a];
        index[a] = index[b];
        index[b] = temp;
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i > length - 1)
            throw new IllegalArgumentException("Invalid argument to index()");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray csa = new CircularSuffixArray(StdIn.readLine());
        for (int i = 0; i < csa.length(); i++) {
            StdOut.println("Index of i = " + i + ": " + csa.index(i));
        }
        StdOut.println("Length: " + csa.length());
    }

}

