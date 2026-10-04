package com.c6server.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        var engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");

        var app = JakartaServletWebApplication.buildApplication(getServletContext());
        var ctx = new WebContext(app.buildExchange(req, resp));

        resp.setContentType("text/html;charset=UTF-8");
        engine.process("register", ctx, resp.getWriter());
    }
}
