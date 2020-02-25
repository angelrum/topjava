package ru.javawebinar.topjava.web.meal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;


@Controller
public class MealRestController {

    private MealService service;

    @Autowired
    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal get(int id) throws NotFoundException {
        return service.get(authUserId(), id);
    }

    public Meal create (Meal meal) {
        return service.create(authUserId(), meal);
    }

    public void update(Meal meal, int id) throws NotFoundException {
        try {
            ValidationUtil.assureIdConsistent(meal, id);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(e.getMessage());
        }
        service.update(authUserId(), meal);
    }

    public void delete(int id) throws NotFoundException {
        service.delete(authUserId(), id);
    }

    public List<MealTo> getAll() {
        return getAll(null, null, null, null);
    }

    public List<MealTo> getAll(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        Collection<Meal> mealCollection = service.getAll(authUserId());
        startDate   = Objects.isNull(startDate) ? LocalDate.MIN : startDate;
        endDate     = Objects.isNull(endDate)   ? LocalDate.MAX : endDate;
        startTime   = Objects.isNull(startTime) ? LocalTime.MIN : startTime;
        endTime     = Objects.isNull(endTime)   ? LocalTime.MAX : endTime;
        return MealsUtil.getFilteredTosByDateTime(mealCollection, authUserCaloriesPerDay(), startDate, startTime, endDate, endTime);
    }
}