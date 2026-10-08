package com.c6server.servlet;

import com.c6server.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("nickname");

        if (nickname == null) {
            showForm(req, resp, null, null, Set.of());
            return;
        }

        String password = (String) session.getAttribute("password");
        String email    = (String) session.getAttribute("email");
        try {
            showForm(req, resp, nickname, email, userService.validate(nickname, password, email));
        } catch (SQLException e) {
            throw new ServletException("Errore durante la registrazione", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");

        String nickname = req.getParameter("nickname");
        String password = req.getParameter("password");
        String email    = req.getParameter("email");

        Set<UserService.RegisterError> errors;
        try {
            errors = userService.validate(nickname, password, email);
        } catch (SQLException e) {
            throw new ServletException("Errore durante la registrazione", e);
        }

        if (!errors.isEmpty()) {
            showForm(req, resp, nickname, email, errors);
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("nickname", nickname);
        session.setAttribute("password", password);
        session.setAttribute("email", email);
        resp.sendRedirect(req.getContextPath() + "/preferences");
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp,
                          String nickname, String email,
                          Set<UserService.RegisterError> errors) throws IOException {
        WebContext ctx = newContext(req, resp);
        ctx.setVariable("nickname", nickname);
        ctx.setVariable("email", email);

        for (UserService.RegisterError error : errors) {
            switch (error) {
                case NICK_EMPTY       -> ctx.setVariable("nickError", "Inserisci un nickname.");
                case NICK_TOO_LONG    -> ctx.setVariable("nickError", "Il nickname non può superare i 10 caratteri.");
                case NICK_TAKEN       -> ctx.setVariable("nickError", "Questo nickname è già registrato: scegline un altro.");
                case PASSWORD_INVALID -> ctx.setVariable("passwordError", "La password può contenere solo lettere minuscole e numeri.");
                case EMAIL_EMPTY      -> ctx.setVariable("emailError", "Inserisci un indirizzo e-mail.");
                case EMAIL_TAKEN      -> ctx.setVariable("emailError", "Questa e-mail è già registrata.");
            }
        }

        render(req, resp, ctx);
    }

    private WebContext newContext(HttpServletRequest req, HttpServletResponse resp) {
        var app = JakartaServletWebApplication.buildApplication(getServletContext());
        return new WebContext(app.buildExchange(req, resp));
    }

    private void render(HttpServletRequest req, HttpServletResponse resp, WebContext ctx) throws IOException {
        var engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        resp.setContentType("text/html;charset=UTF-8");
        engine.process("register", ctx, resp.getWriter());
    }
}