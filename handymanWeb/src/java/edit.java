/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author alvin
 */
@WebServlet(urlPatterns = {"/edit"})
public class edit extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException, SQLException {
        HttpSession session = request.getSession(false);
        
        if(session != null && session.getAttribute("logged").toString().equals("true")){
            response.sendRedirect("listWorker");
        }else{
            response.sendRedirect("index.html");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(edit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(edit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if(session != null && session.getAttribute("logged").toString().equals("true")){
            try {
            
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/handyman", "root", "");
                Statement statement = connection.createStatement();

                String username = request.getParameter("username");

                ResultSet resultset = statement.executeQuery("select * from worker where username = '" + username + "'");

                if(resultset.next()){
                    response.setContentType("text/html;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println("<!DOCTYPE html>");
                    out.println("<html>");
                    out.println("<head>");
                    out.println("<title>Edit Worker</title>");            
                    out.println("</head>");
                    out.println("<body>");
                    out.println("Edit Worker<br>");
                    out.println("<form action=\"editHandler\" method=\"post\">");
                    out.println("Username : <input type=\"text\" name=\"username\" value=\"" + resultset.getObject(1) + "\" readonly/>");
                    out.println("<br>");
                    out.println("Password : <input type=\"password\" name=\"password\" required/><br>");
                    out.println("Name : <input type=\"text\" name=\"name\" value=\"" + resultset.getObject(3) + "\" required/><br>");
                    out.println("Address : <input type=\"text\" name=\"address\" value=\"" + resultset.getObject(5) + "\" required/><br>");
                    out.println("Tag : <input type=\"text\" name=\"tag\" value=\"" + resultset.getObject(8) + "\" required/><br>");
                    out.println("Photo link : <input type=\"text\" name=\"photo\" value=\"" + resultset.getObject(4) + "\" /><br>");
                    out.println("Latitude : <input type=\"text\" name=\"latitude\" value=\"" + resultset.getObject(6) + "\" /><br>");
                    out.println("Longitude : <input type=\"text\" name=\"longitude\" value=\"" + resultset.getObject(7) + "\" /><br>");
                    out.println("<input type=\"submit\"/><br>");
                    out.println("</form><br><br>");
                    out.println("</body>");
                    out.println("</html>");
                }

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(loginHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(registerHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            response.sendRedirect("listWorker");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
