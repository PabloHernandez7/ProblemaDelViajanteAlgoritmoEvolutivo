package ar.unicen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeleccionSobrevivientesRuleta implements SeleccionSobrevivientesInterface {

    @Override
    public List<Individuo> select(List<Individuo> poblacion, List<Individuo> hijos, int n, int[][] matr) {
        List<Individuo> todos = new ArrayList<>(poblacion);
        todos.addAll(hijos);
        
        // CORRECCIÓN 1: El Comparator. 
        // Dividir por el "total" no cambia el orden de los elementos porque el total es igual para todos.
        // Ordenar por (1.0 / fitness) da EXACTAMENTE el mismo orden que tu código original, 
        // pero evita iterar toda la lista en cada comparación del sort.
        Comparator<Individuo> comp = Comparator.comparingDouble(
                ind -> 1.0 / ind.getFitness(matr)
        );
        todos.sort(comp);

        List<Individuo> seleccionados = new ArrayList<>();

        for (int j = 0; j < n; j++) {

            boolean encontrado = false;
            int i = 0;
            double prob = Math.random();     // [0,1)
            double prob_acum = 0.0;
            double total = this.sumatoriaFitness(todos, matr);

            // Si total == 0, no podés hacer ruleta proporcional
            if (total <= 0.0) {
                Individuo elegido = todos.get(0);
                seleccionados.add(elegido);
                todos.remove(0);
                // CORRECCIÓN 2: Se eliminó el "todos.sort(comp);" de aquí.
                continue;
            }

            while (!encontrado && i < todos.size()) {
                Individuo ind = todos.get(i);

                // Convertimos a probabilidad (Tu código original)
                prob_acum += this.probaAcumulada(ind, total, matr);
                if (prob <= prob_acum) {
                    seleccionados.add(ind);
                    encontrado = true;
                    todos.remove(i);
                    // CORRECCIÓN 2: Se eliminó el "todos.sort(comp);" de aquí.
                } else {
                    i++;
                }
            }

            // Si por redondeo no encontró ninguno, agarrá el último.
            if (!encontrado && !todos.isEmpty()) {
                Individuo ind = todos.get(todos.size() - 1);
                seleccionados.add(ind);
                todos.remove(todos.size() - 1);
                // CORRECCIÓN 2: Se eliminó el "todos.sort(comp);" de aquí.
            }
        }

        return seleccionados;
    }

    // Tus métodos originales se mantienen intactos
    private Double probaAcumulada(Individuo i, Double total, int[][] matriz) {
        return (1.0 / i.getFitness(matriz)) / total;
    }

    private Double sumatoriaFitness(List<Individuo> lista, int[][] matriz) {
        Double total = 0.0;
        for (Individuo i : lista) {
            total = total + (1.0 / i.getFitness(matriz));
        }
        return total;
    }
}