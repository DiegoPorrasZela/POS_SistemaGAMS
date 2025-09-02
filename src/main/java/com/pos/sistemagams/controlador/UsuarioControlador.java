package com.pos.sistemagams.controlador;

import com.pos.sistemagams.dao.UsuarioDAO;
import com.pos.sistemagams.modelo.Usuario;

public class UsuarioControlador {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    public Usuario autenticarUsuario(String email, String password) {
        return usuarioDAO.autenticar(email, password);
    }
}