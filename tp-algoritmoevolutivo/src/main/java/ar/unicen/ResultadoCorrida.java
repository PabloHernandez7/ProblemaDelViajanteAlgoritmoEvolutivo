package ar.unicen;
import java.util.List;

public class ResultadoCorrida {
    private long mejorFitness;
    private long solucionesGeneradas;
    private List<Long> historialFitness;
    private List<Individuo> poblacionFinal;

    public ResultadoCorrida(long mejorFitness, long solucionesGeneradas, List<Long> historialFitness, List<Individuo> poblacionFinal) {
        this.mejorFitness = mejorFitness;
        this.solucionesGeneradas = solucionesGeneradas;
        this.historialFitness = historialFitness;
        this.poblacionFinal = poblacionFinal;
    }

    public long getMejorFitness() { return mejorFitness; }
    public long getSolucionesGeneradas() { return solucionesGeneradas; }
    public List<Long> getHistorialFitness() { return historialFitness; }
    public List<Individuo> getPoblacionFinal() { return poblacionFinal; }
}