package com.servlet.school;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by Ioana on 1/24/2016.
 * Provides the logic to provide all the logic to show the available classes in this school
 */
public class SchoolClassServlet extends HttpServlet {
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
        Connection conn = null;

        try {
            // Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            // Open a connection
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute SQL query
            stmt = conn.createStatement();
            String sql = "select c.name as classname,t.firstname as firstname ,t.lastname as lastname, s.subject as subject\n" +
                    "from class c, teacher t, subjects s \n" +
                    "where c.teacher_id = t.id and c.subject_id=s.id";
            ResultSet rs = stmt.executeQuery(sql);

            // Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                String name = rs.getString("classname");
                String teacherFirstname = rs.getString("firstname");
                String teacherLastname = rs.getString("lastname");
                String subject = rs.getString("subject");

                //Display values
                out.println("Class: " + name + " Teacher: " + teacherFirstname + " " + teacherLastname
                        + " Subject " + subject + "<br>");
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
}

