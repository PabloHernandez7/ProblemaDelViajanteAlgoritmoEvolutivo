package ar.unicen;

import java.util.List;

public interface SeleccionPadresInterface {
    public List<Individuo> select(List<Individuo> poblacion, int[][] matriz);
}
