package org.Main;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class CustomerProfileUpdater {

    private static final String DB_URL = "jdbc:oracle:thin:@//localhost:1521/orcl";
    private static final String DB_USER = "system"; // Connect1 username
    private static final String DB_PASSWORD = "R913e822"; // Connect1 password

    public static void main(String[] args) {
        try {
            // Test connection
            testDatabaseConnection();

            URI uri = new URI("http://example.com?phone=8475782341&email=aason@gmail.com");
            String phone = getQueryParam(uri, "phone");

            if (phone != null) {
                System.out.println("Phone number from URL: " + phone);
                Cust_profile existingProfile = getCustomerProfileFromDatabase(phone);
                if (existingProfile != null) {
                    System.out.println("Customer found in database: " + existingProfile);
                } else {
                    System.out.println("No existing user found with the given phone number: " + phone);
                }

                // Print all records in the CUST_INFO table
                printAllCustomers();
            } else {
                System.out.println("Phone number missing in the URL.");
            }
        } catch (SQLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void testDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            if (connection != null)
            {
                System.out.println("Connected!");
            }
            else
            {
                System.out.println("Failed!");

            }
        } catch (SQLException e) {
            e.printStackTrace();


        }


    }

    private static String getQueryParam(URI uri, String param) {
        String[] queryParams = uri.getQuery().split("&");
        for (String queryParam : queryParams) {
            String[] keyValue = queryParam.split("=");
            if (keyValue[0].equals(param)) {
                return keyValue[1];

            }
        }
        return null;
    }

    private static Cust_profile getCustomerProfileFromDatabase(String phone) throws SQLException {
        Cust_profile profile = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String querySQL = "SELECT * FROM CUST_INFO WHERE Phone_num = ?";
            System.out.println("Executing SQL: " + querySQL + " with phonenum = " + phone);
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
                        System.out.println("Customer found: " + profile);
                    }
                    else {
                        System.out.println("No customer found  for phonenum: " + phone);
                    }
                }
            }
        }
        return profile;
    }

    private static void printAllCustomers() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String querySQL = "SELECT * FROM CUST_INFO";
            System.out.println("Using SQL: " + querySQL); // Logging
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySQL)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    System.out.println("CUST_INFO Records:");
                    while (resultSet.next()) {
                        System.out.println("customer_id: " + resultSet.getInt("customer_id") +
                                ", f_name: " + resultSet.getString("f_name") +
                                ", l_name: " + resultSet.getString("l_name") +
                                ", Email: " + resultSet.getString("Email") +
                                ", Phone_num: " + resultSet.getString("Phone_num") +
                                ", lastUpdatedDate: " + resultSet.getString("lastUpdatedDate") +
                                ", lastUpdatedUser: " + resultSet.getString("lastUpdatedUser") +
                                ", Source: " + resultSet.getString("Source"));
                    }
                }
            }
        }
    }
}

class CustomerProfile {
    private int customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String lastUpdatedDate;
    private String lastUpdatedUser;
    private String source;

    //
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