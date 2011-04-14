package fr.loria.score.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Checks if query string contains a parameter named "esid" (editing session id).
 * If true, it echoes back it's value, else it creates a new "editing session id", stores it and makes it available to interested servlets
 * User: Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
public class EditingSessionIdCheckerServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(EditingSessionIdCheckerServlet.class.getName());

    //the editing session id generator
    private final AtomicInteger esId = new AtomicInteger();

    //the mapping between the editing session id and the list of ids of the clients that share the same editing session
    private Map<Integer, List<Integer>> locks = new HashMap<Integer, List<Integer>>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        config.getServletContext().setAttribute("locks", locks);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.finest("Checking the editing session id...");

        String esid = req.getParameter("esid");
        if (esid != null && !esid.trim().equals("")) {
            log.finest("Existing editing session id found: " + esid);
            //sanity check
            if (locks.containsKey(Integer.valueOf(esid))) {
                resp.getWriter().write(esid);
            } else {
                throw new ServletException("Inexistent editing session id: " + esid);
            }
        } else {
            log.finest("Generating a new editing session id...");
            Integer gesid = esId.getAndIncrement();
            locks.put(gesid, new ArrayList<Integer>());
            resp.getWriter().write(String.valueOf(gesid));
        }
    }
}
