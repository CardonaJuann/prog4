/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grafociudades.modelo;

import java.io.Serializable;

/**
 *
 * @author carloaiza
 */
public class Vertice implements Serializable{
    private int codigo;
    private Ficha dato;

    public Vertice(int codigo, Ficha dato) {
        this.codigo = codigo;
        this.dato = dato;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public Ficha getDato() {
        return dato;
    }

    public void setDato(Ficha dato) {
        this.dato = dato;
    }

    @Override
    public String toString() {
        return codigo + " " + dato;
    }
    
    
    
    
    
    
}
