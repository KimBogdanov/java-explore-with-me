package ru.practicum.mainmodule.request.model;

import lombok.*;
import ru.practicum.mainmodule.event.model.Event;
import ru.practicum.mainmodule.request.model.enums.RequestStatus;
import ru.practicum.mainmodule.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;
    @Enumerated
    @JoinColumn(name = "status", nullable = false)
    private RequestStatus status;
}
