package ru.practicum.mainmodule.admin.location.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "id")
@Setter
@Getter
@ToString
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(name = "description", length = 7000)
    private String description;
    @Column(name = "latitude", nullable = false)
    private Double lat;
    @Column(name = "longitude", nullable = false)
    private Double lot;
}
