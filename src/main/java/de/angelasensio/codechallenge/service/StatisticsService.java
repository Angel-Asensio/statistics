package de.angelasensio.codechallenge.service;

import de.angelasensio.codechallenge.model.Stats;
import de.angelasensio.codechallenge.model.Transaction;

public interface StatisticsService {

    boolean addTransaction(Transaction transaction);

    Stats computeStatistics();
}