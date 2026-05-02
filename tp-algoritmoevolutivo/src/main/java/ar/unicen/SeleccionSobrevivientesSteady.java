package ar.unicen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeleccionSobrevivientesSteady implements SeleccionSobrevivientesInterface {

    private int nSteady; // La cantidad a reemplazar que viene del JSON
    public SeleccionSobrevivientesSteady(int nSteady) {
        this.nSteady = nSteady;
    }

    @Override
    public List<Individuo> select(List<Individuo> poblacion, List<Individuo> hijos, int nPoblacion, int[][] matrizCosto) {
        List<Individuo> copiaOrdenadaPoblacion = this.order(poblacion, matrizCosto);
        List<Individuo> copiaOrdenadaHijos = this.order(hijos, matrizCosto);
        int cantidadAReemplazar = Math.min(this.nSteady, copiaOrdenadaHijos.size());
        
        int posInicioReemplazo = copiaOrdenadaPoblacion.size() - cantidadAReemplazar;
        for (int i = 0; i < cantidadAReemplazar; i++) {
            copiaOrdenadaPoblacion.set(posInicioReemplazo + i, copiaOrdenadaHijos.get(i));
        }
        
        return copiaOrdenadaPoblacion;
    }

    private List<Individuo> order(List<Individuo> poblacion, int[][] matriz){
        List<Individuo> copia = new ArrayList<>(poblacion);
        Comparator<Individuo> comparador = Comparator.comparingDouble(i -> i.getFitness(matriz));
        copia.sort(comparador); 
        return copia;
    }
}