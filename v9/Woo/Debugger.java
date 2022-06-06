import java.util.Scanner;

public class Debugger{
  public static void main(String[] args){

    Scanner sc = new Scanner(System.in);
    Map map = new Map();
    System.out.println(map);

    map.processTile();

    while(sc.hasNextLine()){
      String motion = sc.nextLine();
      if(motion.equalsIgnoreCase("W")){
        map.round("W");
        System.out.println(map);
        //System.out.println(map.getR() + " , " + map.getC());
      }else if(motion.equalsIgnoreCase("A")){
        map.round("A");
        System.out.println(map);
        //System.out.println(map.getR() + " , " + map.getC());
      }else if(motion.equalsIgnoreCase("S")){
        map.round("S");
        System.out.println(map);
        //System.out.println(map.getR() + " , " + map.getC());
      }else if(motion.equalsIgnoreCase("D")){
        map.round("D");
        System.out.println(map);
        //System.out.println(map.getR() + " , " + map.getC());
      }else{
        System.out.println(map);
        System.out.println("invalid input");
      }
    }

  }
}
