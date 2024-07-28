package ru.javawebinar.topjava;

import org.assertj.core.api.Assertions;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;
public class MealTestData {
    public static final int MEAL_ID1 = START_SEQ + 3;
    public static final int MEAL_ID2 = START_SEQ + 4;
    public static final int MEAL_ID3 = START_SEQ + 5;
    public static final int NOT_FOUND = 10;

    public static final Meal meal1 = new Meal(MEAL_ID1, LocalDateTime.of(2024, 1, 1, 10, 34, 13), "Завтрак", 1000 );
    public static final Meal meal2 = new Meal(MEAL_ID2, LocalDateTime.of(2024, 1, 1, 13, 0, 0), "Обед", 700 );
    public static final Meal meal3 = new Meal(MEAL_ID3, LocalDateTime.of(2024, 1, 1, 17, 34, 13), "Ужин", 700 );

    public static Meal getNew() {
        return new Meal( LocalDateTime.of(2024, 1, 2, 10, 0, 0), "Завтрак", 334 );
    }

    public static Meal getUpdated() {
        Meal meal = new Meal(meal1);
        meal.setDescription("Обновленная запись");
        return meal;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        Assertions.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

}
