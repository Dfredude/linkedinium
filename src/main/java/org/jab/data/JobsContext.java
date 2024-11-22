package org.jab.data;


import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;


public class JobsContext {
    private Connection conn;
    final private String connectionUrl = "jdbc:mysql://localhost:3306/job_apply_bot?serverTimezone=UTC";
    private String user;
    private String password;
    public JobsContext() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = rootPath + "app.properties";
//        String catalogConfigPath = rootPath + "catalog";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
//            Properties catalogProps = new Properties();
//            catalogProps.load(new FileInputStream(catalogConfigPath));
            user = appProps.getProperty("MYSQL_USERNAME");
            password = appProps.getProperty("MYSQL_PASSWORD");
            conn = DriverManager.getConnection(connectionUrl, user, password);
        } catch (SQLException e) {
            System.out.println("Error connecting to database");
        }
        catch (Exception e) {
            System.out.println("Error loading app.properties");
        }



    }
    public int insertPendingJob(String jobID) {
        String sqlInsertJob = "INSERT INTO pending_jobs VALUES ( " + jobID + " )";
        try ( PreparedStatement ps = conn.prepareStatement(sqlInsertJob)) {
            ps.executeUpdate();
            System.out.println("Job inserted: " + jobID);
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
    public int increaseCounter(String jobID) {
        String sqlInsertJob = "INSERT INTO jobs VALUES ( " + jobID + " )";
        try (PreparedStatement ps = conn.prepareStatement(sqlInsertJob)) {
            ps.executeUpdate();
            System.out.println("Job inserted: " + jobID);
            return 1;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public boolean isJobPending(String jobID) {
        String sqlSelectJob = "SELECT * FROM pending_jobs WHERE job_id = " + jobID;
        try (Connection conn = DriverManager.getConnection(connectionUrl, user, password);
             PreparedStatement ps = conn.prepareStatement(sqlSelectJob)
        ) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Job is pending: " + jobID);
                return true;
            } else {
                System.out.println("Job is not pending: " + jobID);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean hasBeenAppliedTo(String jobID) {
        String sqlSelectJob = "SELECT * FROM jobs WHERE ID = " + jobID;
        try (Connection conn = DriverManager.getConnection(connectionUrl, user, password);
             PreparedStatement ps = conn.prepareStatement(sqlSelectJob)
        ) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Job has been applied to: " + jobID);
                return true;
            } else {
                System.out.println("Job has not been applied to: " + jobID);
                return false;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public int getTotalCount() {
        String sqlSelectJob = "SELECT COUNT(*) FROM jobs";

        try (PreparedStatement ps = conn.prepareStatement(sqlSelectJob)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Total count: " + rs.getInt(1));
                return rs.getInt(1);
            } else {
                System.out.println("Total count: 0");
                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }
}
