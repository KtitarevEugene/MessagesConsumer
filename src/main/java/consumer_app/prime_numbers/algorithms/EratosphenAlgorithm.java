package consumer_app.prime_numbers.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EratosphenAlgorithm implements PrimesSearchAlgorithm {

    @Override
    public List<Integer> searchPrimes(int value) {
        List<Integer> primeNumbers = new ArrayList<>();

        boolean[] primes = new boolean [value + 1];
        Arrays.fill(primes, true);

        primes[0] = false;
        primes[1] = false;

        for (int i = 2; i < primes.length; ++i) {
            if (primes[i]) {
                for (int j = 2; i * j < primes.length; ++j) {
                    primes[i * j] = false;
                }
            }
        }

        for (int i = 2; i < primes.length; ++i) {
            if (primes[i]) {
                primeNumbers.add(i);
            }
        }

        return primeNumbers;
    }
}
