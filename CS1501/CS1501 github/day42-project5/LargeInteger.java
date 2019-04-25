import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class LargeInteger implements Serializable {
	
	private final byte[] ONE = {(byte) 1};
	private final static LargeInteger OTHER_ONE = new LargeInteger(new byte[] { 1 });
	private final static LargeInteger ZERO = new LargeInteger(new byte[] { 0 });

	private byte[] val;

	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b) {
		if(b == null) val = new byte[65];
		else val = b;
	}

	public static byte[] generate(int n, Random rnd){
		return BigInteger.probablePrime(n, rnd).toByteArray();
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd) {
		val = BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal() {
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length() {
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension) {
		byte[] newv = new byte[val.length + 1];
		newv[0] = extension;
		for (int i = 0; i < val.length; i++) {
			newv[i + 1] = val[i];
		}
		val = newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative() {
		return (val[0] < 0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int carry = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

			// Assign to next byte
			res[i] = (byte) (carry & 0xFF);

			// Carry remainder over to next byte (always want to shift in 0s)
			carry = carry >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	public LargeInteger addFunction(LargeInteger number){
		//Null checks
		if(number == null) return null;
		byte[] numVal = number.getVal();
		if(val == null || numVal == null) return null;

		//Do these checks to make sure we can add two differing length binary numbers together; stop considering the other
		//	binary number when i >= minLength.
		int maxLength = val.length >= numVal.length ? val.length : numVal.length;
		int minLength = val.length >= numVal.length ? numVal.length : val.length;
		boolean isThisPrimeLarger = val.length > numVal.length;
		byte[] result = new byte[maxLength];

		boolean carrier = false; //Is there a bit carry?
		byte currentBit = 1; //Used to AND with the current byte to get its bit; 1 = 0x00000001, 2 = 0x00000010, 4 = 0x00000100, ...
		for(int i = 0; i < maxLength; i++){ //Scan through the bytes of each number right to left
			byte currByteWrite = 0; //Write this byte to the output

			for(int j = 7; j >= 0; j--){ //Scan through each byte right to left
				byte bitOne = 0;
				byte bitTwo = 0;

				if(i >= minLength){ //One number has more bits than the other, so if we exceed the bits of the smaller number, just directly copy the larger number over
					//Now decide which binary number is larger and read the bit from it
					if(isThisPrimeLarger){
						bitOne = (byte) ((val[val.length-i-1] & currentBit) == 0 ? 0 : 1);
						bitTwo = 0;
					} else{
						bitOne = 0;
						bitTwo = (byte) ((numVal[numVal.length-i-1] & currentBit) == 0 ? 0 : 1);
					}
				} else{ //Have not exceeded the bit length of either binary number
					bitOne = (byte) ((val[val.length-i-1] & currentBit) == 0 ? 0 : 1);
					bitTwo = (byte) ((numVal[numVal.length-i-1] & currentBit) == 0 ? 0 : 1);
				}

				byte newBit = 0; //New bit to write to the byte

				if(bitOne + bitTwo == 2){ //bit1 = 1 and bit2 = 1
					if(!carrier){ // 1 + 1 = 0 carry 1
						newBit = 0;
						carrier = true;
					} else{ // 1 + 1 + 1 = 1 carry 1
						newBit = 1;
						carrier = true;
					}
				} else if(bitOne + bitTwo == 1){ //one of the bits is 1 and the other one is 0
					if(!carrier){ // 1 + 0 = 1
						newBit = 1;
						carrier = false;
					} else{ // 1 + 0 + 1 = 0 carry 1
						newBit = 0;
						carrier = true;
					}
				} else if(bitOne + bitTwo == 0){ //bit1 = 0 and bit2 = 0
					newBit = (byte) (carrier ? 1 : 0); //If there's a carry, the new bit will be 1; otherwise, it'll stay at 0
					carrier = false;
				}

				currByteWrite |= ((newBit == 0) ? 0 : currentBit); //If the new bit is on (1), then set that bit in the byte
				currentBit *= 2; //AND with 1, then 2, then 4, then 8, 16, 32, 64, 128 for each byte
			}

			result[result.length-i-1] = currByteWrite; //Write the new byte data to the output
			currentBit = 1; //Reset the current spot in the byte
		}

		LargeInteger sumOfPrimes = new LargeInteger(result); //Convert the byte array of the output to a BigPrime
		return sumOfPrimes;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other) {
		// YOUR CODE HERE (replace the return, too...)

		return simpleMultiply(other);
	}

	//Find the modular inverse (a^-1 mod b) of two numbers
	public LargeInteger calculateModularInverse(LargeInteger number){
		//Add one to phi(n)
		LargeInteger phiPlusOne = new LargeInteger(arrayCopy(number.getVal()));
		phiPlusOne = phiPlusOne.add(OTHER_ONE);

		//If the equation is d = e^-1 mod phi(n), then attempt to satisfy the equation ed = k*phi(n) + 1 where k*phi(n) is a multiple of phi(n)
		while(!phiPlusOne.modulus(this).isZero()){ //When e is divided from both sides, the remainder must be 0 to satisfy the equation, otherwise, add another multiple of phi(n)
			phiPlusOne = phiPlusOne.add(number);
		}

		return phiPlusOne.divideTwoNumbers(this); //We know the remainder is zero, so just do phi(n)+1/n
	}

	//Divide two unsigned binary numbers
	public LargeInteger divideTwoNumbers(LargeInteger number){
		if(compareTwoNumbers(number) == -1) return new LargeInteger(null); //a/b = a when a < b
		else if(compareTwoNumbers(number) == 0) return OTHER_ONE; //Return 1 when the numbers are equal

		LargeInteger remainder = new LargeInteger(null);
		LargeInteger quotient = new LargeInteger(null);
		quotient = quotient.setNumBits(this.getBitLength()/8); //Set the length of the quotient to be this BigPrime's size
		for(int i = 0; i < this.getBitLength(); i++){ //Iterate over the bits of this BigPrime object
			remainder = remainder.shiftLeft(1);
			if(getnthBit(i) == true) remainder = remainder.setLSB(true);

			LargeInteger remainderCopy = remainder.trimZeros();
			if(remainderCopy.compareTwoNumbers(number) >= 0){
				remainder = remainder.subtract(number);
				quotient = quotient.setnthBit(i);
			}
		}

		return quotient;
	}

	//Perform the modulus operation on two unsigned binary numbers (a mod b = remainder of a/b)
	public LargeInteger modulus(LargeInteger number){
		if(compareTwoNumbers(number) == -1) return this; //a mod b = a when a < b
		else if(compareTwoNumbers(number) == 0) return new LargeInteger(null); //Return 1 when a = b


		//modulus = a - a/b where a/b is integer multiplication
		//Below uses a simple algorithm as discussed on the line above
		LargeInteger divResult = this.divideTwoNumbers(new LargeInteger(number.getVal()));
		LargeInteger mulResult = divResult.multiply(new LargeInteger(number.getVal()));
		LargeInteger remainder = this.subtract(new LargeInteger(mulResult.getVal()));
		return remainder;
	}
	
	/**
	 * Run the extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this * x + other * y == GCD in index 0
	 */
	 public LargeInteger[] XGCD(LargeInteger other) {
		// YOUR CODE HERE (replace the return, too...)
		 LargeInteger a = this;
		 LargeInteger b = other;
		 LargeInteger[] retvals = new LargeInteger[3];

		 LargeInteger a0 = LargeInteger.OTHER_ONE;
		 LargeInteger a1 = LargeInteger.ZERO;
		 LargeInteger b0 = LargeInteger.ZERO;
		 LargeInteger b1 = LargeInteger.OTHER_ONE;

		 LargeInteger quotient;
		 LargeInteger remainder;

		 while(true){
		 	remainder = a.modulus(b); //this return remainder
			quotient = a.divideTwoNumbers(b);
			a = remainder;
			a0 = a0.subtract(b0.multiply(quotient));
			a1 = a1.subtract(b1.multiply(quotient));
			if (a.isZero()) {
				retvals[0] = b;
				retvals[1] = b0;
				retvals[2] = b1;
				return  retvals;
			}

			remainder = b.modulus(a);
			quotient = b.divideTwoNumbers(a);
			b = remainder;
			b0 = b0.subtract(a0.multiply(quotient));
			b1 = b1.subtract(a1.multiply(quotient));
			if(b.isZero()){
				retvals[0] = a;
				retvals[1] = a0;
				retvals[2] = a1;
				return retvals;
			}
		 }

	 }

	//Calculate the greatest common divisor of two unsigned binary numbers a and b
	public static LargeInteger GCD(LargeInteger x, LargeInteger y){
		//If either number is zero, the GCD is the other number
		if(x.isZero()) return y;
		if(y.isZero()) return x;

		int shift; //Keep a shift counter variable that will be used later
		for(shift = 0; x.orOperation(y).andOperation().isZero(); shift++){
			x = x.shiftRight(1);
			y = y.shiftRight(1);
		}

		while (x.andOperation().isZero()){ //If the LSB of a is 0, then shift right until the LSB is 1.
			//This forces a to be odd.  If a LSB = 1, that means a is divisible by 2.
			x = x.shiftRight(1);
		}

		do{
			while (y.andOperation().isZero()){ //If the LSB = 0, then b is divisible by 2, as mentioned above.
				y = y.shiftRight(1);
			}

			if (x.compareTwoNumbers(y) == 1) { //Make it so a < b so subtraction is easier and won't produce a negative number (since we're only working with unsigned)
				//Swap the references of a and b
				LargeInteger temp = y;
				y = x;
				x = temp;
			}
			y = y.subtract(x); //An easy subtraction since b > a.  b is now even because odd - odd = even.  Repeat this process until b = 0
		} while(!y.isZero());

		return x.shiftLeft(shift);
	}

	//Logical AND with an unsigned binary number
	public LargeInteger andOperation(){
		byte[] newVal = arrayCopy(val);
		boolean andIsOne = (newVal[newVal.length-1] & 0x1) != 0;

		for(int i = 0; i < newVal.length; i++){ //Copy the old data into the new byte array
			newVal[i] = 0;
		}

		if(andIsOne) newVal[newVal.length-1] = 0x1; //If the AND produced a 1, write a 1 to the appropriate byte

		return new LargeInteger(newVal);
	}

	 //Logical OR with an unsigned binary number
	 public LargeInteger orOperation(LargeInteger number){
		 byte[] numVal = number.getVal();
		 byte[] result = new byte[val.length];

		 for(int i = 0; i < val.length; i++){ //Iterate over the bytes of each binary number
			 int currentBit = 1;

			 for(int j = 7; j >= 0; j--){ //Iterate through the bits in both numbers' bytes
				 byte bitOne = 0;
				 byte bitTwo = 0;

				 if(i >= numVal.length){ //If one number is longer than the other, just grab the data from one number so we don't get an ArrayIndexOutOfBoundsException
					 bitOne = (byte) (val[val.length-i-1] & currentBit);
					 bitTwo = 0;
				 } else{ //We have not exceeded the bounds of either binary number, so grab the bit of each
					 bitOne = (byte) (val[val.length-i-1] & currentBit);
					 bitTwo = (byte) (numVal[numVal.length-i-1] & currentBit);
				 }

				 byte bitToWrite = 0; //The byte to write to the new output
				 if(bitOne == 0 && bitTwo == 0) bitToWrite = 0; // 0 | 0 = 0
				 else if(bitOne != 0 && bitTwo == 0) bitToWrite = (byte) currentBit; // 1 | 0 = 1
				 else if(bitOne != 0 && bitTwo != 0) bitToWrite = (byte) currentBit; // 1 | 1 = 1
				 else if(bitOne == 0 && bitTwo != 0) bitToWrite = (byte) currentBit; // 0 | 1 = 1

				 result[val.length-i-1] |= bitToWrite; //Flip the bit on if the bit being written is not 0

				 currentBit *= 2;
			 }
		 }

		 return new LargeInteger(result);
	 }

	/**
	 * Compute the result of raising this to the power of y mod n
	 * @param y exponent to raise this to
	 * @param n modulus value to use
	 * @return this^y mod n
	 */

	 public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
		// YOUR CODE HERE (replace the return, too...)
		 boolean useMod = (n != null); //Determines whether we are using the modulus function in this modPow

		 LargeInteger result = new LargeInteger(null);
		 result = result.add(OTHER_ONE); //Start with result = 1

		 LargeInteger base = new LargeInteger(arrayCopy(val));
		 LargeInteger powCopy = new LargeInteger(arrayCopy(y.getVal()));

		 while(!powCopy.isZero()){ //Divide the power in half for each iteration
			 if(!powCopy.isEven()){ //lsb = 1, so the power is odd.  Thus, multiply the result by the base and do a mod
				 if(useMod) result = result.multiply(base).modulus(n);
				 else result = result.multiply(base);
			 }

			 //Divide the power in half
			 powCopy = powCopy.shiftRight(1);

			 //For every iteration, multiply the base by itself.  If performing mod function, mod this multiplication result.
			 if(useMod) base = base.multiply(base).modulus(n);
			 else base = base.multiply(base);
		 }

		 return result;
	 }

	 //// Some helper methods ////

	//Compare two binary numbers.  Returns an 1 if larger, 0 if equal, or -1 lesser. same as compareTo function
	public int compareTwoNumbers(LargeInteger number){
		//Trim the leading zeroes so that we dont have to compare unnecessary things
		LargeInteger thisShort = this.trimZeros();
		LargeInteger numShort = number.trimZeros();

		//After trimming  if one binary number is longer than the other, it means it is greater
		if(thisShort.getBitLength() > numShort.getBitLength()){
			return 1;
		} else if(thisShort.getBitLength() < numShort.getBitLength()){
			return -1;
		}

		int lengthOfVal = thisShort.getVal().length;
		byte[] thisVal = thisShort.getVal();
		byte[] numVal = numShort.getVal();

		byte currentBit = (byte) 0x40; //start currBitVal equal to 0x01000000
		for(int i = 0; i < lengthOfVal; i++){ //Scan through the bytes of each number right to left
			//Perform the initial and with 0x10000000 that we couldn't do initially with current bit

			byte bitOnePre = (byte) ((thisVal[i] & 0x80) == 0 ? 0 : 1);
			byte bitTwoPre = (byte) ((numVal[i] & 0x80) == 0 ? 0 : 1);

			if(bitOnePre > bitTwoPre){
				return 1;
			} else if(bitOnePre < bitTwoPre){
				return -1;
			}

			for(int j = 6; j >= 0; j--){ //Scan through each bit left to right (MSB to LSB).
				byte bitOne = (byte) ((thisVal[i] & currentBit) == 0 ? 0 : 1);
				byte bitTwo = (byte) ((numVal[i] & currentBit) == 0 ? 0 : 1);

				if(bitOne > bitTwo){
					return 1;
				} else if(bitOne < bitTwo){
					return -1;
				}

				currentBit >>= 1; //we are shifting to right twice
			}

			currentBit = (byte) 0x40; //Reset current bit to starting one
		}

		return 0; // return that both are equal if no other decision is made
	}

	public String toString(){
		String result = "";

		for(int i = 0; i < val.length; i++){
			result += String.format("%8s", Integer.toBinaryString(val[i] & 0xFF)).replace(' ', '0');
		}

		return result;
	}

	public int getBitLength(){
		return length() * 8;
	}

	//Determine if binary number is same as 1
	public boolean isOne(){

		if(val[val.length-1] != 1) return false; //If the smallest byte is not even 1, just return false
		for(int i = 0; i < val.length-1; i++){ //If not iterate over all the bytes to see if binary number is 1
			if(val[i] != 0) return false;
		}

		return true;
	}

	//Determine if binary number is same as 0
	public boolean isZero(){

		for(int i = 0; i < val.length; i++){ //Make sure every byte/bit has the value 0
			if(val[i] != 0) return false;
		}

		return true;
	}

	//Determine if binary num is even or not
	public boolean isEven(){
		byte LSB = (byte) (val[val.length-1] & 0x1); //If the LSB = 0 it is even; if the LSB = 1, it is odd
		return LSB == 0; //Check LSB
	}

	//Set the LSB of a binary number if set true, assign 1 if set false, assign 0
	public LargeInteger setLSB(boolean set){
		byte[] newVal = arrayCopy(val);
		if(set) newVal[newVal.length-1] |= 0x1;
		else newVal[newVal.length-1] &= 0xFE;

		return new LargeInteger(newVal);
	}

	//Set the nTH bit of a binary number to true
	public LargeInteger setnthBit(int bitNumber){
		int byteNumber = bitNumber / 8;
		int bitIndex = 7 - (bitNumber - (byteNumber * 8));
		int bitIndexVal = (int) Math.pow(2, bitIndex); //If the bit index = 3, then 00001000, so turn that bit true in order to OR it later

		byte[] newVal = arrayCopy(val);
		newVal[byteNumber] |= bitIndexVal; //Find that specific byte of the bit and OR it at the appropriate bit value
		return new LargeInteger(newVal);
	}

	//Get the nTH bit of a binary number returns true (1) or false (1)
	public boolean getnthBit(int bitNumber){

		//Calculate the appropriate index of the bit and the byte index within the byte array
		int byteNumber = bitNumber / 8;
		int bitIndex = 7 - (bitNumber - (byteNumber * 8));
		int bitIndexVal = (int) Math.pow(2, bitIndex);

		byte result = (byte) (val[byteNumber] & bitIndexVal); //If result = 0, the bit is 0; otherwise, the bit is 1
		return result != 0;
	}

	//Set the number of bytes/bits that a binary number has.
	public LargeInteger setNumBits(int number){
		byte[] newVal = new byte[number];
		return new LargeInteger(newVal);
	}

	 /////////////Other helper functions//////////////////

	//Subtract the value one from an odd binary number
	public LargeInteger subtractOne(){
		byte[] result = arrayCopy(val);
		result[result.length-1] &= 0xFE; //AND with 1111 1110 to make sure the last bit goes to a 0, thus subtracting one
		return new LargeInteger(result);
	}

	public LargeInteger shiftLeft(int shitfAmount){
	 	byte[] copiedVal = val;
		boolean carrier = false;

		//perform shift by that amount
		for(int i = 0; i < shitfAmount; i++){
			carrier = false;

			int expandData = copiedVal[0] & 128; //& bit operation
			if(expandData == 0) copiedVal = arrayCopy(copiedVal);
			else copiedVal = addByOneByte(copiedVal);

			//shift all bit to left direction by 1 bit
			for(int j = copiedVal.length - 1; j >= 0; j--){
				byte MSB = (byte) (copiedVal[j] & 128); //getting MSB of this bit

				copiedVal[j] <<= 1;
				if(carrier)
					copiedVal[j] |= 1; // copiedVal = copiedVal | 1;

				if(MSB == 0)
					carrier = false;
				else
					carrier = true;
			}
		}

		return new LargeInteger(copiedVal);
	}

	public LargeInteger shiftRight(int shiftAmount){
		byte[] copiedVal = arrayCopy(val);

		for(int i = 0; i < shiftAmount; i++){ //Perform  shifts by that amount
			boolean carrier = false;

			for(int j = 0; j < copiedVal.length; j++){ //Shift bit to the right by 1
				byte LSB = (byte) (copiedVal[j] & 1); //Get the LSB of this byte

				boolean hasMSBOfByte = (copiedVal[j] & 0x80) != 0;
				copiedVal[j] = (byte) ((copiedVal[j] & 0xFF) >> 1); //Shift right by 1
				if(carrier) copiedVal[j] |= 128; //If there's a carry, set the MSB to 1

				if(LSB == 0) carrier = false;
				else carrier = true;
			}

			copiedVal[0] &= 0x7F;
		}

		return new LargeInteger(copiedVal);
	}

	private LargeInteger simpleMultiply(LargeInteger number){
		if(number == null) return null;
		byte[] numData = number.getVal();
		if(numData == null) return null;

		LargeInteger sum = new LargeInteger(null); //Start at a sum of 0
		LargeInteger thisCopy = new LargeInteger(arrayCopy(val));
		LargeInteger numCopy = new LargeInteger(arrayCopy(number.getVal()));

		while(!numCopy.isZero()){ //For every iteration, while the multiplier is not 0, keep on adding and shifting in 0s
			if(!numCopy.isEven()){ //If LSB = 1, add on the current value
				sum = sum.add(new LargeInteger(addByOneByte(thisCopy.getVal())));
			}

			thisCopy = thisCopy.shiftLeft(1);
			numCopy = numCopy.shiftRight(1);
		}

		return sum;
	}

	//Trim only 0s from a binary number
	public LargeInteger trimZeros(){
		int leadingZeroBytes = 0;
		for(int i = 0; i < val.length; i++){ //Count the number of leading bytes that have value 0
			if(val[i] == 0)
				leadingZeroBytes++;
			else
				break;
		}

		byte[] newVal = new byte[val.length-leadingZeroBytes]; //Initialize a new byte array with size of the original minus the number of leading 0 bytes

		for(int i = 0; i < newVal.length; i++){ //Copy the old data into the new array
			newVal[i] = val[i+leadingZeroBytes];
		}

		return new LargeInteger(newVal);
	}

	/*
	//Subtract two unsigned binary numbers
	public BigPrime subtract(BigPrime num){
		byte[] numData = num.getData();
		byte[] output = new byte[data.length];

		//Trim leading zeros of the binary number so a simple length comparison is an easy calculation
		BigPrime thisShort = this.trimLeadingZeros();
		BigPrime numShort = num.trimLeadingZeros();
		if(thisShort.getData().length < numShort.getData().length || (data.length == numData.length && (data[0] < numData[0]))) return new BigPrime(null); //We're subtracting a larger number from a smaller number, so the result will just be 0
		if(thisShort.compare(numShort) == -1 || thisShort.compare(numShort) == 0) return new BigPrime(null); //If a-b and a < b OR a=b, then return 0

		boolean borrow = false;
		boolean carry = false;
		for(int i = 0; i < data.length; i++){ //Iterate over the bytes in both numbers
			int currBitVal = 1; //1, then 2, then 4, then 8, then 16, and so on.  This helps grab the bit value of each byte; multiply by 2 for every iteration
			if(i == data.length-1 && data[0] == 0) continue;

			for(int j = 7; j >= 0; j--){ //Iterate through the bits in both numbers' bytes
				byte bit1 = 0;
				byte bit2 = 0;

				if(i >= numData.length){ //One binary number is longer than the other so once we reach this point, subtract the longer number from 0
					bit1 = (byte) (data[data.length-i-1] & currBitVal);
					bit2 = 0;
				} else{ //Both binary numbers are still within the correct indexable range so we don't have to worry about an ArrayIndexOutOfBoundsException
					bit1 = (byte) (data[data.length-i-1] & currBitVal);
					bit2 = (byte) (numData[numData.length-i-1] & currBitVal);
				}

				if(borrow){ //If we have to borrow a bit, turn a 1 into a 0 and perform necessary operations below
					if(bit1 != 0){ //bit1 = 1
						bit1 = 0;
						borrow = false;
					} else{ //bit1 = 0
						bit1 = (byte) currBitVal; //We can't borrow anything
					}
				}

				byte bitWrite = 0;
				if(bit1 == 0 && bit2 == 0) bitWrite = 0; // 0 - 0 = 0
				else if(bit1 != 0 && bit2 == 0) bitWrite = bit1; // 1 - 0 = 1
				else if(bit1 != 0 && bit2 != 0) bitWrite = 0; // 1 - 1 = 0
				else if(bit1 == 0 && bit2 != 0){ //0 - 1 = 1 borrow 1   --   edge case
					bitWrite = (byte) currBitVal; //Assume that this carry will work and turn this bit to 1
					borrow = true; //Borrow from the first 1 we find
				}

				output[data.length-i-1] |= bitWrite; //Flip the bit on if the bit being written is not 0

				currBitVal *= 2; //Multiply by 2 so we can get the bit in the next index of the byte
			}
		}

		return new BigPrime(output);
	}
	*/

	//Make new copy array of original byte array
	private static byte[] arrayCopy(byte[] array){
		byte[] copy = new byte[array.length];

		for(int i = 0; i < array.length; i++){
			copy[i] = array[i];
		}

		return copy;
	}

	//Make new copy array and then add 1 byte to the front with 8 leading 0 bits
	public  static byte[] addByOneByte(byte[] array){
		byte[] copy = new byte[array.length+1];

		for(int i = 0; i < array.length; i++){
			copy[i+1] = array[i];
		}

		return copy;
	}

	//Returns true if the values in two byte arrays representing primes are equal; returns false otherwise
	public boolean isPrimeEqual(LargeInteger number){
		if(number == null) return false;
		byte[] numberVal = number.getVal();
		if(val.length != numberVal.length || val == null || number == null) return false;

		int maxLength = Math.max(val.length, numberVal.length);

		for(int i = 0; i < maxLength; i++){ //Scan through the bits in the two numbers; if they differ at any point, then return false
			if(i < val.length && i < numberVal.length){ //We haven't exceeded the length of either binary number
				if(val[i] != numberVal[i]) return false;
			} else{ //At this point, one binary number is longer than the other.  So if the longer number has any 1s past this point, they're not equal
				if(i >= val.length){
					if(numberVal[i] != 0) return false;
				} else if(i >= numberVal.length){
					if(val[i] != 0) return false;
				}
			}
		}

		return true; //Scanned through both numbers completely and found no differences
	}
}
