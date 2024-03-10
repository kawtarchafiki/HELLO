package bean;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import db.DbConnexion;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletResponse;


@ManagedBean


	public class Employe implements Serializable {
		private int id;
		private String nom;
	
		private String prenom;
		private Date dateNaissance;
		private String email;
		
		private boolean showAddForm=true;
		 
		

		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getPrenom() {
			return prenom;
		}
		public void setPrenom(String prenom) {
			this.prenom = prenom;
		}
		public Date getDateNaissance() {
			return dateNaissance;
		}
		public void setDateNaissance(Date dateNaissance) {
			this.dateNaissance = dateNaissance;
		}
		public String getEmail() {
			return email;
		}
		public void setEmail(String email) {
			this.email = email;
		}
		public boolean isShowAddForm() {
	        return showAddForm;
	    }

	    public void setShowAddForm(boolean showAddForm) {
	        this.showAddForm = showAddForm;
	    }

	    public boolean toggleAddForm() {
	        showAddForm =! this.showAddForm ;
	        return showAddForm;
	    }
	    
	    


		public ArrayList<Employe>  getGetListeEmploye()  {
			ArrayList<Employe> employes = new ArrayList<>();
			try {
			Connection con = null;
			DbConnexion dbConnexion=new DbConnexion();
			con=dbConnexion.getConnection();
            

            String query = "SELECT * FROM employe";
            

            try (PreparedStatement statement = con.prepareStatement(query)) {
                ResultSet resultSet = statement.executeQuery();
                
                while (resultSet.next()) {
                	Employe employe = new Employe();
                
                	employe.setId(resultSet.getInt("id"));
                	employe.setNom(resultSet.getString("nom"));
                    employe.setPrenom(resultSet.getString("prenom"));
                    employe.setDateNaissance(resultSet.getDate("dateNaissance"));
                    employe.setEmail(resultSet.getString("email"));
                    
                    employes.add(employe);
  
		        }
            }catch(Exception e) {
            	System.out.println(e);
            	
            }
	   }catch(Exception e) {
	           System.out.println(e);
	            	
	   }
            return employes;
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

		        if (!isEmailUnique(con, email)) {
		            out.print("Error: Email is not unique");
		            return;
		        }

		        String query = "INSERT INTO employe (id, nom, prenom, dateNaissance, email) VALUES (?, ?, ?, ?, ?)";

		        try (PreparedStatement statement = con.prepareStatement(query)) {
		            statement.setInt(1, this.id);
		            statement.setString(2, this.nom);
		            statement.setString(3, this.prenom);
		            statement.setDate(4, new java.sql.Date(this.dateNaissance.getTime()));
		            statement.setString(5, this.email);

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
			 public void deleteEmploye() {
				    Connection con = null;
				    try {
				        FacesContext facesContext = FacesContext.getCurrentInstance();
				        ExternalContext externalContext = facesContext.getExternalContext();
				        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

				        PrintWriter out = response.getWriter();
				        DbConnexion dbConnexion = new DbConnexion();
				        con = dbConnexion.getConnection();

				       

				        String query = "DELETE * FROM employe WHERE ";

				        try (PreparedStatement statement = con.prepareStatement(query)) {
				            statement.setInt(1, this.id);
				            statement.setString(2, this.nom);
				            statement.setString(3, this.prenom);
				            statement.setDate(4, new java.sql.Date(this.dateNaissance.getTime()));
				            statement.setString(5, this.email);

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
			 public String editEmploye() {
				 FacesContext facesContext = FacesContext.getCurrentInstance();
				 return "oui";
				 
			 }

	

}
