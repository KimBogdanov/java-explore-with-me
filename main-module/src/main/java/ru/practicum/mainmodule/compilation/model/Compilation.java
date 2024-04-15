package ru.practicum.mainmodule.compilation.model;

import lombok.*;
import ru.practicum.mainmodule.event.model.Event;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"id", "events"})
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "compilations")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "compilations_id"),
            inverseJoinColumns = @JoinColumn(name = "events_id"))
    private List<Event> events;
}
