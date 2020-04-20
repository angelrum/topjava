package ru.javawebinar.topjava.web.meal;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.NestedServletException;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.AbstractControllerTest;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.javawebinar.topjava.TestUtil.readFromJson;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

class MealRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = MealRestController.REST_URL + '/';
    private static final String REST_URL_DELETE = REST_URL + "/delete/";
    private static final String REST_URL_CREAT = REST_URL + "/create/";
    private static final String REST_URL_UPDATE = REST_URL + "/update/";
    private static final String REST_URL_FILTER = REST_URL + "/filter/";

    @Autowired
    private MealService service;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(getMealToList()));
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL_DELETE + MEAL1_ID))
                .andDo(print())
                .andExpect(status().isOk());
        assertThrows(NotFoundException.class, ()-> service.get(MEAL1_ID, USER_ID));
    }

    @Test
    void create() throws Exception {
        Meal newMeal = getNew();
        ResultActions action = perform(MockMvcRequestBuilders.post(REST_URL_CREAT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newMeal)))
                .andExpect(status().isCreated());
        Meal created = readFromJson(action, Meal.class);
        newMeal.setId(created.id());
        MEAL_MATCHER.assertMatch(created, newMeal);
        MEAL_MATCHER.assertMatch(service.get(created.id(), USER_ID), newMeal);
    }

    @Test
    void update() throws Exception {
        Meal update = getUpdated();

        perform(MockMvcRequestBuilders.put(REST_URL_UPDATE + MEAL1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(update)))
                .andExpect(status().isNoContent());

        MEAL_MATCHER.assertMatch(service.get(MEAL1_ID, USER_ID), update);
    }

    @Test
    void updateNotFound() throws Exception {
        Meal update = getUpdated();
        update.setId(100000100);

        assertThrows(NestedServletException.class, () -> {
            perform(MockMvcRequestBuilders.put(REST_URL_UPDATE + update.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.writeValue(update)))
                    .andExpect(status().isInternalServerError());
        });
    }

    @Test
    void getBetweenInclusive() throws Exception {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("startDate", LocalDate.of(2020, Month.JANUARY, 30).toString());
        requestParams.add("endDate", LocalDate.of(2020, Month.JANUARY, 30).toString());

        perform(MockMvcRequestBuilders.get(REST_URL_FILTER)
                .params(requestParams)).andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MEAL_TO_MATCHER.contentJson(getMealToList(MEAL3, MEAL2, MEAL1)));
    }

}