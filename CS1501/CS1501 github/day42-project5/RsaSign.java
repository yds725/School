import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;

/**
 * Daesang Yoon - Day42
 * java RsaSing s <filename> - create signature(.sig extension)
 * java RsaSign v </filename> - verify hash value of sig file same as original file
 */
public class RsaSign {
    private static File file;

    public static void sign(){

        try{

            Path path = file.toPath();
            byte[] value = Files.readAllBytes(path);

            MessageDigest MD =MessageDigest.getInstance("SHA-256");
            MD.update(value);
            byte[] messageDigest =MD.digest(); //generate hash content of this file

            File newFile = new File("privkey.rsa");
            if(!newFile.exists()){
                System.out.println("Error! Private key rsa file is not found!");
                return;
            }

            FileInputStream privateKey =new FileInputStream("privkey.rsa"); //open and read privkey.rsa file
            ObjectInputStream readKey = new ObjectInputStream(privateKey);
            LargeInteger d = (LargeInteger) readKey.readObject(); //d is first stored so read d first
            LargeInteger n = (LargeInteger) readKey.readObject(); // read n next

            readKey.close();

            LargeInteger hashValue = new LargeInteger(n.addByOneByte(messageDigest));
            LargeInteger valueDecrypted =hashValue.modularExp(d, n);

            FileOutputStream sigFile = new FileOutputStream(file.getName() + ".sig"); //put .sig extension to file
            ObjectOutputStream writeSignature =new ObjectOutputStream(sigFile);
            writeSignature.writeObject(value); //write file all data to sig file
            writeSignature.writeObject(valueDecrypted); //write this decrypted val to sigfile
            writeSignature.close();


        }catch(Exception e){
            System.out.println("Error! " + e);
            e.printStackTrace();
            return;
        }
    }

    private static void verify(){
        try{
            //Generate SHA-256 Hash of this file again using MessageDigest class
            MessageDigest compareToMD =MessageDigest.getInstance("SHA-256");
            compareToMD.update(Files.readAllBytes(file.toPath()));
            byte[] hash =compareToMD.digest(); //generate hash content of this file

            File newFile = new File(file.getName() + ".sig");
            if(!newFile.exists()){
                System.out.println("Error! File " + file.getName() + ".sig is not found!");
                return;
            }

            FileInputStream sigFile =new FileInputStream(file.getName() + ".sig"); //open and read sig file
            ObjectInputStream readSigFile = new ObjectInputStream(sigFile);
            byte[] value = (byte[]) readSigFile.readObject();
            LargeInteger valueDecrypted = (LargeInteger) readSigFile.readObject();
            readSigFile.close();

           //Generate hash of original file content
            //MessageDigest originalMD =MessageDigest.getInstance("SHA-256");
            //originalMD.update(value);
           // byte[] otherHash = originalMD.digest();
            LargeInteger originalHash = new LargeInteger(LargeInteger.addByOneByte(hash)); //change that original hash into LargeInteger class //here that hash to originalHash

            File pubKeyFile = new File("pubkey.rsa");
            if(!pubKeyFile.exists()){
                System.out.println("Error! PubKey.rsa file is not found!");
                return;
            }

            //Read e and n from pubkey.rsa file
            FileInputStream publicKey =new FileInputStream("pubkey.rsa"); //open and read privkey.rsa file
            ObjectInputStream readKey = new ObjectInputStream(publicKey);
            LargeInteger e = (LargeInteger) readKey.readObject(); //d is first stored so read d first
            LargeInteger n = (LargeInteger) readKey.readObject(); // read n next

            readKey.close();

            LargeInteger valueEncrypted = valueDecrypted.modularExp(e, n);

            valueEncrypted = valueEncrypted.trimZeros();
            originalHash = originalHash.trimZeros();

            boolean isSigVaid = valueEncrypted.isPrimeEqual(originalHash); //compare hash value of sig file and original file to verify
            if(isSigVaid)
                System.out.println("Hash Values are same! This signature is valid!");
            else
                System.out.println("Hash Values are NOT same! This signature is invalid!");
        }catch(Exception exception){
            System.out.println("Error! " + exception);
        }
    }

    public static void main(String[] args){

        if(args.length == 2){
            char input = args[0].charAt(0);
            if(input != 's' && input != 'v'){
                    System.out.println("Error! Invalid command! Must type s or v!");
                    return;
            }

            String fileName = args[1];
            file = new File(fileName);
            if(!file.exists()){
                System.out.println("Error! Invalid File! Does not exist!");
                return;
            }

            if(input == 's')
                sign();
            else if(input == 'v')
                verify();

        } else {
            System.out.println("Error! Program requires 2 commands!");
            return;
        }
    }
}
