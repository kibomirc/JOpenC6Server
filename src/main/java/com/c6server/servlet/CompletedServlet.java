package com.c6server.servlet;

import com.c6server.model.ProfileOptionsEntity;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

public class CompletedServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);

        var app = JakartaServletWebApplication.buildApplication(getServletContext());
        var ctx = new WebContext(app.buildExchange(req, resp));


        var engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        resp.setContentType("text/html;charset=UTF-8");
        engine.process("completed", ctx, resp.getWriter());
    }
}
