package ru.javawebinar.topjava.repository.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.web.JspMealController;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public class JdbcMealRepository implements MealRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcMealRepository.class);
    private static final RowMapper<Meal> ROW_MAPPER = BeanPropertyRowMapper.newInstance(Meal.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertMeal;
    private final ValidatorFactory vf;

    public JdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertMeal = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("meal")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.vf = Validation.buildDefaultValidatorFactory();
    }
    @Transactional
    @Override
    public Meal save(Meal meal, int userId) {
        if (checkObject(meal))
            return null;
        MapSqlParameterSource map = new MapSqlParameterSource()
                .addValue("id", meal.getId())
                .addValue("description", meal.getDescription())
                .addValue("calories", meal.getCalories())
                .addValue("date_time", meal.getDateTime())
                .addValue("user_id", userId);

        if (meal.isNew()) {
            Number newId = insertMeal.executeAndReturnKey(map);
            meal.setId(newId.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("" +
                    "UPDATE meal " +
                    "   SET description=:description, calories=:calories, date_time=:date_time " +
                    " WHERE id=:id AND user_id=:user_id", map) == 0) {
                return null;
            }
        }
        return meal;
    }

    @Transactional
    @Override
    public boolean delete(int id, int userId) {
        return jdbcTemplate.update("DELETE FROM meal WHERE id=? AND user_id=?", id, userId) != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        List<Meal> meals = jdbcTemplate.query(
                "SELECT * FROM meal WHERE id = ? AND user_id = ?", ROW_MAPPER, id, userId);
        return DataAccessUtils.singleResult(meals);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meal WHERE user_id=? ORDER BY date_time DESC", ROW_MAPPER, userId);
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM meal WHERE user_id=?  AND date_time >=  ? AND date_time < ? ORDER BY date_time DESC",
                ROW_MAPPER, userId, startDateTime, endDateTime);
    }

    private boolean checkObject(Meal meal) {
        Set<ConstraintViolation<Meal>> validate = vf.getValidator().validate(meal);
        if (!validate.isEmpty()) {
            for (ConstraintViolation<Meal> cv : validate) {
                log.error("Св-во: {}. Значение: {}. Сообщение: {}",
                        cv.getPropertyPath(), cv.getInvalidValue(), cv.getMessage());
            }
        }
        return !validate.isEmpty();
    }
}
