package consumer_app.prime_numbers.algorithms;

import java.util.ArrayList;
import java.util.List;

public class SimpleSearchAlgorithm implements PrimesSearchAlgorithm {
    @Override
    public List<Integer> searchPrimes(int value) {
        List<Integer> primeNumbers = new ArrayList<>();
        for (int i = 2; i <= value; ++i) {
            if (isPrime(i)) {
                primeNumbers.add(i);
            }
        }

        return primeNumbers;
    }

    private boolean isPrime (int number) {
        int sqrtNumber = (int) (Math.sqrt(number));
        for (int i = 2; i <= sqrtNumber; ++i) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
}
