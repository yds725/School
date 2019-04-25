/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static  int W = 9;         // codeword width
    private static  int L = (int) Math.pow(2, W);       // number of codewords = 2^W 512
    private static final int MIN_SIZE = 9; // vary from 9 - 16 bits (code bit words)
    private static final int MAX_SIZE = 16; // vary from 9 - 16 bits

    private static char mode = 'n';
    private static double original_size = 0;
    private static double compressed_size = 0;
    private static double old_ratio = 0;
    private static double new_ratio = 0;


    public static void compress() {
        BinaryStdOut.write(mode);
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R + 1;  // R is codeword for EOF


        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();

            original_size += t * 16;
            compressed_size += W;

            if (t < input.length() && code < L) {    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
                old_ratio = original_size / compressed_size; //calculate old ratio

            } else if (t < input.length() && code >= L) { // if code words size is full
                if (W < MAX_SIZE) { // add words until code width reaches 16
                    W++;
                    L = (int) Math.pow(2, W);
                    st.put(input.substring(0, t + 1), code++);

                } else if (W == MAX_SIZE) { // when code width reaches 16 or symbol table is full
                    if (mode == 'r') { //if mode is Reset mode

                        st = new TST<Integer>(); //reinitialize code book or going back to initializing state

                        for (int i = 0; i < R; i++)
                            st.put("" + (char) i, i);

                          code = R + 1;
                          W = MIN_SIZE;
                          L = (int) Math.pow(2, W);

                        st.put(input.substring(0, t + 1), code++);

                    } else if (mode == 'm') { // if mode is Monitor mode
                        new_ratio = original_size / compressed_size; // calculate new ratio
                        double ratioOfTwoRatios = old_ratio / new_ratio; // this is ratio of compression ratios

                        if (ratioOfTwoRatios > 1.1) { // this is threshold to determine resetting

                            st = new TST<Integer>(); //reinitialize code book or going back to initializing state

                            for (int i = 0; i < R; i++)
                                st.put("" + (char) i, i);

                            code = R + 1;
                            W = MIN_SIZE;
                            L = (int) Math.pow(2, W);

                            st.put(input.substring(0, t + 1), code++);
                        }
                    }
                }
            }
            input = input.substring(t);            // Scan past s in input.

        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();

    }

    public static void expand() {

        mode = BinaryStdIn.readChar();

        String[] st = new String[(int) Math.pow(2,16)];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];


        while (true) {
            original_size += val.length() *16;
            compressed_size += W;


            BinaryStdOut.write(val);

            if(i >= L){ // when i(current code value) is out of range of codewords length( numbers of codeword)
                if(W < MAX_SIZE){ //if there is more room to expand
                    W++;
                    L = (int) Math.pow(2,W);
                    old_ratio = original_size / compressed_size;
                } else if(W == MAX_SIZE){// if codebook is absolutely filled up
                    if(mode == 'r'){ // when mode is Reset

                         st = new String[(int) Math.pow(2,16)];
                      //  int i; // next available codeword value

                        // initialize symbol table with all 1-character strings
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = "";                        // (unused) lookahead for EOF

                        W = MIN_SIZE;
                        L = (int) Math.pow(2,W);

                    } else if(mode=='m'){ // when mode is Monitor
                        new_ratio = original_size / compressed_size;
                        double ratioOfTwoRatios = old_ratio / new_ratio;

                        if(ratioOfTwoRatios > 1.1) { //if ratio exceed threshold reset dictionary again

                             st = new String[(int) Math.pow(2,16)];
                            //int i; // next available codeword value

                            // initialize symbol table with all 1-character strings
                            for (i = 0; i < R; i++)
                                st[i] = "" + (char) i;
                            st[i++] = "";                        // (unused) lookahead for EOF

                            W = MIN_SIZE;
                            L = (int) Math.pow(2, W);

                            old_ratio = 0;
                        }
                    }
                }
            }

            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L){
                st[i++] = val + s.charAt(0);
                old_ratio = original_size / compressed_size;
            }
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if(args.length >= 2){
            if(args[1].equals("m")){
                mode = 'm';
            }
            if(args[1].equals("r")){
                mode = 'r';
            }
            if (args[1].equals("n")) {
                mode = 'n';
            }
        }

        if (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
