package consumer_app.prime_numbers.strategies_context;

import com.sun.istack.NotNull;
import consumer_app.prime_numbers.algorithms.PrimesSearchAlgorithm;

import java.util.List;

public class PrimeNumbers implements PrimesSearch {

    private PrimesSearchAlgorithm algorithm;

    public PrimeNumbers(@NotNull PrimesSearchAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public List<Integer> getPrimeNumbers(int value) {
        return algorithm.searchPrimes(value);
    }
}
