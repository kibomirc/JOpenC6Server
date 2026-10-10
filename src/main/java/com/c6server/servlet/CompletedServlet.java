package com.c6server.servlet;

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
        String nickname = (session == null) ? null : (String) session.getAttribute("nickname");

        if (nickname == null) {
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        var app = JakartaServletWebApplication.buildApplication(getServletContext());
        var ctx = new WebContext(app.buildExchange(req, resp));
        ctx.setVariable("nickname", nickname);

        var engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        if (engine == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "templateEngine non trovato nel ServletContext");
            return;
        }

        resp.setContentType("text/html;charset=UTF-8");
        engine.process("completed", ctx, resp.getWriter());
    }
}