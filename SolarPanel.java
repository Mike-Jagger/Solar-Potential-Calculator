public abstract class SolarPanel {
 protected double efficiency; 
 protected double area; 
 
 public SolarPanel(double efficiency, double area){
  this.efficiency = efficiency; 
  this.area = area; 
 }

 public abstract double computeRawOutput(double dni);

 public double getEfficiency(){
  return efficiency; 
 }
}
