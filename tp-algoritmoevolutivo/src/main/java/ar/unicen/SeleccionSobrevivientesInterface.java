package ar.unicen;


import java.util.List;

public interface SeleccionSobrevivientesInterface {
    public List<Individuo> select(List<Individuo> poblacion, List<Individuo> hijos, int n, int[][] matrizCosto);
}
