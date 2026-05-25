package event_catalog_service.entities;

import event_catalog_service.entities.enums.EventStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "tb_events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O título do evento não pode estar em branco")
    @Size(min = 5, max = 150, message = "O título deve ter entre 5 e 150 caracteres")
    @Column(nullable = false, length = 150)
    private String title;

    @NotNull
    @FutureOrPresent(message = "A data do evento deve ser no presente ou futuro")
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatusEnum status;

    @NotNull
    @Column(name = "venue_id", nullable = false)
    private UUID venueId;

    @NotNull
    @Column(name = "home_team_id", nullable = false)
    private UUID homeTeamId;

    @NotNull
    @Column(name = "away_team_id", nullable = false)
    private UUID awayTeamId;

    @NotNull
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<EventSectorPricing> pricings = new ArrayList<>();

    protected Event() {
    }

    public Event(String title, LocalDateTime eventDate, UUID venueId, UUID homeTeamId, UUID awayTeamId) {
        this.title = title;
        this.eventDate = eventDate;
        this.status = EventStatusEnum.SCHEDULED;
        this.venueId = venueId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public EventStatusEnum getStatus() {
        return status;
    }

    public UUID getVenueId() {
        return venueId;
    }

    public UUID getHomeTeamId() {
        return homeTeamId;
    }

    public UUID getAwayTeamId() {
        return awayTeamId;
    }

    public List<EventSectorPricing> getPricings() {
        return Collections.unmodifiableList(pricings);
    }

    // --- MÉTODOS DE COMPORTAMENTO DE NEGÓCIO ---

    public void cancelEvent() {
        if (this.status == EventStatusEnum.FINISHED) {
            throw new IllegalStateException("Não é possível cancelar um evento já finalizado.");
        }

        this.status = EventStatusEnum.CANCELED;
    }

    public void addPricing(EventSectorPricing pricing) {
        pricing.setEvent(this);
        this.pricings.add(pricing);
    }

    public void removePricing(EventSectorPricing pricing) {
        this.pricings.remove(pricing);
        pricing.setEvent(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return id != null && id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
