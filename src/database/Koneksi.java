/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author Dewi_Pamungkas
 */
public class Koneksi {
    public static Connection kon(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection konek = DriverManager.getConnection("jdbc:mysql://localhost/aaa","root","");
            return konek;   
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }  
}
