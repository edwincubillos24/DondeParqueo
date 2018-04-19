package com.edwinacubillos.dondeparqueo.modelo;

public class Parqueaderos {
    String nombre, foto, valor;

    public Parqueaderos(String nombre, String foto, String valor) {
        this.nombre = nombre;
        this.foto = foto;
        this.valor = valor;
    }

    public Parqueaderos() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
