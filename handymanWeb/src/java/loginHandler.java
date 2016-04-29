/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.*;
import java.math.*;
import javax.servlet.http.HttpSession;

/**
 *
 * @author alvin
 */
@WebServlet(urlPatterns = {"/loginHandler"})
public class loginHandler extends HttpServlet {

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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet loginHandler</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet loginHandler at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        processRequest(request, response);
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
        
        try {
            String username = request.getParameter("username");
            if(username.equals("admin@gmail.com")){
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/handyman", "root", "");
                Statement statement = connection.createStatement();
                String pass = request.getParameter("password");
                
                MessageDigest crypt = MessageDigest.getInstance("SHA-1");
                crypt.reset();
                crypt.update(pass.getBytes("UTF-8"));

                pass = new BigInteger(1, crypt.digest()).toString(16);
                ResultSet resultset = statement.executeQuery("select * from user where username = 'admin@gmail.com'");
                resultset.next();
                if(pass.equals(resultset.getObject(2))){
                    HttpSession session = request.getSession(true);
                    session.setAttribute("logged", "true");
                    response.sendRedirect("register");
                }else{
                    response.sendRedirect("index.html");
                }
                
            }else{
                response.sendRedirect("index.html");
            }
            
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(loginHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(loginHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(loginHandler.class.getName()).log(Level.SEVERE, null, ex);
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
