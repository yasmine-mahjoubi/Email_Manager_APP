package com.fst.emailmanagerapp;

import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(EmailServlet.class.getName());
    private final List<String> emailList = new ArrayList<>();
    private String filePath;
    private String subscribe;
    private String unsubscribe;

    @Override
    public void init() throws ServletException {
        filePath = getServletContext().getInitParameter("emailFilePath");
        if (filePath == null || filePath.isEmpty()) {
            throw new ServletException("Le chemin du fichier d'adresses email est manquant.");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(getServletContext().getRealPath(filePath)))) {
            String line;
            while ((line = br.readLine()) != null) {
                emailList.add(line.trim());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la lecture du fichier d'adresses email : {0}", e.getMessage());
            throw new ServletException("Erreur lors de la lecture du fichier d'adresses email.", e);
        }
    }

    private void saveEmailsToFile() {
        try (BufferedWriter writer  = new BufferedWriter(new FileWriter(getServletContext().getRealPath(filePath), false))) {
            for (String email : emailList) {
                writer.write(email);
                writer.newLine();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'enregistrement des adresses email dans le fichier : {0}", e.getMessage());
        }
    }


    private void subscribe(String email) {
        if (!emailList.contains(email)) {
            emailList.add(email);
            saveEmailsToFile();
            subscribe = email;
        }
    }

    private void unsubscribe(String email) {
        if (emailList.contains(email)) {
            emailList.remove(email);
            saveEmailsToFile();
            unsubscribe = email;
        }
    }

    /*@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Membres:</h2>");
        out.println("<ul>");
        for (String email : emailList) {
            out.println("<li>" + email + "</li>");
        }
        out.println("</ul><hr>");

        out.println("<form method='post'>");
        out.println("  <label>Entrez votre adresse email:</label>");
        out.println("  <input type='email' name='email' required><br>");
        out.println("  <button type='submit' name='action' value='subscribe'>subscribe</button>");
        out.println("  <button type='submit' name='action' value='unsubscribe'>unsubscribe</button>");
        out.println("</form>");

        out.println("</body></html>");
    }*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("emailList", emailList);
        req.getRequestDispatcher("/ListEmail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String action = request.getParameter("action");

        if (email == null || email.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Erreur : L'adresse email ne peut pas être vide.");
            return;
        }
        if (action.equals("Unsubscribe") && !emailList.contains(email)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Adresse " + email + " non inscrite.");
            return;
        }
        //response.setContentType("text/html");
        //PrintWriter out = response.getWriter();

        if ("Subscribe".equals(action)) {
            if (!emailList.contains(email)) {
                subscribe(email);
                request.setAttribute("subscribe", email);
                //out.println("<p>Adresse " + email + " inscrite.</p>");
            }
        }else if ("Unsubscribe".equals(action)) {
            if (emailList.contains(email)) {
                unsubscribe(email);
                request.setAttribute("unsubscribe", email);
                //out.println("<p>Adresse " + email + " supprimée.</p>");
            }
        }else {
                throw new ServletException("Action non reconnue.");

        }
        doGet(request, response);
       // out.println("<a href=\"EmailServlet\">Afficher la liste</a>");
    }
}
