package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final Map<Integer, User> usersMap = new HashMap<>();

    private final RowMapper<User> USER_WITH_ROLES_ROW_MAPPER = (rs, rowNum) -> {
        Integer id = rs.getInt("id");
        if (!usersMap.containsKey(id)) {
            usersMap.put(id, new User(id,
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getInt("calories_per_day"),
                    rs.getBoolean("enabled"),
                    rs.getDate("registered"),
                    Collections.emptySet()));
        }
        String role = rs.getString("role");
        if (role != null) { usersMap.get(id).getRoles().add(Role.valueOf(role)); }
        return usersMap.get(id);
    };

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update(
                "UPDATE users SET name=:name, email=:email, password=:password, " +
                        "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
            return null;
        } else {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
        }
        batchInsert(new ArrayList<>(user.getRoles()), user);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        usersMap.clear();
        jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON " +
                "users.id = user_roles.user_id WHERE id=?", USER_WITH_ROLES_ROW_MAPPER, id);
        return usersMap.get(id);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        usersMap.clear();
        jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON " +
                "users.id = user_roles.user_id WHERE email=?", USER_WITH_ROLES_ROW_MAPPER, email);
        return usersMap.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> getAll() {
        usersMap.clear();
        jdbcTemplate.query("SELECT * FROM users LEFT JOIN user_roles ON users.id = user_roles.user_id " +
                "ORDER BY name, email", USER_WITH_ROLES_ROW_MAPPER);
        return List.copyOf(usersMap.values());
    }

    private void batchInsert(List<Role> roles, User user) {
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (role, user_id) VALUES(?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, roles.get(i).name());
                        ps.setInt(2, user.getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return roles.size();
                    }
                }
        );
    }
}
