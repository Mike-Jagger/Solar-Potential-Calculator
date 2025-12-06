public class Rooftop {
  private double area; 
  private double shadingFactor; 
  private String orientation; 

  public Rooftop(double area, double shadingFactor, String orientation){
    this.area = area; 
    this.shadingFactor = shadingFactor; 
    this.orientation = orientation;
  }

  public double getEffectiveArea(){
    return area* (1-shadingFactor);
  }

  public String getOrientation(){
    return orientation; 
  }
}
