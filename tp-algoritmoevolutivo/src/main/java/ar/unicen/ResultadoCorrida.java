package ar.unicen;
import java.util.List;

public class ResultadoCorrida {
    private long mejorFitness;
    private long solucionesGeneradas;
    private List<Long> historialFitness; // Para la evolución del fitness 

    public ResultadoCorrida(long mejorFitness, long solucionesGeneradas, List<Long> historialFitness) {
        this.mejorFitness = mejorFitness;
        this.solucionesGeneradas = solucionesGeneradas;
        this.historialFitness = historialFitness;
    }

    // Getters para el exportador
    public long getMejorFitness() { return mejorFitness; }
    public long getSolucionesGeneradas() { return solucionesGeneradas; }
    public List<Long> getHistorialFitness() { return historialFitness; }
}