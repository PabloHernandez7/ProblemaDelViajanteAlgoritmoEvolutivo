import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Individuo {
    private List<Integer> permutaciones;
    private boolean flagFitness;
    private long fitness;

    public Individuo(List<Integer> list, int[][] matrizCosto) {
        permutaciones.addAll(list);
        this.permutar();
    }
    
    public List<Integer> getCopiaPermutaciones() {
        List<Integer> list = new ArrayList<>();
        list.addAll(permutaciones);
        return list;
    }

    public List<Integer> getCopiaSubList(int i,int j){
        List<Integer> list = new ArrayList<>();
        for (int inic = i; inic <= j; inic++ ){
            list.add(permutaciones.get(inic));
        }
        return list;
    }

    public void setSublistPermutacion(List<Integer> subList, int i, int j){
        for (int inic = i; inic <= j; inic++ ){           
            int segundoElemento = subList.get(0);
            permutaciones.set(i, segundoElemento);
        }
    }

    public void permutar(){
        Collections.shuffle(permutaciones); //reordena al azar
        flagFitness = false;
    }

    public long getFitness(int[][] matriz){
        if (!flagFitness) {
            fitness = this.calcularCosto(matriz);
        } 
        return fitness;
    }

    private long calcularCosto(int[][] matriz){
        int aux = 0;
        for (int i = 0; i < permutaciones.size() - 1; i++ ){
            int fila = permutaciones.get(i);
            int column = permutaciones.get(i+1);
            aux = aux + matriz[fila][column];
        }
        return aux;
    }

    public int getSizePermutacion(){
        return permutaciones.size();
    }

    public void swap(int i, int j){ //son posiciones
        int primerElemento = permutaciones.get(i);
        int segundoElemento = permutaciones.get(j);
        permutaciones.set(i, segundoElemento);
        permutaciones.set(j, primerElemento);
        flagFitness = false;
    }

}
