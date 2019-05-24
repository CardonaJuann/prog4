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
public class Ficha implements Serializable{
       
    private String color;
    private boolean estado;
    
    
    public Ficha() {
    }

    
    
    public Ficha(String color, boolean estado) {
        this.color = color;
        this.estado = estado;
    }


    
    
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean getEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }


    @Override
    public String toString() {
        return this.color;
    }

    
    
    
}
