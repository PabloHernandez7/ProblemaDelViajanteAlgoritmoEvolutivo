package ar.unicen;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SeleccionSobrevivientesRuleta implements SeleccionSobrevivientesInterface {

    @Override
    public List<Individuo> select(List<Individuo> poblacion, List<Individuo> hijos, int n, int[][] matr) {
        List<Individuo> todos = new ArrayList<>(poblacion);
        todos.addAll(hijos);
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
                todos.sort(comp);
                continue;
            }

            while (!encontrado && i < todos.size()) {
                Individuo ind = todos.get(i);

                // Convertimos a probabilidad
                prob_acum += this.probaAcumulada(ind, total, matr);
                if (prob <= prob_acum) {
                    seleccionados.add(ind);
                    encontrado = true;
                    todos.remove(i);
                    todos.sort(comp);
                } else {
                    i++;
                }
            }

            // Si por redondeo no encontró ninguno, agarrá el último.
            if (!encontrado && !todos.isEmpty()) {
                Individuo ind = todos.get(todos.size() - 1);
                seleccionados.add(ind);
                todos.remove(todos.size() - 1);
                todos.sort(comp);
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