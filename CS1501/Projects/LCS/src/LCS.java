import java.util.Arrays;

public class LCS {

    public static String findLCS(String s1, String s2){

        //dynamic programming with memoization and recursive
        int[][] lengthOfLCS = new int[s1.length() + 1][s2.length() +1];
        String stringOfLCS = "";
        for(int i = 0; i < s1.length(); i++){
            for(int j = 0; j < s2.length(); j++){

                //if c1 == c2; LCS(s1,s2) +c1 we are putting the possible optimal length in matrix table
                if(s1.charAt(i) == s2.charAt(j)){
                    lengthOfLCS[i+1][j+1] = lengthOfLCS[i][j] + 1;
                } else {
                    // LCS(s1+c1,s2) if c1!=c2 and LCS(s1+c1,s2)>LCS(s1,s2+c2)
                    // if c1!=c2, length is LCS(s1+c1,s2) if it is larger; if not larger LCS(s1,s2+c2)
                    lengthOfLCS[i+1][j+1] = Math.max(lengthOfLCS[i+1][j], lengthOfLCS[i][j+1]);
                }
            }
        }

        //testing purpose
        /*for (int[] row : lengthOfLCS)
        {
            // Arrays.fill(row, 0);
            System.out.println(Arrays.toString(row));
        }*/

        int c1 = s1.length();
        int c2 = s2.length();
        //now search through length matrix table, starting from very bottom right (s1Max, s2Max)
        while(c1 > 0 && c2 > 0){
            //we are walking back matrix through
            //if length is equal that means there is no character match so move to other position in matrix
            if(lengthOfLCS[c1][c2] == lengthOfLCS[c1-1][c2]){
                c1--;
            }
            else if(lengthOfLCS[c1][c2] == lengthOfLCS[c1][c2-1]){
                c2--;
            } else {
                //if length is not matched then there is character so we add character
                stringOfLCS = s1.charAt(c1-1) + stringOfLCS;
                c1--;
                c2--;
            }
        }
        return stringOfLCS;
        //return "a";
    }

}
