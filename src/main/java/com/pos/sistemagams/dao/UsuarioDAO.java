/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pos.sistemagams.dao;

import com.pos.sistemagams.modelo.Usuario;
import com.pos.sistemagams.util.ConexionBD;
import java.sql.*;

/**
 *
 * @author Diego
 */
public class UsuarioDAO {
    
    public Usuario autenticar(String email, String password) {
        String sql = "SELECT id_usuario, nombre, apellido, email, rol, estado "
                   + "FROM usuarios WHERE email = ? AND password = ?";
        
        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setApellido(rs.getString("apellido"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setEstado(rs.getBoolean("estado"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error en autenticación: " + e.getMessage());
        }
        return null;
    }
}
