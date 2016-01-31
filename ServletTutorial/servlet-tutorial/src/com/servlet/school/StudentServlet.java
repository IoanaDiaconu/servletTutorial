package com.servlet.school;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by Ioana on 1/24/2016.
 */
public class StudentServlet extends HttpServlet {
    // JDBC driver name and database URL
    static final String DB_URL = "jdbc:mysql://localhost/school";

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "passw0rd";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession httpSession = request.getSession();

        String title = "Database Result";
        String docType =
                "<!doctype html public \"-//w3c//dtd html 4.0 " +
                        "transitional//en\">\n";
        out.println(docType +
                "<html>\n" +
                "<head><title>" + title + "</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + title + "</h1>\n");
        Statement stmt = null;
        PreparedStatement preparedStatement;
        Connection conn = null;
        final String sqlQuery;
        final String SQL_STUDENT_INFO ;

        Integer accessCount;
        synchronized (httpSession) {
            accessCount = (Integer) httpSession.getAttribute("accessCount");
            if (accessCount == null) {
                accessCount = 0;
                sqlQuery = insertNewUser(request);
                SQL_STUDENT_INFO = "SELECT firstname, secondname, grade as info from students where firstname=? and secondname=?";
            } else {
                accessCount += 1;
                sqlQuery = updateUser(request);
                SQL_STUDENT_INFO = "SELECT name as info FROM class " +
                        "WHERE id =(SELECT class_id from student_class " +
                        "WHERE student_id=(select id from students where firstname=? and secondname=?))";
            }
            httpSession.setAttribute("accessCount", accessCount);
        }


        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            // Execute SQL query
            stmt = conn.createStatement();
            String sql = sqlQuery;
            stmt.executeUpdate(sql);
           conn.commit();
            preparedStatement = conn.prepareStatement(SQL_STUDENT_INFO);
            preparedStatement.setString(1, (String) request.getAttribute("name"));
            preparedStatement.setString(2, (String) request.getAttribute("lastname"));
            ResultSet rs = preparedStatement.executeQuery();

            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String info = rs.getString("info");

                //Display values

                out.println("Info: " + info +  "<br>");
            }
            out.println("</body></html>");

        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            if (stmt != null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }
    }

    private String updateUser(HttpServletRequest request) {
        String studentFirstname = request.getParameter("name");
        String studentLastname = request.getParameter("lastname");
        String attendedClass = request.getParameter("class");

        return "UPDATE student_class SET class_id= " + "(select id from class where name ='" + attendedClass + "') " +
                "WHERE student_id IN (select id from students where firstname='" + studentFirstname + "' AND " +
                " secondname ='" + studentLastname + "')";

    }

    private String insertNewUser(HttpServletRequest request) {
        String studentFirstname = request.getParameter("name");
        String studentLastname = request.getParameter("lastname");
        String studentGrade = request.getParameter("grade");

        return "INSERT INTO students(firstname,secondname,grade) values('" + studentFirstname + "','" + studentLastname + "','" + studentGrade + "')";
    }
}
