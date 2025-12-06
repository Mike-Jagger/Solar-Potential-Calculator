public class PolycrystallinePanel extends SolarPanel {
  private double degradationFactor; 

  public PolycrystallinePanel(double efficiency, double area, double degradationFactor){
    super(efficiency, area);
    this.degradationFactor = degradationFactor;


  }

  @Override 
  public double computeRawOutput(double dni){
    return dni*area*efficiency*(1-degradationFactor);
  }
}
