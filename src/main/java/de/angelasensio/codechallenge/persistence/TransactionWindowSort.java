package de.angelasensio.codechallenge.persistence;

import de.angelasensio.codechallenge.model.Stats;
import de.angelasensio.codechallenge.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class TransactionWindowSort {

    /**
     * We will be adding transactions to this special map without requiring any explicit synchronization.
     * The ConcurrentSkipListMap will sort these transactions using the Comparator that was passed to the
     * constructor.
     */
    private final ConcurrentNavigableMap<ZonedDateTime, Double> transactions
            = new ConcurrentSkipListMap<>(Comparator.comparingLong((ZonedDateTime value) -> value.toInstant().toEpochMilli()));

    public void addTransaction(Transaction event) {
        transactions.put(event.getUtcTime(), event.getAmount());
    }

    /**
     * tailMap returns an immutable snapshot of the data in a lock-free way. To get the transactions
     * that arrived within the last minute, we only need to pass the time frame we want to get. In this case,
     * one minute
     */
    private ConcurrentNavigableMap<ZonedDateTime, Double> getTransactionsFromLastMinute() {
        return transactions.tailMap(ZonedDateTime.now().minusMinutes(1));
    }


    public Stats computeStatistics() {
        ConcurrentNavigableMap<ZonedDateTime, Double> transactionsFromLastMinute = getTransactionsFromLastMinute();

        DoubleSummaryStatistics doubleSummaryStatistics = transactionsFromLastMinute.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .summaryStatistics();

        return new Stats(doubleSummaryStatistics.getSum(),
                doubleSummaryStatistics.getAverage(),
                doubleSummaryStatistics.getMax() == Double.NEGATIVE_INFINITY ? 0d : doubleSummaryStatistics.getMax(),
                doubleSummaryStatistics.getMin() == Double.POSITIVE_INFINITY ? 0d : doubleSummaryStatistics.getMin(),
                doubleSummaryStatistics.getCount());
    }
}