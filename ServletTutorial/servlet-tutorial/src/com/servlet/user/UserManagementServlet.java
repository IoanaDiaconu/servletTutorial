package com.servlet.user;

import com.db.DatabaseManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Ioana on 1/31/2016.
 * Responsible for updating the user information if a user exist otherwise a new user is created
 */
public class UserManagementServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession httpSession = request.getSession();

        final String userQuery;
        Integer accessCount;
        String username;
        String requestName;
        synchronized (httpSession) {
            accessCount = (Integer) httpSession.getAttribute("accessCount");
            username = (String) httpSession.getAttribute("name");
            requestName =  request.getParameter("name");
            if (accessCount == null && (username == null || !username.equals(requestName))) {
                accessCount = 0;
                username = requestName;
                userQuery = insertUser();
            } else {
                accessCount += 1;
                userQuery = updateUser();
            }
            httpSession.setAttribute("accessCount", accessCount);
            httpSession.setAttribute("name", username);
        }

        PreparedStatement preparedStatement = null;
        Connection conn = DatabaseManager.getConnection();

        // Execute SQL query
        try {
            preparedStatement = conn.prepareStatement(userQuery);
            preparedStatement.setInt(1, accessCount);
            preparedStatement.setDate(2, new java.sql.Date(new Date().getTime()));
            preparedStatement.setString(3, username);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null)
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

        response.sendRedirect("/userInfo");

    }


    private String insertUser() {
        return "INSERT INTO users(countVisit,dateOfVisit,name) VALUES(?,?,?)";
    }

    private String updateUser() {
        return "UPDATE users SET countVisit=? , dateOfVisit=? WHERE name=?";
    }
}
