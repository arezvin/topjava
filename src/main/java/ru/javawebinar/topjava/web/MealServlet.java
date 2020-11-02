package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.repository.InMemoryMealRepositoryImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final Logger Log = getLogger(MealServlet.class);
    private MealRepository repository;

    @Override
    public void init() throws ServletException {
        super.init();
        repository = new InMemoryMealRepositoryImpl();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action == null ? "" : action) {
            case "delete":
                Log.debug("meals delete");
                repository.delete(getId(request));
                response.sendRedirect("meals");
                break;
            case "edit":
                Log.debug("meals edit");
                Meal meal = repository.getById(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                break;
            case "insert":
                Log.debug("meals insert");
                request.getRequestDispatcher("/editMeal.jsp").forward(request, response);
                break;
            default:
                Log.debug("forward to meals");
                request.setAttribute("meals", filteredByStreams(repository.getAll(), LocalTime.MIN, LocalTime.MAX, 2000));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"), Integer.parseInt(request.getParameter("calories")));

        int id = getId(request);
        if(id == 0)
            repository.save(meal);
        else {
            meal.setId(id);
            repository.update(meal);
        }

        response.sendRedirect("meals");
    }

    private int getId(HttpServletRequest request) {
        String id = request.getParameter("mealId");
        if(id == null || id.isEmpty())
            return 0;
        else
            return Integer.parseInt(id);
    }
}
