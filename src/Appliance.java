public class Appliance {
  private String name; 
  private double watts; 

  public Appliance(String name, double watts){
    this.name = name; 
    this.watts = watts; 
  }

  public String getName(){
    return name;
  }

  public double getWatts(){
    return watts; 
  }

  @Override 
  public String toString(){
    return name + " (" + watts + "W)";
  }
}
