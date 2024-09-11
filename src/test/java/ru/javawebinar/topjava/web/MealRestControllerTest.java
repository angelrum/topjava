package ru.javawebinar.topjava.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static java.time.format.DateTimeFormatter.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.web.json.JsonUtil.readValues;
import static ru.javawebinar.topjava.web.json.JsonUtil.writeValue;

public class MealRestControllerTest extends AbstractControllerTest {
    @Autowired
    protected MealService service;
    private final static String MEAL_REST_URL = "/rest/meals";

    @Test
    void testGetAll() throws Exception {
        perform(get(MEAL_REST_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(result -> {
                    List<MealTo> mealTos = readValues(result.getResponse().getContentAsString(), MealTo.class);
                    MEAL_MATCHER.assertMatch(convertTOtoObject(mealTos), meals);
                });
    }

    @Test
    void testGet() throws Exception {
        String url = MEAL_REST_URL + "/" + MEAL1_ID;
        perform(get(url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(MEAL_MATCHER.contentJson(meal1));
    }

    @Test
    void testDelete() throws Exception {
        String url = MEAL_REST_URL + "/" + MEAL1_ID;
        perform(delete(url))
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, USER_ID));
    }

    @Test
    void testUpdate() throws Exception {
        Meal updated = getUpdated();
        String url = MEAL_REST_URL + "/" + updated.id();
        perform(put(url)
                .contentType(APPLICATION_JSON).content(writeValue(updated)))
                .andDo(print())
                .andExpect(status().isNoContent());
        MEAL_MATCHER.assertMatch(service.get(updated.id(), USER_ID), updated);
    }

    @Test
    void testCreate() throws Exception {
        Meal created = getNew();
        perform(post(MEAL_REST_URL).contentType(APPLICATION_JSON).content(writeValue(created)))
                .andDo(print())
                .andExpect(result -> {
                    List<Meal> meals = readValues(result.getResponse().getContentAsString(), Meal.class);
                    Meal meal = meals.get(0);
                    created.setId(meal.id());
                    MEAL_MATCHER.assertMatch(meal, created);
                });
    }

    @Test
    void testGetByFilter() throws Exception {
        String url = MEAL_REST_URL + "/filter";
        LocalDateTime startDate = LocalDate.of(2020, Month.JANUARY, 30).atStartOfDay();
        LocalDateTime endDate = LocalDate.of(2020, Month.JANUARY, 30).atTime(23, 59);
        perform(get(url)
                .queryParam("start", startDate.format(ISO_LOCAL_DATE_TIME))
                .queryParam("end", endDate.format(ISO_LOCAL_DATE_TIME)))
                .andDo(print())
                .andExpect(result -> {
                    List<MealTo> mealTos = readValues(result.getResponse().getContentAsString(), MealTo.class);
                    MEAL_MATCHER.assertMatch(convertTOtoObject(mealTos), meal3, meal2, meal1);
                });
    }

    @Test
    void testGetBetween() throws Exception {
        String url = MEAL_REST_URL + "/between";
        LocalDate date = LocalDate.of(2020, Month.JANUARY, 30);
        perform(get(url)
                .queryParam("startDate", date.format(ISO_DATE))
                .queryParam("starTime", LocalTime.MIN.format(ISO_TIME))
                .queryParam("endDate", date.format(ISO_DATE))
                .queryParam("endTime", LocalTime.MAX.format(ISO_TIME)))
                .andDo(print())
                .andExpect(result -> {
                    List<MealTo> mealTos = readValues(result.getResponse().getContentAsString(), MealTo.class);
                    MEAL_MATCHER.assertMatch(convertTOtoObject(mealTos), meal3, meal2, meal1);
                });
    }
}
