package de.angelasensio.codechallenge.controller;


import com.google.common.base.Preconditions;
import de.angelasensio.codechallenge.model.Stats;
import de.angelasensio.codechallenge.model.Transaction;
import de.angelasensio.codechallenge.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
class StatisticsController {

    private final StatisticsService service;

    @RequestMapping(value = "/transactions", method = RequestMethod.POST)
    ResponseEntity<?> addTransaction(@RequestBody Transaction transaction) {
        validateTransaction(transaction);
        return service.addTransaction(transaction) ? new ResponseEntity<>(HttpStatus.CREATED)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    Stats retrieveStatistics() {
        return service.computeStatistics();
    }

    private void validateTransaction(final Transaction transaction) {
        Objects.requireNonNull(transaction);
        Objects.requireNonNull(transaction.getAmount());
        Objects.requireNonNull(transaction.getTimestamp());
        Preconditions.checkArgument(transaction.getAmount() > 0, "amount must be > 0");
        Preconditions.checkArgument(transaction.getTimestamp() > 0, "timestamp must be > 0");
    }


}
