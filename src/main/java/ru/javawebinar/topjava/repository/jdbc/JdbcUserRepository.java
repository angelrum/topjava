package ru.javawebinar.topjava.repository.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcUserRepository.class);
    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final SimpleJdbcInsert insertUser;
    private final UserExtractor userExtractor;
    private final ValidatorFactory vf;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userExtractor = new UserExtractor();
        this.vf = Validation.buildDefaultValidatorFactory();
    }

    @Transactional
    @Override
    public User save(User user) {
        if (checkObject(user))
            return null;
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        return user;
    }

    @Transactional
    @Override
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_role ur on users.id = ur.user_id WHERE id=?", userExtractor, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_role ur on users.id = ur.user_id WHERE email=?", userExtractor, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_role ur on users.id = ur.user_id ORDER BY name, email", userExtractor);
    }

    class UserExtractor implements ResultSetExtractor<List<User>> {
        @Override
        public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Integer, User> userMap = new HashMap<>();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                Role role = hasText(rs.getString("role"))
                        ? Role.valueOf(rs.getString("role"))
                        : null;
                if (userMap.containsKey(id)) {
                    Set<Role> roles = userMap.get(id).getRoles();
                    roles.add(role);
                } else {
                    Set<Role> roles = new HashSet<>();
                    if (nonNull(role)) roles.add(role);
                    User user = new User(id,
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getInt("calories_per_day"),
                            rs.getBoolean("enabled"),
                            rs.getDate("registered"),
                            roles);
                    userMap.put(id, user);
                }
            }
            return userMap.values()
                    .stream()
                    .sorted(Comparator.comparing(User::getName).thenComparing(User::getEmail))
                    .toList();
        }
    }

    private boolean checkObject(User user) {
        Set<ConstraintViolation<User>> validate = vf.getValidator().validate(user);
        if (!validate.isEmpty()) {
            for (ConstraintViolation<User> cv : validate) {
                log.error("Св-во: {}. Значение: {}. Сообщение: {}",
                        cv.getPropertyPath(), cv.getInvalidValue(), cv.getMessage());
            }
        }
        return !validate.isEmpty();
    }
}
