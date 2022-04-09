package jacobi_algorithm;

import file_handling.WriteFile;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Jacobi {
    static BigInteger first;
    static BigInteger second;
    static BigInteger number;
    static BigInteger seed;
    static int maximum = 1_000_000;

    /**
     * The generated numbers are two BigInteger prime numbers which are congruent to 3 mod 4.
     */
    public void generateTwoPrimeNumbers() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter how many bytes: ");
        int numBytes = Integer.parseInt(scanner.nextLine());

        first = new BigInteger(numBytes,1,new Random());
        BigInteger modulus = first.mod(BigInteger.valueOf(4));

        while(modulus.compareTo(BigInteger.valueOf(3)) != 0) {
            first = new BigInteger(numBytes,1,new Random());
            modulus = first.mod(BigInteger.valueOf(4));
        }
        System.out.println("Generated first prime number: " + first.toString());

        second = new BigInteger(numBytes,1,new Random());
        modulus = second.mod(BigInteger.valueOf(4));

        while(modulus.compareTo(BigInteger.valueOf(3)) != 0) {
            second = new BigInteger(numBytes,1,new Random());
            modulus = second.mod(BigInteger.valueOf(4));
        }
        System.out.println("Generated second prime number: " + second.toString());
    }

    /**
     * Generating seed using the system time in milliseconds. The seed is a quadratic residue mod number.
     */
    public void generatingSeed() {
        Date date = new Date();
        long time = date.getTime();

        seed = BigInteger.valueOf(time);
        seed = seed.pow(2);
        seed = seed.mod(number);

        System.out.println("Seed: " + seed);

    }

    /**
     * Method used to generate the prime numbers (P & Q), the product of them (N = P * Q) and the seed.
     */
    public void setup() {
        generateTwoPrimeNumbers();

        number = first.multiply(second);
        System.out.println("Number: " + number);

        generatingSeed();
    }

    public BigInteger jacobiSymbol(BigInteger a, BigInteger n) {
        BigInteger b = a.mod(n);
        BigInteger c = n;
        int s = 1;

        while (b.compareTo(BigInteger.TWO) >= 0) { // while b>=2
            while(b.mod(BigInteger.valueOf(4)).compareTo(BigInteger.valueOf(0)) == 0) { // while 4|b do b=b/4
                b = b.divide(BigInteger.valueOf(4));
            }
            if(b.mod(BigInteger.TWO).compareTo(BigInteger.ZERO) == 0) {  // if b mod 2 = 0 for R5)
                BigInteger cMod8 = c.mod(BigInteger.valueOf(8));
                if(cMod8.compareTo(BigInteger.valueOf(3)) == 0 || cMod8.compareTo(BigInteger.valueOf(5)) == 0) { // R5.2) c mod 8 = 3 or 5 => JS = -1
                    s = -s;
                    b = b.divide(BigInteger.TWO);
                }
            }
            if(b.compareTo(BigInteger.ONE) == 0) { // R3) b == 1
                break;
            }
            BigInteger bMod4 = b.mod(BigInteger.valueOf(4));
            BigInteger cMod4 = c.mod(BigInteger.valueOf(4));
            if(bMod4.compareTo(BigInteger.valueOf(3)) == 0 && cMod4.compareTo(BigInteger.valueOf(3)) == 0) { // R6.2) (b mod 4)== 3 ==(c mod 4)
                s = -s;
            }
            BigInteger oldB = b;
            b = c.mod(b);
            c = oldB;
        }
        return b.multiply(BigInteger.valueOf(s)); // return b*s
    }

    public String testOneOfJacobi() {
        StringBuilder sb = new StringBuilder();
        int countZeros = 0;

        BigInteger seed1 = seed;

        for(int i = 0; i < maximum; i++) {
            BigInteger jacobiSymbol = jacobiSymbol(seed1,number);

            if(jacobiSymbol.compareTo(BigInteger.valueOf(-1)) == 0 || jacobiSymbol.compareTo(BigInteger.ZERO) == 0) {
                sb.append("0");
                countZeros++;
            }
            else {
                sb.append("1");
            }

            seed1 = seed1.add(BigInteger.ONE);
        }

        System.out.println("Test 1 - counting 0 and 1: ");
        System.out.println("#0: " + countZeros);
        System.out.println("#1: " + (maximum - countZeros) + '\n');

        return sb.toString();
    }

    public void generateZeroOneSequence() {
        String uncompressedZeroOne = "01".repeat(Math.max(0, maximum/2));
        long lengthUncompressedZeroOne = uncompressedZeroOne.length();

        System.out.println("Sequence \"0101..01\" size: " + lengthUncompressedZeroOne + " bytes.");

        WriteFile wf = new WriteFile("01Sequence.txt");
        wf.printToFile(uncompressedZeroOne);
    }

    public void generateOneSequence() {
        String uncompressedOne = "1".repeat(Math.max(0, maximum));
        long lengthUncompressedOne = uncompressedOne.length();

        System.out.println("Sequence \"1111...111\" size: " + lengthUncompressedOne + " bytes.");

        WriteFile wf = new WriteFile("1Sequence.txt");
        wf.printToFile(uncompressedOne);
    }

    public void testTwoOfJacobi(String uncompressed) {
        WriteFile wf = new WriteFile("jacobi.txt");
        wf.printToFile(uncompressed);

        File uncompressedFile = new File("jacobi.txt");
        long fileSize = uncompressedFile.length();
        System.out.println("Uncompressed file size: " + fileSize + " bytes.");

        generateZeroOneSequence();
        generateOneSequence();
    }

    public static void startApp() {
        System.out.println("Jacobi Generator...");
        Jacobi jacobi = new Jacobi();
        jacobi.setup();
        String uncompressed = jacobi.testOneOfJacobi();
        jacobi.testTwoOfJacobi(uncompressed);
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        startApp();
        long stopTime = System.nanoTime();
        long elapsedTime = stopTime - startTime;
        double elapsedTimeInSecond= (double) elapsedTime / 1_000_000_000;
        System.out.println("Elapsed time: " + elapsedTimeInSecond + " seconds. ");
    }
}
