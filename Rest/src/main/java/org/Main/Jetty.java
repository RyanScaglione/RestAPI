package org.Main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Jetty {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);

        // Add CustomerProfileServlet
        handler.addServlet(new ServletHolder(new CustomerProfileServlet()), "/customer-profile");

        server.start();
        server.join();
    }
}