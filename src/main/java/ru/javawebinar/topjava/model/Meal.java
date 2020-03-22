package ru.javawebinar.topjava.model;

import ru.javawebinar.topjava.util.LocalDateTimePersistenceConverter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NamedQueries({
        @NamedQuery(name = Meal.DELETE,             query = "DELETE FROM Meal WHERE id=:id AND user=:user"),
        @NamedQuery(name = Meal.GET,                query = "SELECT m FROM Meal m WHERE m.id=:id AND m.user=:user"),
        @NamedQuery(name = Meal.GET_ALL,            query = "SELECT m FROM Meal m WHERE m.user=:user ORDER BY m.dateTime DESC"),
        @NamedQuery(name = Meal.GET_BETWEEN_OPEN,   query = "SELECT m FROM Meal m WHERE m.dateTime>=:start_time AND m.dateTime<:end_time AND m.user=:user ORDER BY m.dateTime DESC")
})

@Entity
@Table(name = "meals", uniqueConstraints = @UniqueConstraint(columnNames = {"date_time", "user_id"}, name = "meals_unique_idx"))
public class Meal extends AbstractBaseEntity {
    public static final String DELETE = "Meal.delete";
    public static final String GET = "Meal.get";
    public static final String GET_ALL = "Meal.get_all";
    public static final String GET_BETWEEN_OPEN = "Meal.getBetweenHalfOpen";

    @Column(name = "date_time",nullable = false, columnDefinition = "timestamp default now()")
    private LocalDateTime dateTime;

    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @Column(name = "calories", nullable = false)
    @Min(value = 0)
    private int calories;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
