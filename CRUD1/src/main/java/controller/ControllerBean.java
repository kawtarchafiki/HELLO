package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DbConnexion;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletResponse;
import model.Employe;

@ManagedBean

public class ControllerBean {
	private List<Employe> employeList;
	private Employe employe=new Employe();
	



	public List<Employe> getGetListeEmploye() {
        

        List<Employe> employes = new ArrayList<>();
        try {
            Connection con = null;
            DbConnexion dbConnexion = new DbConnexion();
            con = dbConnexion.getConnection();

            String query = "SELECT * FROM employe";
            try (PreparedStatement statement = con.prepareStatement(query)) {

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Employe employe = new Employe();
                        employe.setId(resultSet.getInt("id"));
                        employe.setNom(resultSet.getString("nom"));
                        employe.setPrenom(resultSet.getString("prenom"));
                        employe.setDateNaissance(resultSet.getDate("dateNaissance"));
                        employe.setEmail(resultSet.getString("email"));
                        employes.add(employe);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return employes;
    }

	
	    public void setEmployeList(List<Employe> employeList) {
			this.employeList=employeList;
		}


	    public Employe getEmploye() {
		   return employe;
	    }


	    public void setEmploye(Employe employe) {
		    this.employe = employe;
	    }
	    

		public void addEmploye() {
		    Connection con = null;
		    try {
		        FacesContext facesContext = FacesContext.getCurrentInstance();
		        ExternalContext externalContext = facesContext.getExternalContext();
		        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

		        PrintWriter out = response.getWriter();
		        DbConnexion dbConnexion = new DbConnexion();
		        con = dbConnexion.getConnection();

		        if (!isEmailUnique(con, employe.getEmail())) {
		            out.print("Error: Email is not unique");
		            return;
		        }

		        String query = "INSERT INTO employe (id, nom, prenom, dateNaissance, email) VALUES (?, ?, ?, ?, ?)";

		        try (PreparedStatement statement = con.prepareStatement(query)) {
		            statement.setInt(1, this.employe.getId());
		            statement.setString(2, this.employe.getNom());
		            statement.setString(3, this.employe.getPrenom());
		            statement.setDate(4, new java.sql.Date(this.employe.getDateNaissance().getTime()));
		            statement.setString(5, this.employe.getEmail());

		            statement.executeUpdate();
		            out.print("Employee added successfully!");
		        } catch (SQLException e) {
		            out.print("Error executing SQL query: " + e.getMessage());
		        }
		    } catch (IOException e) {
		        System.out.println("Error obtaining PrintWriter: " + e.getMessage());
		    } catch (Exception e) {
		        System.out.println("Error establishing database connection: " + e.getMessage());
		    } finally {
		        try {
		            if (con != null) {
		                con.close();
		            }
		        } catch (SQLException e) {
		            System.out.println("Error closing database connection: " + e.getMessage());
		        }
		    }
		}


			 private boolean isEmailUnique(Connection con, String email) {
			        try {
			            String query = "SELECT COUNT(*) FROM employe WHERE email = ?";
			            try (PreparedStatement statement = con.prepareStatement(query)) {
			                statement.setString(1, email);

			                try (ResultSet resultSet = statement.executeQuery()) {
			                    if (resultSet.next()) {
			                        int count = resultSet.getInt(1);
			                        return count == 0; 
			                    }
			                }
			            }
			        } catch (Exception e) {
			            System.out.println("Error checking email uniqueness: " + e.getMessage());
			        }
			        return false; 
			}
			 
			 
			 public void editEmploye() {
				
				    Connection con = null;
				    try {
				        FacesContext facesContext = FacesContext.getCurrentInstance();
				        ExternalContext externalContext = facesContext.getExternalContext();
				        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

				        PrintWriter out = response.getWriter();
				        DbConnexion dbConnexion = new DbConnexion();
				        con = dbConnexion.getConnection();

				        String oldEmail = getOldEmailFromDatabase(con, this.employe.getId());
				        this.employe.setOldEmail(oldEmail);

				       
				        if (!this.employe.getEmail().equals(oldEmail) && !isEmailUnique(con, this.employe.getEmail())) {
				            out.print("Error: Email is not unique");
				            return;
				        }

				        String query = "UPDATE employe SET nom=?, prenom=?, dateNaissance=?, email=? WHERE id=?";

				        try (PreparedStatement statement = con.prepareStatement(query)) {
				            statement.setString(1, this.employe.getNom());
				            statement.setString(2, this.employe.getPrenom());
				            statement.setDate(3, new java.sql.Date(this.employe.getDateNaissance().getTime()));
				            statement.setString(4, this.employe.getEmail());
				            statement.setInt(5, this.employe.getId());

				            int rowsUpdated = statement.executeUpdate();

				            if (rowsUpdated > 0) {
				                out.print("Employee updated successfully!");
				            } else {
				                out.print("No employee found with the given ID for update.");
				            }
				        } catch (SQLException e) {
				            out.print("Error executing SQL query: " + e.getMessage());
				        }
				    } catch (IOException e) {
				        System.out.println("Error obtaining PrintWriter: " + e.getMessage());
				    } catch (Exception e) {
				        System.out.println("Error establishing database connection: " + e.getMessage());
				    } finally {
				        try {
				            if (con != null) {
				                con.close();
				            }
				        } catch (SQLException e) {
				            System.out.println("Error closing database connection: " + e.getMessage());
				        }
				    }
				}

				
				private String getOldEmailFromDatabase(Connection con, int employeId) {
				    try {
				        String query = "SELECT email FROM employe WHERE id = ?";
				        try (PreparedStatement statement = con.prepareStatement(query)) {
				            statement.setInt(1, employeId);

				            try (ResultSet resultSet = statement.executeQuery()) {
				                if (resultSet.next()) {
				                    return resultSet.getString("email");
				                }
				            }
				        }
				    } catch (SQLException e) {
				        System.out.println("Error getting old email from database: " + e.getMessage());
				    }
				    return null;
				}

			 
			 
			 public void deleteEmploye() {
				    Connection con = null;
				    try {
				        FacesContext facesContext = FacesContext.getCurrentInstance();
				        ExternalContext externalContext = facesContext.getExternalContext();
				        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

				        PrintWriter out = response.getWriter();
				        DbConnexion dbConnexion = new DbConnexion();
				        con = dbConnexion.getConnection();

				        String query = "DELETE FROM employe WHERE id=?";

				        try (PreparedStatement statement = con.prepareStatement(query)) {
				            statement.setInt(1, this.employe.getId());

				            int rowsDeleted = statement.executeUpdate();

				            if (rowsDeleted > 0) {
				                out.print("Employee deleted successfully!");
				            } else {
				                out.print("No employee found with the given ID for deletion.");
				            }
				        } catch (SQLException e) {
				            out.print("Error executing SQL query: " + e.getMessage());
				        }
				    } catch (IOException e) {
				        System.out.println("Error obtaining PrintWriter: " + e.getMessage());
				    } catch (Exception e) {
				        System.out.println("Error establishing database connection: " + e.getMessage());
				    } finally {
				        try {
				            if (con != null) {
				                con.close();
				            }
				        } catch (SQLException e) {
				            System.out.println("Error closing database connection: " + e.getMessage());
				        }
				    }
				}
			 
			 
			 
			 public void cancelEmploye() {
			        this.employe = new Employe(); 
			    }
			 
			 
			 
			 
			 public int getTotalRecords() {
				    int totalRecords = 0;
				    Connection con = null;

				    try {
				        DbConnexion dbConnexion = new DbConnexion();
				        con = dbConnexion.getConnection();

				        String query = "SELECT COUNT(*) FROM employe";

				        try (PreparedStatement statement = con.prepareStatement(query)) {
				            try (ResultSet resultSet = statement.executeQuery()) {
				                if (resultSet.next()) {
				                    totalRecords = resultSet.getInt(1);
				                }
				            }
				        }
				    } catch (SQLException e) {
				        System.out.println("Error executing SQL query: " + e.getMessage());
				    } finally {
				        try {
				            if (con != null) {
				                con.close();
				            }
				        } catch (SQLException e) {
				            System.out.println("Error closing database connection: " + e.getMessage());
				        }
				    }

				    return totalRecords;
			 }







}
