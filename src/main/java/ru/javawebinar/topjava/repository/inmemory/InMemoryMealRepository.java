package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> this.save(1, meal));
    }

    @Override
    public Meal save(int userId, Meal meal) {
        log.info("user id: {}, save {}", userId, meal);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            Map<Integer, Meal> meals = repository.computeIfAbsent(userId, HashMap::new);
            meals.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Map<Integer, Meal> meals = repository.computeIfPresent(userId, (id, oldMap) -> {
            oldMap.computeIfPresent(meal.getId(), (key, oldMeal) -> meal);
            return oldMap;
        });
        return meals.containsKey(meal.getId()) ? meal : null;
    }

    @Override
    public boolean delete(int userId, int id) {
        log.info("user id: {}, delete by id {}", userId, id);
        Map<Integer, Meal> meals = repository.get(userId);

        if (Objects.nonNull(meals)) {
            Meal meal = meals.remove(id);
            if (meals.values().size()==0)
                repository.remove(userId);
            return Objects.nonNull(meal);
        }
        return false;
    }

    @Override
    public Meal get(int userId, int id) {
        log.info("user id: {}, get by id {}", userId, id);
        Map<Integer, Meal> meals = repository.get(userId);
        return Objects.nonNull(meals) ? meals.get(id) : null;
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        log.info("get all by user id: {}", userId);
        Map<Integer, Meal> meals = repository.computeIfAbsent(userId, HashMap::new);
        return meals.values()
                .stream()
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

