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
import javax.servlet.http.HttpSession;

/**
 *
 * @author alvin
 */
@WebServlet(urlPatterns = {"/register"})
public class register extends HttpServlet {

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
        HttpSession session = request.getSession(false);
        
        if(session != null && session.getAttribute("logged").toString().equals("true")){
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Register</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("Register Worker<br>");
            out.println("<form action=\"registerHandler\" method=\"post\">");
            out.println("Username : <input type=\"text\" name=\"username\" required/><br>");
            out.println("Password : <input type=\"password\" name=\"password\" required/><br>");
            out.println("Name : <input type=\"text\" name=\"name\" required/><br>");
            out.println("Address : <input type=\"text\" name=\"address\" required/><br>");
            out.println("Tag : <input type=\"text\" name=\"tag\" required/><br>");
            out.println("<input type=\"submit\"/><br>");
            out.println("</form><br><br>");
            out.println("<form action=\"logoutHandler\" method=\"post\">");
            out.println("<input type=\"submit\" value=\"Logout\"/><br>");
            out.println("</form><br>");
            out.println("</body>");
            out.println("</html>");
            out.close();
            
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
        
        HttpSession session = request.getSession(false);
        
        if(session != null && session.getAttribute("logged").toString().equals("true")){
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Register</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<form action=\"registerHandler\" method=\"post\">");
            out.println("Username : <input type=\"text\" name=\"username\" required/><br>");
            out.println("Password : <input type=\"password\" name=\"password\" required/><br>");
            out.println("Name : <input type=\"text\" name=\"name\" required/><br>");
            out.println("Address : <input type=\"text\" name=\"address\" required/><br>");
            out.println("Tag : <input type=\"text\" name=\"tag\" required/><br>");
            out.println("<input type=\"submit\"/>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            out.close();
            
        }else{
            response.sendRedirect("index.html");
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
