/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
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
@WebServlet(urlPatterns = {"/listWorker"})
public class listWorker extends HttpServlet {

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
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                /* TODO output your page here. You may use following sample code. */
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>List Worker</title>");            
                out.println("</head>");
                out.println("<body>");
                
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/handyman", "root", "");
                Statement statement = connection.createStatement();
                
                ResultSet resultset = statement.executeQuery("select username from worker");
                ResultSetMetaData metaData = resultset.getMetaData();
                
                int numCol = metaData.getColumnCount();
                out.println("<table>");
                out.println("<tr>");
                for(int i = 1; i <= numCol; i++){
                    out.println("<td>");
                    out.println(metaData.getColumnName(i));
                    out.println("</td>");
                }
                out.println("<td>");
                out.println("edit");
                out.println("</td>");
                out.println("<td>");
                out.println("delete");
                out.println("</td>");
                out.println("</tr>");
                while(resultset.next()){
                    out.println("<tr>");
                    for(int i = 1; i <= numCol; i++){
                        out.println("<td>");
                        out.println(resultset.getObject(i));
                        out.println("</td>");
                        out.println("<td>");
                        out.println("<form action=\"edit\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"username\" value=\"" + resultset.getObject(i) + "\"/><br>");
                        out.println("<input type=\"submit\" value=\"Edit\"/><br>");
                        out.println("</form>");
                        out.println("</td>");
                        out.println("<td>");
                        out.println("<form action=\"deleteHandler\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"username\" value=\"" + resultset.getObject(i) + "\"/><br>");
                        out.println("<input type=\"submit\" value=\"Delete\"/><br>");
                        out.println("</form>");
                        out.println("</td>");
                    }
                    out.println("</tr>");
                }
                out.println("</table>");
                out.println("<form action=\"logoutHandler\" method=\"post\">");
                out.println("<input type=\"submit\" value=\"Logout\"/><br>");
                out.println("</form><br><br>");
                out.println("</body>");
                out.println("</html>");
            }
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
            Logger.getLogger(listWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(listWorker.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(listWorker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(listWorker.class.getName()).log(Level.SEVERE, null, ex);
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
