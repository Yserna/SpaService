package com.example.diego.spaservice.Ref;

import android.net.Uri;

public class Users {

    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private String contrasena;
    private String perfil;
    private String educacion;
    private String experiencia;
    private String perfilPro;
    private String servicios;
    private String foto;

    public Users() {
    }

    public Users(String nombre, String direccion, String telefono, String email,
                 String contrasena, String perfil, String educacion, String experiencia, String perfilPro,
                 String servicios, String foto) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.contrasena = contrasena;
        this.perfil = perfil;
        this.educacion = educacion;
        this.experiencia = experiencia;
        this.perfilPro = perfilPro;
        this.servicios = servicios;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getEducacion() {
        return educacion;
    }

    public void setEducacion(String educacion) {
        this.educacion = educacion;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }

    public String getPerfilPro() {
        return perfilPro;
    }

    public void setPerfilPro(String perfilPro) {
        this.perfilPro = perfilPro;
    }

    public String getServicios() {
        return servicios;
    }

    public void setServicios(String servicios) {
        this.servicios = servicios;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Users{" +
                "nombre='" + nombre + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", contrasena='" + contrasena + '\'' +
                ", perfil='" + perfil + '\'' +
                '}';
    }
}
