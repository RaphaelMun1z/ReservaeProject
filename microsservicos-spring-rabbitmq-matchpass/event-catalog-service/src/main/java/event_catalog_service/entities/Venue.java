package event_catalog_service.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String city;
    private String state;
    private Integer totalCapacity;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL)
    private List<Sector> sectors;
}