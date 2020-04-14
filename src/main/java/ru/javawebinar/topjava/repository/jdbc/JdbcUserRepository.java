package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private PlatformTransactionManager transactionManager;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              PlatformTransactionManager transactionManager) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.transactionManager = transactionManager;
    }

    @Override
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            if (user.isNew()) {
                Number newKey = insertUser.executeAndReturnKey(parameterSource);
                user.setId(newKey.intValue());
                batchInsert(user.getRoles(), user.getId());
            } else {

                int count = namedParameterJdbcTemplate.update(
                        "UPDATE users SET name=:name, email=:email, password=:password, " +
                                "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id",
                        parameterSource);
                int[] rolesCount = batchUpdate(user.getRoles(), user.getId());
                for (int i : rolesCount) count+=i;
                user = count == 0 ? null : user;
            }
            transactionManager.commit(status);
            return user;

        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private int[] batchInsert(Set<Role> roles, int userId) {
        if (roles.isEmpty())
            return new int[]{0};
        List<Map<String, Object>> listRoles = new ArrayList<>();
        roles.forEach(role -> {
            listRoles.add(Map.of("rl", role.toString(), "id", userId));
        });
        return batchInsert(listRoles.toArray(new Map[listRoles.size()]));
    }

    private int[] batchUpdate(Set<Role> roles, int userId) {
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", userId);
        return batchInsert(roles, userId);
    }

    private int[] batchInsert(Map<String, Object>[] maps) {
        return namedParameterJdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES (:id, :rl)",
                maps);
    }

    @Override
    public boolean delete(int id) {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        boolean update = false;
        try {
            update = jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
        } catch (Exception e) {
            transactionManager.rollback(status);
        }
        transactionManager.commit(status);
        return update;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE id=?",
                ROW_MAPPER,
                id);
        return mappedRole(DataAccessUtils.singleResult(users));
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query(
                "SELECT * FROM users WHERE email=?",
                ROW_MAPPER,
                email);
        return mappedRole(DataAccessUtils.singleResult(users));
    }

    @Override
    public List<User> getAll() {
        List<User> userList = jdbcTemplate.query(
                "SELECT * FROM users ORDER BY name, email",
                ROW_MAPPER);
        mappedRoleForAllUser(userList);
        return userList;
    }

    private User mappedRole(User user) {
        if (Objects.nonNull(user)) {
            List<Role> roles = jdbcTemplate.query("SELECT role FROM user_roles WHERE user_id=?",
                    ((resultSet, i) -> Role.valueOf(resultSet.getString("role"))),
                    user.getId());
            user.setRoles(roles);
        }
        return user;
    }

    private void mappedRoleForAllUser(List<User> userList) {
        if (!userList.isEmpty()) {
            Map<Integer, List<Role>> mapRole = new HashMap<>();
            jdbcTemplate.query("SELECT * FROM user_roles", resultSet -> {
                int id = resultSet.getInt("user_id");
                Role role = Role.valueOf(resultSet.getString("role"));
                mapRole.computeIfAbsent(id, ArrayList::new).add(role);
            });

            userList.forEach(user -> user.setRoles(mapRole.get(user.getId())));
        }
    }
}
