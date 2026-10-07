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

public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("nickname");
        if (nickname == null) {                      // nessuna registrazione in corso
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        var app = JakartaServletWebApplication.buildApplication(getServletContext());
        var ctx = new WebContext(app.buildExchange(req, resp));
        ctx.setVariable("nickname", nickname);
        ctx.setVariable("etaList", ProfileOptionsEntity.ETA);
        ctx.setVariable("genereList", ProfileOptionsEntity.GENERE);
        ctx.setVariable("orientamentoList", ProfileOptionsEntity.ORIENTAMENTO);
        ctx.setVariable("occupazioneList", ProfileOptionsEntity.OCCUPAZIONE);
        ctx.setVariable("areaList", ProfileOptionsEntity.AREA_GEOGRAFICA);
        ctx.setVariable("regioneList", ProfileOptionsEntity.REGIONE_PROVINCIA);
        ctx.setVariable("hobbyList", ProfileOptionsEntity.HOBBY);
        ctx.setVariable("sportList", ProfileOptionsEntity.SPORT);
        ctx.setVariable("musicaList", ProfileOptionsEntity.GENERE_MUSICALE);
        ctx.setVariable("cinemaList", ProfileOptionsEntity.GENERE_CINEMA);
        ctx.setVariable("comunitaList", ProfileOptionsEntity.COMUNITA_VIRTUALE);
        ctx.setVariable("odiList", ProfileOptionsEntity.ODI_CORDIALI);

        var engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        resp.setContentType("text/html;charset=UTF-8");
        engine.process("preferences", ctx, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO: salvare i dati del profilo e andare alla pagina successiva
        doGet(req, resp);
    }
}
