public class MonocrystallinePanel extends SolarPanel{
  private double temperatureCoeff; 

  public MonocrystallinePanel(double efficiency, double area, double temperatureCoeff){
    super(efficiency, area);
    this.temperatureCoeff = temperatureCoeff;
  }
  
  @Override 
  public double computeRawOutput(double dni){
    return dni* area * efficiency * (1-temperatureCoeff);
  }
}
