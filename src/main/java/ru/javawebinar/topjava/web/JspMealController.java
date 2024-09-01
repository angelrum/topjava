package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.*;
import static org.springframework.util.StringUtils.*;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.util.MealsUtil.*;
import static ru.javawebinar.topjava.web.SecurityUtil.*;

@Controller
public class JspMealController {
    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private final MealService service;

    public JspMealController(MealService service) {
        this.service = service;
    }

    @GetMapping("/meals")
    public String getMealsByFilter( @RequestParam(value = "action", required = false) String action,
                                    @RequestParam(value = "id", required = false) Integer id,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate,
                                    @RequestParam(value = "startTime", required = false) String startTime,
                                    @RequestParam(value = "endTime", required = false) String endTime,
                                    Model model) {
        int userId = authUserId();
        String page = "meals";
        switch (isNull(action) ? "all" : action) {
            case "filter" -> {
                LocalDate lStartDate = parseLocalDate(startDate);
                LocalDate lEndDate = parseLocalDate(endDate);
                LocalTime lStartTime = parseLocalTime(startTime);
                LocalTime lEndTime = parseLocalTime(endTime);
                List<Meal> mealsDateFiltered = service.getBetweenInclusive(lStartDate, lEndDate, userId);
                model.addAttribute("meals",
                        getFilteredTos(mealsDateFiltered, SecurityUtil.authUserCaloriesPerDay(), lStartTime, lEndTime));
            }
            case "delete" -> {
                service.delete(id, userId);
                page = "redirect:meals";
            }
            case "create" -> {
                final Meal meal = new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000);
                model.addAttribute("meal", meal);
                page = "mealForm";
            }
            case "update" -> {
                final Meal meal = service.get(id, userId);
                model.addAttribute("meal", meal);
                page = "mealForm";
            }
            default -> {
                log.info("Get meals for user {}", userId);
                model.addAttribute("meals", getTos(service.getAll(userId), userId));
            }
        }
        return page;
    }

    @PostMapping("/meals")
    public String postMethod(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (hasLength(request.getParameter("id"))) {
            create(meal, request);
        } else {
            update(meal);
        }
        return "redirect:meals";
    }

    private void create(Meal meal, HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        meal.setId(id);
        log.info("Update meal with id={} for userId={}", id, authUserId());
        service.update(meal, authUserId());
    }

    private void update(Meal meal) {
        log.info("Create new meal for userId={}", authUserId());
        service.create(meal, authUserId());
    }

}
