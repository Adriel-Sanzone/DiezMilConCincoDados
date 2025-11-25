package com.example.diezmilconcincodados.observer;

import java.io.Serializable;

public interface Observable extends Serializable
{
    void agregarObservador(Observador observador);
    void quitarObservador(Observador observador);
    void notificarObservadores();
}
