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

import java.util.Arrays;

public class CircularSuffixArrayOptimized {

    // original index of suffix in the ith row
    private final int[] index;
    // length of input string
    private final int length;

    // circular suffix array of s
    public CircularSuffixArrayOptimized(String s) {
        if (s == null)
            throw new IllegalArgumentException("Null argument to constructor");
        length = s.length();
        index = new int[length];
        CircularSuffix[] suffixArray = new CircularSuffix[length];
        String circularString = s + s;
        for (int i = 0; i < length; i++)
            suffixArray[i] = new CircularSuffix(circularString, i, i);

        Arrays.sort(suffixArray);

        for (int i = 0; i < length; i++)
            index[i] = suffixArray[i].index();

    }

    // nested helper class to represent circular suffixes, maintaing reference
    // to the same string
    private class CircularSuffix implements Comparable<CircularSuffix> {
        // circular suffix original string (appended to itself)
        private final String text;
        // offset of this suffix
        private final int offset;
        // initial index in the circular suffix array of this suffix
        private final int index;

        // constructs new circular suffix object
        private CircularSuffix(String text, int offset, int index) {
            this.text = text;
            this.offset = offset;
            this.index = index;
        }

        // length of circular suffix (divided by 2 because input text is doubled)
        private int length() {
            return text.length() / 2;
        }

        // char at index i of circular suffix
        private char charAt(int i) {
            return text.charAt(offset + i);
        }

        // char at index i of circular suffix
        private int index() {
            return index;
        }

        // toString for debugging
        public String toString() {
            return text.substring(offset, offset + length());
        }

        // defines method of comparison between two suffixes
        public int compareTo(CircularSuffix that) {
            if (this == that) return 0;
            int n = Math.min(this.length(), that.length());
            for (int i = 0; i < n; i++) {
                if (this.charAt(i) < that.charAt(i)) return -1;
                if (this.charAt(i) > that.charAt(i)) return 1;
            }
            return this.length() - that.length();
        }
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
        CircularSuffixArrayOptimized csa = new CircularSuffixArrayOptimized(StdIn.readLine());
        for (int i = 0; i < csa.length(); i++) {
            StdOut.println("Index of i = " + i + ": " + csa.index(i));
        }
        StdOut.println("Length: " + csa.length());
    }

}
