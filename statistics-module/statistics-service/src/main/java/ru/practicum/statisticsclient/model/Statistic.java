package ru.practicum.statisticsclient.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"id"})
@Entity
@Table(name = "statistics")
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app")
    private String app;

    @Column(name = "ip")
    private String ip;

    @Column(name = "create")
    private LocalDateTime create;

    @Column(name = "uri")
    private String uri;
}
