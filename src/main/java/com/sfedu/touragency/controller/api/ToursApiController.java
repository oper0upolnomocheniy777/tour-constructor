package com.sfedu.touragency.controller.api;

import com.google.gson.Gson;
import com.sfedu.touragency.domain.Tour;
import com.sfedu.touragency.service.TourService;
import com.sfedu.touragency.util.ServiceLocator;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;

@WebServlet("/api/tours/*")
public class ToursApiController extends HttpServlet {
    private TourService tourService = ServiceLocator.INSTANCE.get(TourService.class);
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String pathInfo = req.getPathInfo();
        
        // Если запрос /api/tours - возвращаем все туры
        if (pathInfo == null || pathInfo.equals("/")) {
            List<Tour> tours = tourService.findAll();
            // Убираем лишние данные из ответа
            tours.forEach(tour -> {
                // Можно добавить логику для сокращения описания
            });
            resp.getWriter().write(gson.toJson(tours));
        } 
        // Если запрос /api/tours/{id} - возвращаем конкретный тур
        else {
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                Tour tour = tourService.read(id);
                if (tour != null) {
                    resp.getWriter().write(gson.toJson(tour));
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Проверка авторизации
        if (req.getSession().getAttribute("user") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        BufferedReader reader = req.getReader();
        Tour tour = gson.fromJson(reader, Tour.class);
        
        tourService.create(tour);
        
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(gson.toJson(tour));
    }
}