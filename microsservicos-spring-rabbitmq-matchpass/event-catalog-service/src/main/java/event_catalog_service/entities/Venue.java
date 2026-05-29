package event_catalog_service.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_venues")
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "O nome do local é obrigatório")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres")
    @Column(nullable = false, length = 150)
    private String name;

    @NotBlank(message = "A cidade é obrigatória")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "A sigla do estado é obrigatória")
    @Size(min = 2, max = 2, message = "O estado deve conter exatamente 2 caracteres (ex: SP, MG)")
    @Column(nullable = false, length = 2)
    private String state;

    @NotNull
    @Positive(message = "A capacidade total deve ser maior que zero")
    @Column(name = "total_capacity", nullable = false)
    private Integer totalCapacity;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sector> sectors = new ArrayList<>();

    protected Venue() {
    }

    public Venue(String name, String city, String state, Integer totalCapacity) {
        this.name = name;
        this.city = city;
        this.state = state.toUpperCase();
        this.totalCapacity = totalCapacity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public List<Sector> getSectors() {
        return sectors;
    }

    // --- MÉTODOS DE COMPORTAMENTO ---

    public void addSector(Sector sector) {
        sector.setVenue(this);
        this.sectors.add(sector);
    }

    public void addMultipleSectors(List<Sector> sectors) {
        this.sectors.addAll(sectors);
    }

    public void removeSector(Sector sector) {
        this.sectors.remove(sector);
        sector.setVenue(null);
    }

    public void updateLocation(String city, String state) {
        if (city != null && !city.isBlank()) this.city = city;
        if (state != null && state.length() == 2) this.state = state.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venue venue)) return false;
        return id != null && id.equals(venue.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}