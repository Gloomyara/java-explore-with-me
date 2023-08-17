package ru.practicum.event.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.category.model.Category;
import ru.practicum.event.enums.State;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long participantLimit;

    private String title;
    private String annotation;
    private String description;

    private LocalDateTime createdOn;
    private LocalDateTime publishedOn;
    private LocalDateTime eventDate;

    private boolean requestModeration;
    private boolean paid;

    @Enumerated(EnumType.STRING)
    private State state;

    @Embedded
    private Location location;

    @ManyToOne
    private Category category;

    @ManyToOne
    private User initiator;

    @Transient
    private long views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return id != null && Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
