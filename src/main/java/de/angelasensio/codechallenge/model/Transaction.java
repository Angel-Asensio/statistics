package de.angelasensio.codechallenge.model;

import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
    private Double amount;
    private Long timestamp;
    private ZonedDateTime utcTime;
}
