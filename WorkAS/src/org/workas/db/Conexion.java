/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.workas.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author PC
 */
public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/workas_db?useSSL=false";
    private static final String user = "quintom";
    private static final String password = "admin";
    private static final String driver = "com.mysql.jdbc.Driver";
    
    public void Conexion(){
        conectar();
    }
    
    public void conectar(){
        try { 
            Class.forName(driver).newInstance();  
            conexion = DriverManager.getConnection(URL, user, password);
            System.out.println("Conexion exitosa");
        }catch (ClassNotFoundException|InstantiationException|
                IllegalAccessException|SQLException ex) {
            System.out.println("Error al conectar");
            ex.printStackTrace();
        }

    }

    public static Conexion getInstancia() {
        if ( instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conectar();
            }
        } catch (SQLException eX) {
            eX.printStackTrace();
        }
        return conexion;
    }
}
