package org.Main;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerProfileServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";
    private static final String DB_USER = "system";
    private static final String DB_PASSWORD = "R913e822";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");

        if ((phone == null || phone.isEmpty()) && (email == null || email.isEmpty())) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing 'phone' or 'email'");
            return;
        }

        try {
            Cust_profile profile = null;
            if (phone != null && !phone.isEmpty()) {
                profile = getCustByNum(phone);
            } else if (email != null && !email.isEmpty()) {
                profile = getCustByEmail(email);
            }

            if (profile != null) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
                ObjectMapper mapper = new ObjectMapper();
                String jsonResponse = mapper.writeValueAsString(profile);
                resp.getWriter().println(jsonResponse);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("Customer not found");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        CustomerProfile profile = mapper.readValue(req.getInputStream(), CustomerProfile.class);

        if (profile.getPhone() == null || profile.getPhone().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing 'phone' parameter");
            return;
        }

        try {
            boolean updated = updateCustInfo(profile);
            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println("Customer profile updated!");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().println("Customer phone not found : " + profile.getPhone());
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private Cust_profile getCustByNum(String phone) throws SQLException {
        Cust_profile profile = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String querySQL = "SELECT * FROM CUST_INFO WHERE Phone_num = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                preparedStatement.setString(1, phone);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        profile = new Cust_profile();
                        profile.setCustomerId(resultSet.getInt("customer_id"));
                        profile.setFirstName(resultSet.getString("f_name"));
                        profile.setLastName(resultSet.getString("l_name"));
                        profile.setEmail(resultSet.getString("Email"));
                        profile.setPhone(resultSet.getString("Phone_num"));
                        profile.setLastUpdatedDate(resultSet.getString("lastUpdatedDate"));
                        profile.setLastUpdatedUser(resultSet.getString("lastUpdatedUser"));
                        profile.setSource(resultSet.getString("Source"));
                    }
                }
            } 
        }
        return profile;
    }
    private Cust_profile getCustByEmail(String email) throws SQLException {
        Cust_profile profile = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String querySQL = "SELECT * FROM CUST_INFO WHERE Email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                preparedStatement.setString(1, email);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        profile = new Cust_profile();
                        profile.setCustomerId(resultSet.getInt("customer_id"));
                        profile.setFirstName(resultSet.getString("f_name"));
                        profile.setLastName(resultSet.getString("l_name"));
                        profile.setEmail(resultSet.getString("Email"));
                        profile.setPhone(resultSet.getString("Phone_num"));
                        profile.setLastUpdatedDate(resultSet.getString("lastUpdatedDate"));
                        profile.setLastUpdatedUser(resultSet.getString("lastUpdatedUser"));
                        profile.setSource(resultSet.getString("Source"));
                    }
                }
            }
        }
        return profile;
    }

    private boolean updateCustInfo(CustomerProfile profile) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateSQL = "UPDATE CUST_INFO SET f_name = ?, l_name = ?, Email = ?, lastUpdatedDate = ?, lastUpdatedUser = ?, Source = ? WHERE Phone_num = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
                preparedStatement.setString(1, profile.getFirstName());
                preparedStatement.setString(2, profile.getLastName());
                preparedStatement.setString(3, profile.getEmail());
                preparedStatement.setString(4, profile.getLastUpdatedDate());
                preparedStatement.setString(5, profile.getLastUpdatedUser());
                preparedStatement.setString(6, profile.getSource());
                preparedStatement.setString(7, profile.getPhone());

                int rowsUpdated = preparedStatement.executeUpdate();
                return rowsUpdated > 0;
            }
        }
    }


}


class   Cust_profile {
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String lastUpdatedDate;
    private String lastUpdatedUser;
    private String source;

    // Getters and Setters

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(String lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getLastUpdatedUser() {
        return lastUpdatedUser;
    }

    public void setLastUpdatedUser(String lastUpdatedUser) {
        this.lastUpdatedUser = lastUpdatedUser;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "CustomerProfile{" +
                "customerId=" + customerId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", lastUpdatedDate='" + lastUpdatedDate + '\'' +
                ", lastUpdatedUser='" + lastUpdatedUser + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}