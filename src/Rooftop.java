public class Rooftop {
  private double area; 
  private double shadingFactor; 
  private String orientation; 
  
public double getOrientationFactor() {
    switch (orientation.toUpperCase()) {
        case "S": return 1.0;
        case "E":  return 0.8;
        case "W":  return 0.8;
        case "N": return 0.6;
        default:      return 0.9;   
    }
}
  public Rooftop(double area, double shadingFactor, String orientation){
    this.area = area; 
    this.shadingFactor = shadingFactor; 
    this.orientation = orientation;
  }

  public double getEffectiveArea(){
    return area* (1-shadingFactor) * getOrientationFactor();
  }

  public String getOrientation(){
    return orientation; 
  }
}
