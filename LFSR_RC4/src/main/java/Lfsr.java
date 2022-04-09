import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Lfsr {
    List<Integer> number;

    public Lfsr() {
        this.number = new ArrayList<>();
    }

    public static String baseConversion(String number, int sBase, int dBase)
    {
        return Integer.toString(Integer.parseInt(number, sBase), dBase);
    }

    /**
     * Method used for generating a random number between 1 and (2^16)-1 written in base 2 containing 16 bits.
     */
    public void generateNumber() {
        Random r = new Random();
        int low = 1;
        int high = (int)(Math.pow(2,16) - 1);
        int nr = r.nextInt(high - low) + low;
        String base2Nr = baseConversion(Integer.toString(nr),10,2);

        System.out.println("Limits: [" + low + ", " + high + ']');
        System.out.print("Base10: " + nr + " -> ");
        System.out.println("Base2: " + base2Nr);

        for(int i = 0; i < base2Nr.length(); i++) {
            int numericVal = Character.getNumericValue(base2Nr.charAt(i));
            number.add(numericVal);
        }
        while(number.size() < 16) {
            number.add(0,0);
        }
        System.out.println("Number: " + number);
    }

    /**
     * Primitive pentanomial used in algorithm: x^16 + x^5 + x^3 + x^2 + 1.<br>
     * This pentanomial is used to determine which positions are needed to calculate the new bit using XOR (sum of bits mod 2).<br>
     * Finally, the last bit (16th bit) is removed and the list is right-shifted.<br>
     * The algorithm stops when the copy of the list is equal to the first list generated.<br>
     * The algorithm is correct only if the counter of steps is equal to 2^16 - 1.<br>
     */
    public void lfsrAlgorithm() {
        int counter = 0;
        List<Integer> copy = new ArrayList<>(number);
        while(true) {
            System.out.print(copy.get(15));

            int secondBit = copy.get(1);
            int thirdBit = copy.get(2);
            int fifthBit = copy.get(4);
            int lastBit = copy.get(15);
            int newBit = (secondBit + thirdBit + fifthBit + lastBit) % 2;

            copy.remove(15);
            copy.add(0, newBit);
            counter++;
            if(copy.equals(number)) {
                break;
            }
        }
        System.out.print("\nStopped after: " + counter + " steps. ");
        if(counter == ((int)(Math.pow(2,16) - 1))) {
            System.out.println("Correct!");
        }
        else {
            System.out.println("Incorrect!");
        }
    }

    public static void startApp() {
        System.out.println("LFSR Generator...");
        Lfsr lfsr = new Lfsr();
        lfsr.generateNumber();
        lfsr.lfsrAlgorithm();
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
