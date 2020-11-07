package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static org.junit.Assert.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDb.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Autowired
    private MealRepository repository;

    @Test
    public void get() {
        Meal meal = service.get(MEAL1_ID, USER_ID);
        assertMatch(meal, MEAL1);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL1_ID, USER_ID);
        assertNull(repository.get(MEAL1_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(ADMIN_MEAL1_ID, USER_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> meals = service.getBetweenInclusive(null, LocalDate.of(2020, Month.JANUARY, 30), USER_ID);
        assertMatch(meals, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void getAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, MEAL7, MEAL6, MEAL5, MEAL4, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL1_ID, USER_ID), updated);
    }

    @Test
    public void updateNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), ADMIN_ID));
    }

    @Test
    public void create() {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Повтор даты", 500), USER_ID));
    }
}
