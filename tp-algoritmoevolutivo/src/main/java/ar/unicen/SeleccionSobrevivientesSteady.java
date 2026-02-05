package ar.unicen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeleccionSobrevivientesSteady implements SeleccionSobrevivientesInterface {

     //reemplazar n peores poblacion
     //n parametro definido

    
    @Override
    public List<Individuo> select(List<Individuo> poblacion, List<Individuo> hijos, int n, int[][] matrizCosto) {
        List<Individuo> copiaOrdenadaPoblacion = new ArrayList<>();
        copiaOrdenadaPoblacion = this.order(poblacion, matrizCosto);
        List<Individuo> copiaOrdenadaHijos = new ArrayList<>();
        copiaOrdenadaHijos = this.order(hijos, matrizCosto);
        if (poblacion.size() >= n && hijos.size() >=n) {
            int pos = poblacion.size() - n;
            for (int i = pos ;  i < poblacion.size(); i++ ){
                copiaOrdenadaPoblacion.set(i, copiaOrdenadaHijos.get(0));
                copiaOrdenadaHijos.remove(0);
            }
            return copiaOrdenadaPoblacion;
        }
        else {
            if (hijos.size() < n) {
                 throw new IllegalStateException(
                  "No hay suficientes hijos para reemplazar " + n + " individuos");
                 }
            if (poblacion.size() < n) {
                 throw new IllegalStateException(
                  "No hay suficiente poblacion para reemplazar " + n + " individuos");
            }
        }
        return copiaOrdenadaPoblacion;
    }

    private List<Individuo> order(List<Individuo> poblacion,int[][] matriz){
        List<Individuo> copia = new ArrayList<>(poblacion);
        Comparator<Individuo> comparador = Comparator.comparingDouble(i -> i.getFitness(matriz)); //es un lamda como ordena?
        copia.sort(comparador.reversed()); //mejor fitness (mayor) primero
        return copia;
    }
}
