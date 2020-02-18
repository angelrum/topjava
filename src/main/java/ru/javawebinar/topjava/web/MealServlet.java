package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action != null && action.equals("delete"))
            remove(req);
        else if (action != null && action.equals("update")) {
            int id = Integer.parseInt(req.getParameter("id"));
            MealTo mealUp = MealsUtil.getMealToList().stream().filter(m->m.getId()==id).findFirst().get();
            if (mealUp != null)
                req.setAttribute("mealUp", mealUp);
        }
        req.setAttribute("meals", MealsUtil.getMealToList());
        req.getRequestDispatcher("/meals.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        if (action!=null && ( action.equals("update") || action.equals("save"))) {
            LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("datetime"));
            String description = req.getParameter("description");
            int calories = Integer.parseInt(req.getParameter("calories"));
            if (req.getParameter("id")!= null)
                MealsUtil.updateMeals(new Meal(dateTime, description, calories), Integer.parseInt(req.getParameter("id")));
            else
                MealsUtil.insertToMeals(new Meal(dateTime, description, calories));
        }

        resp.sendRedirect("meals");
    }

    private void remove(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        log.debug("Need remove " + id);
        MealsUtil.removeFromMeals(id);
    }
}
