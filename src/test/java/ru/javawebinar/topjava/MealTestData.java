package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final List<Meal> MEALS = Arrays.asList(
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    );

    public static int FIRST_MEAL_ID = START_SEQ + 2;
    public static Meal FIRST_MEAL = new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static LocalDate START_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static LocalDate END_DATE = LocalDate.of(2020, Month.JANUARY, 31).plusDays(1);

    public static Meal getNewMeal() {
        return new Meal(LocalDateTime.of(2020, Month.MARCH, 10, 10, 0), "Завтрак для теста", 1000);
    }

    public static Meal getUpdateMeal() {
        Meal mealUpd = new Meal(FIRST_MEAL);
        mealUpd.setId(FIRST_MEAL_ID);
        mealUpd.setCalories(600);
        mealUpd.setDescription("Обновленный завтрак");
        return mealUpd;
    }

    public static List<Meal> getMeals() {
        List<Meal> mealList = new ArrayList<>(MEALS);
        mealList.add(FIRST_MEAL);
        Collections.sort(mealList, Comparator.comparing(Meal::getDateTime).reversed());
        return mealList;
    }

    public static List<Meal> getListHalfOpen() {
        List<Meal> mealList = getMeals();
        return mealList.stream()
                .filter(meal -> Util.isBetweenHalfOpen(meal.getDate(), START_DATE, END_DATE))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "id");
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingElementComparator(Comparator.comparing(Meal::getDateTime)).isEqualTo(expected);
    }

}
