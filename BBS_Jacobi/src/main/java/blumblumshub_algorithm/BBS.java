package blumblumshub_algorithm;

import blumblumshub_algorithm.compression.Lzw;
import file_handling.WriteFile;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;
import java.util.Date;
import java.util.Scanner;

public class BBS {
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

    /**
     * The method is used for the first test of the Blum-Blum-Shub algorithm.
     * <br>This first test is an elementary test that counts how many bytes of 0 and 1 are. The pseudo-random generated number is relatively correct if the number of bytes 0 is close to the number of bytes 1.
     * <br>First, a quadratic residue is needed (= pow(seed,2) mod N ). The result is further used in a loop for calculating the bytes and appending them to a StringBuilder.
     *
     * @return      A string containing a large amount of 0 and 1 bytes depending on the parity of the modulus of the quadratic residue.
     */
    public String testOneOfBBS() {
        StringBuilder sb = new StringBuilder();
        int countZeros = 0;

        BigInteger result = seed.pow(2);
        result = result.mod(number);
        BigInteger modulus = result.mod(BigInteger.valueOf(2));

        if(modulus.toString().equals("0")) {
            sb.append('0');
            countZeros++;
        }
        else {
            sb.append('1');
        }

        for(int i = 1; i < maximum; i++) {
            result = result.pow(2);
            result = result.mod(number);

            modulus = result.mod(BigInteger.valueOf(2));
            if (modulus.toString().equals("0")) {
                sb.append('0');
                countZeros++;
            } else {
                sb.append('1');
            }
        }

        System.out.println("Test 1 - counting 0 and 1: ");
        System.out.println("#0: " + countZeros);
        System.out.println("#1: " + (maximum - countZeros) + '\n');

        return sb.toString();
    }

    /**
     * The method is used for calculating the space-saving ratio and blumblumshub_algorithm.compression ratio of two strings by using their lengths.
     *
     * @param uncompressed  The uncompressed string.
     */
    public void testTwoOfBbsUsingLZW(String uncompressed) {
        int lengthUncompressed = uncompressed.length();
        System.out.println("Uncompressed length: " + lengthUncompressed);

        Lzw lzw = new Lzw();
        String compressed = lzw.compress(uncompressed);
        int lengthCompressed = compressed.length();
        System.out.println("Compressed length: " + lengthCompressed);

        float compressionRatio = (float)lengthUncompressed / lengthCompressed;
        float spaceSaving = 1 - ((float)lengthCompressed / lengthUncompressed);

        System.out.println("Compression ratio: " + compressionRatio + ", Saving " + (spaceSaving*100) + "% of space.");

        StringBuilder zeroOne = new StringBuilder();
        for(int i = 0; i < maximum; i++) {
            if (i % 2 == 0) {
                zeroOne.append("1");
            }
            else {
                zeroOne.append("0");
            }
        }

        String uncompressedZeroOne = zeroOne.toString();
        int lengthUncompressedZeroOne = uncompressedZeroOne.length();

        String compressedZeroOne = lzw.compress(uncompressedZeroOne);
        int lengthCompressedZeroOne = compressedZeroOne.length();

        System.out.println("\nUncompressed length: " + lengthUncompressedZeroOne);
        System.out.println("Compressed length: " + lengthCompressedZeroOne);

        float compressionRatioZeroOne = (float)lengthUncompressedZeroOne / lengthCompressedZeroOne;
        float spaceSavingZeroOne = 1 - ((float)lengthCompressedZeroOne / lengthUncompressedZeroOne);

        System.out.println("Compression ratio: " + compressionRatioZeroOne + ", Saving " + (spaceSavingZeroOne*100) + "% of space.");
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

    public void testTwoBBS(String uncompressed) {
        WriteFile wf = new WriteFile("bbs.txt");
        wf.printToFile(uncompressed);

        File uncompressedFile = new File("bbs.txt");
        long fileSize = uncompressedFile.length();
        System.out.println("Uncompressed file size: " + fileSize + " bytes.");

        generateZeroOneSequence();
        generateOneSequence();
    }

    public static void startApp() {
        System.out.println("BBS Generator...");
        BBS bbsAlgorithm = new BBS();
        bbsAlgorithm.setup();

        String uncompressed = bbsAlgorithm.testOneOfBBS();

        //bbsAlgorithm.testTwoOfBbsUsingLZW(uncompressed);
        bbsAlgorithm.testTwoBBS(uncompressed);
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
