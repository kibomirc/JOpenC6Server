package com.c6server.servlet;

import com.c6server.c6enum.C6EnumUserProfilePreferences;
import com.c6server.model.ProfileOptionsEntity;
import com.c6server.model.UserProfileEntity;
import com.c6server.service.UserPreferencesService;
import com.c6server.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileServlet extends HttpServlet {

    /** nome del campo del form -> indice di categoria dell'enum */
    private static final Map<String, Integer> PREF_PARAMS = buildPrefParams();

    private final UserService userService = new UserService();
    private final UserPreferencesService preferencesService = new UserPreferencesService();

    private static Map<String, Integer> buildPrefParams() {
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("eta", 0x01);
        m.put("genere", 0x02);
        m.put("orientamento", 0x03);
        m.put("occupazione", 0x04);
        m.put("areaGeografica", 0x05);
        m.put("regione", 0x06);
        for (int i = 1; i <= 3; i++) {
            m.put("hobby" + i, 0x07);
            m.put("sport" + i, 0x08);
            m.put("musica" + i, 0x09);
            m.put("cinema" + i, 0x0A);
            m.put("odio" + i, 0x0C);
        }
        m.put("comunita", 0x0B);
        return m;
    }

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        String nickname = session == null ? null : (String) session.getAttribute("nickname");
        if (nickname == null) {                      // nessuna registrazione in corso
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }
        String password = (String) session.getAttribute("password");
        String email    = (String) session.getAttribute("email");

        // voci scelte nei menu (categoria + posizione) -> costanti dell'enum -> profilo
        List<C6EnumUserProfilePreferences> scelte = new ArrayList<>();
        for (Map.Entry<String, Integer> campo : PREF_PARAMS.entrySet()) {
            scelte.add(preferencesService.toPreference(campo.getValue(), req.getParameter(campo.getKey())));
        }
        UserProfileEntity profile = preferencesService.buildProfile(scelte);

        Set<UserService.RegisterError> errors;
        try {
            errors = userService.register(nickname, password, email, profile);   // utente + preferenze
        } catch (SQLException e) {
            throw new ServletException("Errore durante la registrazione", e);
        }

        if (!errors.isEmpty()) {
            // nel frattempo qualcun altro ha preso nick o e-mail: si torna al primo passo,
            // dove RegisterServlet mostra il messaggio
            resp.sendRedirect(req.getContextPath() + "/register");
            return;
        }

        session.setAttribute("nickname", nickname);
        resp.sendRedirect(req.getContextPath() + "/completed");
    }
}