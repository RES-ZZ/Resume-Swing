package com.resumebuilder.dao;

import com.resumebuilder.model.Resume;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
	// DEFINING CONNECTION FOR THE DATABASE CONNECTION
	private static final String URL = "jdbc:mysql://localhost:3306/resumebuilder";
	private static final String USER = "root";
	private static final String PASSWORD = "12345678";



	//ESTABLISHING THE CONNECTION
	public static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");


			connection = DriverManager.getConnection(URL,USER,PASSWORD);
			System.out.println("Connection established successfully!");
		}
		catch (SQLException | ClassNotFoundException e){
		    e.printStackTrace();
		}
		return connection;
	}

	//CLOSING THE CONNECTION
	public static void closeConnection(Connection connection) {
		try{
			if(connection != null && !connection.isClosed()){
				connection.close();
				System.out.println("Connection closed successfully!");
			}

		}
		catch (SQLException e){
			e.printStackTrace();
		}
	}
	public  void saveResume(Resume resume) throws SQLException {
		// Save resume to database
		String sql  = "INSERT INTO resume (job_title,first_name,last_name,email,phone,country,city,professional_summary) VALUES (?,?,?,?,?,?,?,?)";
		try (Connection connection = getConnection()){
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1,resume.getJobTitle());
			pstmt.setString(2,resume.getFirstName());
			pstmt.setString(3,resume.getLastName());
			pstmt.setString(4,resume.getEmail());
			pstmt.setString(5, resume.getPhone());
			pstmt.setString(6,resume.getCountry());
			pstmt.setString(7, resume.getCity());
			pstmt.setString(8, resume.getProfessionalSummary());

			//Executing the insertion operation !!!!!!!!!!!!!

			pstmt.executeUpdate();
			System.out.println("Resume saved successfully!");

	}
		catch (SQLException e) {
			// Rethrow the exception to be handled by the calling method
			throw e;
		}
}
	public List<Resume> getAllResume() throws SQLException {
		List<Resume> resumes = new ArrayList<>();
		String query = "SELECT * FROM resume";

		try (Connection connection = getConnection();
		     PreparedStatement pstmt = connection.prepareStatement(query);
			 ResultSet rs = pstmt.executeQuery()) {
			while (rs.next()){
				Resume resume = new Resume();
				resume.setId(rs.getInt("id"));
				resume.setJobTitle(rs.getString("job_title"));
				resume.setFirstName(rs.getString("first_name"));
				resume.setLastName(rs.getString("last_name"));
				resume.setEmail(rs.getString("email"));
				resume.setPhone(rs.getString("phone"));
				resume.setCountry(rs.getString("country"));
				resume.setCity(rs.getString("city"));
				resume.setProfessionalSummary(rs.getString("professional_summary"));

				resumes.add(resume);// add the resume to the list
			}



		}
		catch (SQLException e){
			e.printStackTrace();









		}
		return resumes; // Return the list of resume

	}
	public void deleteResume(int id) throws SQLException {
		String sql = "DELETE FROM resume WHERE id = ?";
		try (Connection connection = getConnection();
		     PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
			System.out.println("Resume deleted successfully!");
		}
	}
}