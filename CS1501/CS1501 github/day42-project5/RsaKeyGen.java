import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 * Daesang Yoon - day42
 * RsaKeyGen creates public key file and private key file
 */
public class RsaKeyGen {

    public static void generateKeyRsa(LargeInteger e, LargeInteger d, LargeInteger n){

        try{
            //Generate pubKey.rsa (save e and n)
            FileOutputStream pubKeyFile = new FileOutputStream("pubkey.rsa");
            ObjectOutputStream writeToPublic = new ObjectOutputStream(pubKeyFile);
            writeToPublic.writeObject(e);
            writeToPublic.writeObject(n);
            writeToPublic.close();

            //Generate privkey.rsa (save d and n)
            FileOutputStream privateKeyFile = new FileOutputStream("privkey.rsa");
            ObjectOutputStream writeToPrivate = new ObjectOutputStream(privateKeyFile);
            writeToPrivate.writeObject(d);
            writeToPrivate.writeObject(n);
            writeToPrivate.close();

            System.out.println("pubkey.rsa privkey.rsa successfully generated!");

        }catch(IOException except){
            System.out.println("Error! " + except.toString());
            return;
        }
    }

    //generate e value
    public static LargeInteger eValueCompute(){
        byte three = 3;
        LargeInteger e = new LargeInteger(null);
        LargeInteger initialVal = new LargeInteger(new byte[] {three});
        e = e.add(initialVal); //add 3 to 3 so that initialize e as 3

        return e;
    }

    public static void main(String args[]){
        Random randomNum = new Random();

        //Generate random prime p q
        //LargeInteger p = new LargeInteger(LargeInteger.generate(256, randomNum));
        LargeInteger p = new LargeInteger(256, randomNum);
        //LargeInteger q = new LargeInteger(LargeInteger.generate(256, randomNum));
        LargeInteger q = new LargeInteger(256, randomNum);

        // equation n = p * q
        LargeInteger n = p.multiply(q);

        LargeInteger minusOneP = p.subtractOne(); //p-1
        LargeInteger minusOneQ = q.subtractOne(); //q=1
        // PHI(n) = (p-1) * (q-1)
        LargeInteger PHI = new LargeInteger(minusOneP.addByOneByte(minusOneP.multiply(minusOneQ).getVal()));
        LargeInteger e = eValueCompute(); //create e s.t. 1 < e < PHI (must be coprime to PHI)
        LargeInteger d = new LargeInteger(null); //just make d as 0 until calculation is made

        while(!LargeInteger.GCD(e, PHI).isOne()) {
            e = e.add(new LargeInteger(new byte[] {2} )); // e = e + 2 adding 2 will always make it odd
        }

        d = e.calculateModularInverse(PHI);

        generateKeyRsa(e, d , n);
    }
}
