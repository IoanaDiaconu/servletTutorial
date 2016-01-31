package com.servlet.user;

import com.db.DatabaseManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * Created by Ioana on 1/31/2016.
 * Responsible for displaying user information
 */
public class UserInformationServlet extends HttpServlet {
    private static final String userInfoQuery = "SELECT name, countVisit,dateOfVisit FROM users";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // Set response content type
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(getHtmlHeader());

        Connection connection = DatabaseManager.getConnection();
        Statement statement = null;

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(userInfoQuery);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int countVisit = resultSet.getInt("countVisit");
                Date dateOfVisit = resultSet.getDate("dateOfVisit");

                //Display values
                out.println("User: " + name + " has visited site: " + countVisit + " last visit " + dateOfVisit + "<br>");
            }
            out.println("</body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            DatabaseManager.closeConnection(connection);
        }
    }


    private String getHtmlHeader() {
        String title = "Welcome";
        String docType =
                "<!doctype html public \"-//w3c//dtd html 4.0 " +
                        "transitional//en\">\n";
        return docType +
                "<html>\n" +
                "<head><title>" + title + "</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + title + "</h1>\n";
    }
}
