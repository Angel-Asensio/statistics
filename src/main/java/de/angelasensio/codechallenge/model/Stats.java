package de.angelasensio.codechallenge.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Stats {
    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Long count;
}
