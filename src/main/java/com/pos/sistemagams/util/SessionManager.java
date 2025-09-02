package com.pos.sistemagams.util;

import com.pos.sistemagams.modelo.Usuario;

public class SessionManager {
    private static Usuario currentUser;
    
    public static void setCurrentUser(Usuario usuario) {
        currentUser = usuario;
    }
    
    public static Usuario getCurrentUser() {
        return currentUser;
    }
    
    public static boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRol());
    }
    
    public static void cerrarSesion() {
        currentUser = null;
    }
}