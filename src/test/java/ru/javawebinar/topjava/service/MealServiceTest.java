package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;


@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml",
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal actual = service.get(FIRST_MEAL_ID, USER_ID);
        assertMatch(actual, FIRST_MEAL);
    }

    @Test(expected = NotFoundException.class)
    public void delete() {
        service.delete(FIRST_MEAL_ID, USER_ID);
        service.get(FIRST_MEAL_ID, USER_ID);
    }

    @Test
    public void getBetweenHalfOpen() {
        List<Meal> mealList = service.getBetweenHalfOpen(START_DATE, END_DATE, USER_ID);
        assertMatch(mealList, getListHalfOpen());
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), getMeals());
    }

    @Test
    public void update() {
        Meal update = getUpdateMeal();
        service.update(update, USER_ID);
        assertMatch(service.get(update.getId(), USER_ID), update);
    }

    @Test
    public void create() {
        Meal newMeal = getNewMeal();
        Meal created = service.create(newMeal, USER_ID);
        assertMatch(created, newMeal);
    }

    @Test(expected = NotFoundException.class)
    public void getWithException() {
        Meal actual = service.get(FIRST_MEAL_ID, ADMIN_ID);
        assertMatch(actual, FIRST_MEAL);
    }

    @Test(expected = NotFoundException.class)
    public void deleteWithException() {
        service.delete(FIRST_MEAL_ID, ADMIN_ID);
    }

    @Test(expected = NotFoundException.class)
    public void updateWithException() {
        service.update(getUpdateMeal(), ADMIN_ID);
    }
}