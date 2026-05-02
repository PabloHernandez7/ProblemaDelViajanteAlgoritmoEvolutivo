package ar.unicen;

public class ResumenConfiguracion {
    public int id;
    public Main.ConfiguracionAG config;
    public double promedioFitness;
    public double desviacionEstandar;
    public double promedioSoluciones;

    public ResumenConfiguracion(int id, Main.ConfiguracionAG config, double f, double d, double s) {
        this.id = id; 
        this.config = config; 
        this.promedioFitness = f; 
        this.desviacionEstandar = d; 
        this.promedioSoluciones = s;
    }
}