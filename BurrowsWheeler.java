/* *****************************************************************************
 *  Name:    Joshua Drossman
 *  NetID:   drossman
 *  Precept: P02
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description:  API that supports rearranging the characters in the input so
 *                that there are lots of clusters with repeated characters, but
 *                in such a way that it is still possible to recover the
 *                original input (i.e. the Burrows-Wheeler transform)
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // size of the ASCII alphabet
    private static final int R = 256;

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        while (!BinaryStdIn.isEmpty()) {

            String input = BinaryStdIn.readString();
            CircularSuffixArray csa = new CircularSuffixArray(input);
            String circularInput = input + input;

            // determines index of first in csa
            int first = 0;
            while (csa.index(first) != 0)
                first++;
            BinaryStdOut.write(first);

            for (int i = 0; i < input.length(); i++) {
                int index = csa.index(i) + input.length() - 1;
                BinaryStdOut.write(circularInput.charAt(index));
            }
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {

        int first = BinaryStdIn.readInt();
        String string = BinaryStdIn.readString();

        int[] count = new int[R + 1];
        int n = string.length();

        // key-index counting to trace original ordering of the letters
        for (int i = 0; i < n; i++)
            count[string.charAt(i) + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];

        int[] next = new int[n];
        for (int i = 0; i < n; i++)
            next[i] = count[string.charAt(i)]++;

        // retrieve the original string by tracing next[]
        int current = first;
        char[] aux = new char[n];

        for (int i = 0; i < n; i++) {
            aux[i] = string.charAt(current);
            current = next[current];
        }

        // returns correctly ordered original string, which was backwards in aux
        for (int i = n - 1; i >= 0; i--) {
            BinaryStdOut.write(aux[i]);
        }


        BinaryStdOut.close();


    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-"))
            transform();
        if (args[0].equals("+"))
            inverseTransform();
    }

}
