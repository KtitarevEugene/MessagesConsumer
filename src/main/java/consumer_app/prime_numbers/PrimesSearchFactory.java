package consumer_app.prime_numbers;

import consumer_app.prime_numbers.algorithms.EratosphenAlgorithm;
import consumer_app.prime_numbers.algorithms.SimpleSearchAlgorithm;
import consumer_app.prime_numbers.strategies_context.PrimeNumbers;
import consumer_app.prime_numbers.strategies_context.PrimesSearch;

public class PrimesSearchFactory {

    private PrimesSearchFactory () {}

    public static PrimesSearch eratosphenAlgorithm() {
        return new PrimeNumbers(new EratosphenAlgorithm());
    }

    public static PrimesSearch simpleSearchAlgorithm() {
        return new PrimeNumbers(new SimpleSearchAlgorithm());
    }
}
