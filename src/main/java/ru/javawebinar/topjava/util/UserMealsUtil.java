package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println();
        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> calSumByDate = new HashMap<>();
        for(UserMeal meal : meals) {
            calSumByDate.put(meal.getDate(), meal.getCalories() + calSumByDate.getOrDefault(meal.getDate(), 0));
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for(UserMeal meal : meals) {
            if(TimeUtil.isBetweenHalfOpen(meal.getTime(), startTime, endTime)) {
                result.add(createTo(meal.getDateTime(), meal.getDescription(), meal.getCalories(),
                        calSumByDate.get(meal.getDate()) > caloriesPerDay));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> calSumByDate = meals
                .stream()
                .collect(Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories)));
        return meals
                .stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getTime(), startTime, endTime))
                .map(m -> createTo(m.getDateTime(), m.getDescription(), m.getCalories(), calSumByDate.get(m.getDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    private static UserMealWithExcess createTo(LocalDateTime dateTime, String description, int calories, boolean excess) {
        return new UserMealWithExcess(dateTime, description, calories , excess);
    }
}
