package com.c6server.config;

import com.c6server.servlet.RegisterServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class WebServer {

    private static final Logger logger = LogManager.getLogger(WebServer.class);
    private static final int PORT = 8080;

    public static void start() throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        ctx.addApplicationListener(ThymeleafConfig.class.getName());

        Tomcat.addServlet(ctx, "register", new RegisterServlet());
        ctx.addServletMappingDecoded("/register", "register");

        tomcat.start();
        logger.info("Tomcat in ascolto sulla porta " + PORT);
    }
}
