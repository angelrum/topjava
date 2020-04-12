package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.web.meal.MealRestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping("/meals")
public class JspMealController {
    @Autowired
    private MealRestController controller;

    @GetMapping
    public String getAllMeals(Model model) {

        model.addAttribute("meals", controller.getAll());
        return "meals";
    }

    @GetMapping(params = "action")
    public String getAllFithFilter(@RequestParam String action,
                                   @RequestParam String startDate,
                                   @RequestParam String endDate,
                                   @RequestParam String startTime,
                                   @RequestParam String endTime,
                                   Model model) {
        if (!"filter".equals(action) ||
                ( StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate) &&
                        StringUtils.isEmpty(startTime) && StringUtils.isEmpty(endTime)))
            return "redirect:/meals";
        LocalDate startLocalDate = parseLocalDate(startDate);
        LocalDate endLocalDate = parseLocalDate(endDate);
        LocalTime startLocalTime = parseLocalTime(startTime);
        LocalTime endLocalTime = parseLocalTime(endTime);
        model.addAttribute("meals", controller.getBetween(startLocalDate, startLocalTime,endLocalDate, endLocalTime));
        return "meals";
    }

    @GetMapping(value = {"/create", "/update"})
    public String createMeal(@RequestParam(required = false) Integer id,
                             Model model) {
        final Meal meal = Objects.isNull(id) ?
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                controller.get(id);
        model.addAttribute("meal", meal);
        model.addAttribute("action", Objects.isNull(id) ? "create" : "update");
        return "mealForm";
    }


    @GetMapping("/delete")
    public String deleteMeal(@RequestParam Integer id) {
        controller.delete(id);
        return "redirect:/meals";
    }

    @PostMapping
    public String saveMeal(@RequestParam Integer id,
                           @RequestParam String description,
                           @RequestParam String dateTime,
                           @RequestParam Integer calories) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime);
        Meal meal = new Meal(localDateTime, description, calories);
        if (Objects.isNull(id))
            controller.create(meal);
        else
            controller.update(meal, id);

        return "redirect:meals";
    }

}
