/* *****************************************************************************
 *  Name:    Joshua Drossman
 *  NetID:   drossman
 *  Precept: P02
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 *
 *  Description:  API that supports Move-to-front encoding and decoding, as
 *                described in their respective methods, by reading in data
 *                from BinaryStdIn and writing it to BinaryStdOut.
 *
 **************************************************************************** */


import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;


public class MoveToFront {

    // size of the ASCII alphabet
    private static final int R = 256;

    // apply move-to-front encoding, reading from stdin and writing to stdout
    public static void encode() {
        // maps characters to their associated index
        int[] charToIndex = new int[R];
        // maps indices to their associated character
        char[] indexToChar = new char[R];

        // initialize the arrays
        for (int i = 0; i < R; i++) {
            charToIndex[i] = i;
            indexToChar[i] = (char) i;
        }

        // read each 8-bit character c from standard input, output the 8-bit
        // index in the sequence where c appears and move c to the front
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = charToIndex[c];
            BinaryStdOut.write((char) index);
            for (int i = index; i > 0; i--) {
                indexToChar[i] = indexToChar[i - 1];
                charToIndex[indexToChar[i]]++;
            }
            indexToChar[0] = c;
            charToIndex[c] = 0;
        }

        BinaryStdOut.close();

    }

    // apply move-to-front decoding, reading from stdin and writing to stdout
    public static void decode() {
        // maps indices to their associated character
        char[] indexToChar = new char[R];

        // initialize the arrays
        for (int i = 0; i < R; i++) {
            indexToChar[i] = (char) i;
        }

        // read each 8-bit character i from standard input, write the ith
        // character in the sequence and move that character to the front
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = (int) c;
            char decodedChar = indexToChar[index];
            BinaryStdOut.write(decodedChar);
            for (int i = index; i > 0; i--) {
                indexToChar[i] = indexToChar[i - 1];
            }
            indexToChar[0] = decodedChar;
        }

        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-"))
            encode();
        if (args[0].equals("+"))
            decode();
    }

}
