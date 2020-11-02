package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealRepositoryImpl implements MealRepository {
    private AtomicInteger counter = new AtomicInteger(0);
    private ConcurrentHashMap<Integer, Meal> storage = new ConcurrentHashMap<>();

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal getById(int id) {
        return storage.get(id);
    }

    @Override
    public void save(Meal meal) {
        meal.setId(counter.incrementAndGet());
        storage.put(meal.getId(), meal);
    }

    @Override
    public void update(Meal meal) {
        storage.put(meal.getId(), new Meal(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories()));
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }
}
