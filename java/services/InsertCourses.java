package services;

import models.*;
import org.slf4j.Logger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InsertCourses {
  public static void insertCourses(Logger logger, List<Course> courses) {
    try (Connection conn = GetConnection.getConnection()) {

    } catch (SQLException e) {
    }
  }
}
