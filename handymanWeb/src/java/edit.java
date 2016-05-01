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
                        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"/>");
                        out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/materialize.min.css\"  media=\"screen,projection\"/>");
                        out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/materialize.min.css\"  media=\"screen,projection\"/>");
                        out.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"css/style.css\">");
                        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("<script type=\"text/javascript\" src=\"https://code.jquery.com/jquery-2.1.1.min.js\"></script>");
                        out.println("<script type=\"text/javascript\" src=\"js/materialize.min.js\"></script>");
                        out.println("<script type=\"text/javascript\"></script>");
                        out.println("<div class=\"intro blue z-depth-2\">");
                        out.println("<h2 class=\"white-text text-darken-1\">Handyman Hand</h2>");
                        out.println("</div>");
                        out.println("<div class = \"container white\">");
                        out.println("<ul class=\"collapsible white\" data-collapsible=\"accordion\">");
                        out.println("<li><div class=\"collapsible-header\">");
                        out.println("<h3 class=\"blue-text text-lighten-1 center\">Edit Tukang</h3>");
                        out.println("</div>");
                        out.println("<div class=\"collapsible-body\">");
                        out.println("<div class=\"container\">");
                        out.println("<div class=\"row\">");
                        out.println("<form action=\"editHandler\" method=\"post\">");
                        out.println("<div class=\"input-field col s6\">");
                        out.println("<input type=\"text\" name=\"username\" value=\"" + resultset.getObject(1) + "\" readonly/>");
                        out.println("<label for=\"username\">Username</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s6\">");
                        out.println("<input type=\"password\" name=\"password\" required/>");
                        out.println("<label for=\"password\">Password</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s12\">");
                        out.println("<input type=\"text\" name=\"name\" value=\"" + resultset.getObject(3) + "\" required/>");
                        out.println("<label for=\"name\">Name</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s12\">");
                        out.println("<input type=\"text\" name=\"address\" value=\"" + resultset.getObject(5) + "\" required/>");
                        out.println("<label for=\"address\">Address</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s12\">");
                        out.println("<input type=\"text\" name=\"tag\" value=\"" + resultset.getObject(8) + "\" required/>");
                        out.println("<label for=\"tag\">Tag</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s12\">");
                        out.println("<input type=\"text\" name=\"photo\" value=\"" + resultset.getObject(4) + "\" />");
                        out.println("<label for=\"photo\">Photo Link</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s6\">");
                        out.println("<input type=\"number\" step=\"any\" name=\"latitude\" value=\"" + resultset.getObject(6) + "\" />");
                        out.println("<label for=\"latitude\">Latitude</label>");
                        out.println("</div>");
                        out.println("<div class=\"input-field col s6\">");
                        out.println("<input type=\"number\" step=\"any\" name=\"longitude\" value=\"" + resultset.getObject(7) + "\" />");
                        out.println("<label for=\"longitude\">Longitude</label>");
                        out.println("</div>");
                        out.println("<button class=\"btn waves-effect waves-light\" type=\"submit\" name=\"action\">Submit</button>");
                        out.println("</form></div></div></div></li>");
                        out.println("<li><div class=\"collapsible-header\">");
                        out.println("<h3 class=\"blue-text text-lighten-1 center\">Other</h3>");
                        out.println("</div>");
                        out.println("<div class=\"collapsible-body\">");
                        out.println("<div class=\"container\">");
                        out.println("<div class=\"row\"><br>");
                        out.println("<form action=\"register\" method=\"post\">");
                        out.println("<button class=\"btn waves-effect waves-light\" type=\"submit\" name=\"action\">Register Tukang</button>");
                        out.println("</form><br>");
                        out.println("<form action=\"listWorker\" method=\"post\">");
                        out.println("<button class=\"btn waves-effect waves-light\" type=\"submit\" name=\"action\">View Tukang</button>");
                        out.println("</form><br>");
                        out.println("<form action=\"logoutHandler\" method=\"post\">");
                        out.println("<button class=\"btn waves-effect waves-light\" type=\"submit\" name=\"action\">Logout</button>");
                        out.println("</form></div></div></div></li>");
                        out.println("</ul></div>");
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
