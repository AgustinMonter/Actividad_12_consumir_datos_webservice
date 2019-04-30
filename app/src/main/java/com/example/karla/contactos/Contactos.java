package com.example.karla.contactos;

public class Contactos  {
    private int id_contacto;
    private String nombre;
    private String telefono;

    public Contactos() {
    }

    public Contactos(int id_contacto, String nombre, String telefono) {
        this.id_contacto = id_contacto;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public int getId_contacto() {
        return id_contacto;
    }

    public void setId_contacto(int id_contacto) {
        this.id_contacto = id_contacto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
