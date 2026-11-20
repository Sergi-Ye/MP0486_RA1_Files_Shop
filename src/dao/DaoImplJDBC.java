package dao;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import main.Shop;
import model.Employee;
import model.Product;
import model.Sale;

public class DaoImplJDBC implements Dao {
	Connection connection;

	@Override
	public void connect() {
		// Define connection parameters
		String url = "jdbc:mysql://localhost:3306/shop";
		String user = "root";
		String pass = "";
		try {
			this.connection = DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	// Create table Inventory
	public void createInventoryTable() {
	
	    String createTable = "CREATE TABLE IF NOT EXISTS inventory ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "name VARCHAR(100) NOT NULL, "
	            + "wholesalerPrice DECIMAL(10,2) NOT NULL, "
	            + "available BOOLEAN NOT NULL, "
	            + "stock INT NOT NULL"
	            + ")";
	
	    String insertProducts = "INSERT IGNORE INTO inventory "
	            + "(id, name, wholesalerPrice, available, stock) VALUES "
	            + "(1, 'Manzana', 10.00, TRUE, 10), "
	            + "(2, 'Fresa', 5.00, TRUE, 20)";
	
	    try (Statement statement = connection.createStatement()) {
	
	        // Crear tabla
	        statement.executeUpdate(createTable);
	        // Insertar productos iniciales
	        statement.executeUpdate(insertProducts);
	
	        System.out.println("Tabla inventory preparada correctamente.");
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	@Override
	public Employee getEmployee(int employeeId, String password) {
		Employee employee = null;
		String query = "select * from employee where employeeId= ? and password = ? ";
		
		try (PreparedStatement ps = connection.prepareStatement(query)) { 
    		ps.setInt(1,employeeId);
    	  	ps.setString(2,password);
    	  	//System.out.println(ps.toString());
            try (ResultSet rs = ps.executeQuery()) {
            	if (rs.next()) {
            		employee = new Employee(rs.getInt(1), rs.getString(2), rs.getString(3));
            	}
            }
        } catch (SQLException e) {
			// in case error in SQL
			e.printStackTrace();
		}
    	return employee;
	}
	


	@Override
	public ArrayList<Product> getInventory() {
	
	    ArrayList<Product> inventory = new ArrayList<>();
	
	    String query = "SELECT * FROM inventory";
	
	    try (PreparedStatement ps = connection.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {
	
	        int maxId = 0;
	
	        while (rs.next()) {
	
	            int id = rs.getInt("id");
	            String name = rs.getString("name");
	            double wholesalerPrice = rs.getDouble("wholesalerPrice");
	            boolean available = rs.getBoolean("available");
	            int stock = rs.getInt("stock");
	
	            Product product = new Product(
	                name,
	                new model.Amount(wholesalerPrice),
	                available,
	                stock
	            );
	
	            product.setId(id);
	
	            inventory.add(product);
	
	            maxId = Math.max(maxId, id);
	        }
	
	        Product.setTotalProducts(
	            Math.max(Product.getTotalProducts(), maxId)
	        );
	
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return inventory;
	}

	
	public boolean writeInventory(ArrayList<Product> inventory){return false;}

}
