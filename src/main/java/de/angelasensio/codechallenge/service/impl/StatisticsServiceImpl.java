package de.angelasensio.codechallenge.service.impl;

import de.angelasensio.codechallenge.persistence.TransactionWindowSort;
import de.angelasensio.codechallenge.model.Stats;
import de.angelasensio.codechallenge.model.Transaction;
import de.angelasensio.codechallenge.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final TransactionWindowSort transactionWindowSort;

    @Override
    public boolean addTransaction(Transaction transaction) {
        ZonedDateTime zonedDateTimeFromTransaction = getZonedDateTimeFromTimestamp(transaction.getTimestamp());
        transaction.setUtcTime(zonedDateTimeFromTransaction);

        ZonedDateTime nowUTC = Instant.now().atZone(ZoneOffset.UTC);
        Duration duration = Duration.between(zonedDateTimeFromTransaction, nowUTC);
        if (duration.getSeconds() > 60) {
            log.info("transaction rejected because is older than 60 seconds: {} - currentTime:{}", transaction.toString(), nowUTC);
            return false;
        }
        log.info("add transaction: {}", transaction.toString());
        transactionWindowSort.addTransaction(transaction);
        return true;
    }

    @Override
    public Stats computeStatistics() {
        Stats stats = transactionWindowSort.computeStatistics();
        log.info("statistics: {}", stats.toString());
        return stats;
    }

    private ZonedDateTime getZonedDateTimeFromTimestamp(Long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.UTC);
    }
}
