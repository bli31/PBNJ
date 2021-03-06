// PBNJ - Brian Li, Nakib Abedin, Jefford Shau
// APCS pd07
// Final Project -- Dungeon Crawler
// 2022-06-10

import java.util.ArrayList;
import java.util.LinkedList;

public class Map{

    private String hero = "X";
    private String enemy = "M";

    public static void clear()
    {
      System.out.println("\033[2J");
      System.out.println("\033[" + 1 + ";" + 1 + "H");
    }

    // maze instance variables
    private Maze maze;
    public Maze currentFrame;
    private int rows;
    private int cols;
    private boolean exitPlaced;

    // RPG instance variables
    public Character mc;
    private Monster monster;
    public ArrayList<Monster> monsters = new ArrayList<Monster>();
    private int monsterCount = 15; // total amount of monsters per floor
    public boolean battlePhase = false;
    private String lastMsg = "N/A";

    // system instance variables
    public int score; // accumulates after each floor

    // default constructor
    public Map(){
        rows = 81;
        cols = 144;
        MazeGenerator troll = new MazeGenerator(rows, cols);
        maze = new Maze(troll.getGeneratedMaze());
        currentFrame = maze;

        // creates the hero in a room
        while (true) {
          int heroR = troll.randNum(1, rows-1);
          int heroC = troll.randNum(1, cols-1);
          if(isRoom(heroR, heroC)) {
            mc = new Hero(150, 10, 1, heroR, heroC, currentFrame);
            break;
          }
        }

        // create and spawns the monsters in a room
        while (monsters.size() != monsterCount) {
          int monsterR = (int) (Math.random() * (rows - 1)) + 1;
          int monsterC = (int) (Math.random() * (cols - 1)) + 1;
          if(isRoom(monsterR, monsterC) && Math.sqrt(Math.pow(monsterC - mc.getC(), 2) + Math.pow(monsterR - mc.getR(), 2)) > 10) { // monster spawns distance of at least 10
            monster = new Monster(100, 10, 1, monsterR, monsterC, currentFrame);
            monsters.add(new Monster(100, 10, 1, monsterR, monsterC, currentFrame));
            currentFrame.setPos(monsterR, monsterC, enemy);
          }
        }

        // spawn 6 healing tiles
        int healTiles = 0;
        while (healTiles != 6){
          int healR = (int) (Math.random() * (rows - 1)) + 1;
          int healC = (int) (Math.random() * (cols - 1)) + 1;
          if (isRoom(healR, healC)){
            currentFrame.setPos(healR, healC, "H");
            healTiles++;
          }
        }

        // spawn 4 treasure chest tile
        int chestCount = 0;
        while (chestCount != 4){
          int treasureR = (int) (Math.random() * (rows - 1)) + 1;
          int treasureC = (int) (Math.random() * (cols - 1)) + 1;
          if (isRoom(treasureR, treasureC)){
            currentFrame.setPos(treasureR, treasureC, "T");
            chestCount++;
          }
        }

        // creates an exit tile
        while (true) {
          int exitR = (int) (Math.random() * (rows - 1)) + 1;
          int exitC = (int) (Math.random() * (cols - 1)) + 1;
          if (isRoom(exitR, exitC)) {
            currentFrame.setPos(exitR, exitC, "E");
            break;
          }
        }

        // place hero
        currentFrame.setPos(mc.getR(), mc.getC(), hero);
    }

    // player movement
    public void playerTurn(String key) {
      if (key == "W") {
        moveUp();
      }
      else if (key == "A") {
        moveLeft();
      }
      else if (key == "S") {
        moveDown();
      }
      else if (key == "D") {
        moveRight();
      }
    }

    // monster movement
    public void monsterTurn() {
      // each monster will play their turn
      for (int i = 0; i < monsters.size(); i++){
        if ((monsters.get(i)).playTurn()){
          // monster initiaties battle
          System.out.println("The monster has initiated a battle with you!");

          battlePhase = true;
          battle(monsters.get(i), mc);
          battlePhase = false;

          System.out.println("You defeated a monster!\n");
          System.out.println("Current score: " + score);
        }
      }
    }

    public Monster findMonster(){
      for (int i = 0; i < monsters.size(); i++){
        if (monsters.get(i).getR() == mc.getR() && monsters.get(i).getC() == mc.getC()){
          return monsters.get(i);
        }
      }
      return null;
    }

    // initiates battle between two characters
    public void battle(Character first, Character second){
      LinkedList<Character> turnOrder = new LinkedList();
      turnOrder.offerFirst(first); turnOrder.offerLast(second);

      // play battle
      System.out.println("A battle has started!");
      while (first.isAlive() && second.isAlive()){
       if (first.chooseMove(second)){ // if true, then hero has fleed
         System.out.println("Your act of cowardice is sad. Your score has been reduced.");
         score -= 200;
         currentFrame.setPos(mc.getR(), mc.getC(), "X");
         monsters.remove(second);
         return;
       }
       if (second.isAlive()){
         if (second.chooseMove(first)){ // if true, then hero has fleed
           System.out.println("Your act of cowardice is sad. Your score has been reduced.");
           score -= 200;
           currentFrame.setPos(mc.getR(), mc.getC(), "X");
           monsters.remove(first);
           return;
         }
       }
      }

      // check if character is dead
      if (!(mc.isAlive())){
        System.out.println("Sorry, you have died.\n");
        System.out.println("Final Score: " + score);
        System.exit(0);
      }

      // check which character is dead
      if (!(turnOrder.getFirst().isAlive())){
        if (turnOrder.getFirst() instanceof Monster){
          // you kill the monster
          monsters.remove(turnOrder.getFirst());
          return;
        }
        else {
          // you died
          dead();
          return;
        }
      }
      else{ // whoever was 2nd has died
        if (turnOrder.getLast() instanceof Monster){
          // you killed the monster
          monsters.remove(turnOrder.getLast());
          return;
        }
        else {
          // you died
          dead();
          return;
        }
      }
    }

    // play a "round," you make your move, then monsters make their moves
    public void round(String key) {
      if (battlePhase == false) {
        playerTurn(key); // player movement
        processTile(); // checks player for special tile
        if (mc.isAlive()) {
          monsterTurn(); // monster turn
        }
        // player died
        else if (!mc.isAlive()) {
          dead();
        }
      }
    }

    // process the tile the character is currently standing on
    public void processTile(){
      if (mc.lastTile().equals("E")){ // escape the floor, go to next floor
        nextFloor();
      }

      if (mc.lastTile().equals("T")){ // give random weapon
        Weapon temp = new Weapon();
        mc.addWeapon(temp);
        mc.lastTileToSpace();
        lastMsg = temp.getName();
      }

      if (mc.lastTile().equals("H")){ // restore health to max
        mc.addHealth(150 - mc.getHealth());
      }

      if (mc.lastTile().equals("M")){
        // itertate thru, find the monster, initiate the battle
        for (int i = 0; i < monsters.size(); i++){
          if (monsters.get(i).getR() == mc.getR() && monsters.get(i).getC() == mc.getC()){
            System.out.println("You initiated a battle with a monster!");

            battlePhase = true;
            battle(mc, monsters.get(i));
            battlePhase = false;

            mc.lastTileToSpace();
            score += 50;
            System.out.println("You defeated a monster!");
            System.out.println("Current score: " + score);
            return;
          }
        }
      }

    }

    // moves on to next floor
    public void nextFloor() {
      score += 100;
      System.out.println("You cleared the floor!");
      System.out.println("Current score: " + score);
      System.out.println("Generating next stage ...");
      System.out.println("...");

      // generates new monster floor
      MazeGenerator temp = new MazeGenerator(rows, cols);
      maze = new Maze(temp.getGeneratedMaze());
      currentFrame = maze;
      monsters.clear();
      exitPlaced = false;

      // creates the hero in a room
      while (true) {
        int heroR = temp.randNum(1, rows-1);
        int heroC = temp.randNum(1, cols-1);
        if(isRoom(heroR, heroC)) {
          mc = new Hero(mc.getHealth(), mc.getAtk(), mc.getSpeed(), heroR, heroC, mc.getInventory(), mc.getLastWeapon(), currentFrame);
          break;
        }
      }

      // creates the monsters in a room
      while (monsters.size() != monsterCount) {
        int monsterR = (int) (Math.random() * (rows - 1)) + 1;
        int monsterC = (int) (Math.random() * (cols - 1)) + 1;
        if(isRoom(monsterR, monsterC) && Math.sqrt(Math.pow(monsterC - mc.getC(), 2) + Math.pow(monsterR - mc.getR(), 2)) > 10) { // monster spawns distance of at least 10
          monster = new Monster(100, 10, 1, monsterR, monsterC, currentFrame);
          monsters.add(monster);
          currentFrame.setPos(monsterR, monsterC, enemy);
        }
      }

      // spawn 6 healing tiles
      int healTiles = 0;
      while (healTiles != 6){
        int healR = (int) (Math.random() * (rows - 1)) + 1;
        int healC = (int) (Math.random() * (cols - 1)) + 1;
        if (isRoom(healR, healC)){
          currentFrame.setPos(healR, healC, "H");
          healTiles++;
        }
      }

      // spawn 4 treasure chest
      int chestTiles = 0;
      while (chestTiles != 4){
        int treasureR = (int) (Math.random() * (rows - 1)) + 1;
        int treasureC = (int) (Math.random() * (cols - 1)) + 1;
        if (isRoom(treasureR, treasureC)){
          currentFrame.setPos(treasureR, treasureC, "T");
          chestTiles++;
        }
      }

      // creates an exit tile
      while (!exitPlaced) {
        int exitR = (int) (Math.random() * (rows - 1)) + 1;
        int exitC = (int) (Math.random() * (cols - 1)) + 1;
        if (isRoom(exitR, exitC)) {
          currentFrame.setPos(exitR, exitC, "E");
          exitPlaced = true;
        }
      }

      // place hero
      currentFrame.setPos(mc.getR(), mc.getC(), hero);
    }

    // execute if dead
    public void dead() {
      System.out.println("You have died. Better luck next time.");
      System.out.println("Final Score: " + score);
    }

    public String toString() {
         String[][] displayZone = displayZone();
         String output = "";
         for(String[] row : displayZone){
           for(String col : row){
             if(col.equals("#") || col.equals("@"))
              output += "\u001B[37m"+"\u001B[47m" +"#" + "\u001B[0m";
             else if(col.equals("M"))
              output += "\u001B[31m"+ col + "\u001B[0m";
             else if(col.equals("H"))
               output += "\u001B[32m"+ col + "\u001B[0m";
             else if(col.equals("E"))
              output += "\u001B[35m"+ col + "\u001B[0m";
             else if(col.equals("X"))
               output += "\u001B[36m"+ col + "\u001B[0m";
            else if(col.equals("T"))
              output += "\u001B[33m"+ col + "\u001B[0m";
             else
               output += col;
           }
           output += "\n";
         }
         return output;
     }

    // player movement
    public boolean moveUp() {
      if( (!(currentFrame.getPos(mc.getR()-1, mc.getC()).equals("#"))) && (!(currentFrame.getPos(mc.getR()-1, mc.getC()).equals("@"))) ){
        currentFrame.setPos(mc.getR(), mc.getC(), mc.lastTile());
        mc.moveUp();
        processTile();
        currentFrame.setPos(mc.getR(), mc.getC(), hero);
        return true;
      } else {
        return false;
      }
    }
    public boolean moveRight() {
        if( (!(currentFrame.getPos(mc.getR(), mc.getC()+1).equals("#"))) && (!(currentFrame.getPos(mc.getR(), mc.getC()+1).equals("@"))) ){
            currentFrame.setPos(mc.getR(), mc.getC(), mc.lastTile());
            mc.moveRight();
            processTile();
            currentFrame.setPos(mc.getR(), mc.getC(), hero);
            return true;
        } else {
          return false;
        }
    }
    public boolean moveDown() {
        if( (!(currentFrame.getPos(mc.getR()+1, mc.getC()).equals("#"))) && (!(currentFrame.getPos(mc.getR()+1, mc.getC()).equals("@"))) ){
            currentFrame.setPos(mc.getR(), mc.getC(), mc.lastTile());
            mc.moveDown();
            processTile();
            currentFrame.setPos(mc.getR(), mc.getC(), hero);
            return true;
        } else {
          return false;
        }
    }
    public boolean moveLeft() {
        if( (!(currentFrame.getPos(mc.getR(), mc.getC()-1).equals("#"))) && (!(currentFrame.getPos(mc.getR(), mc.getC()-1).equals("@"))) ){
            currentFrame.setPos(mc.getR(), mc.getC(), mc.lastTile());
            mc.moveLeft();
            processTile();
            currentFrame.setPos(mc.getR(), mc.getC(), hero);
            return true;
        } else {
          return false;
        }
    }

    // accessor for text
    public String getLastMsg(){
      return lastMsg;
    }

    // accessor for Score
    public int getScore(){
      return score;
    }

    // accessor for HP
    public int getHP(){
      return mc.getHealth();
    }

    // accessor method for maze
    public String[][] getMaze() {
      return maze.getMaze();
    }

    // only shows the region surrounding the Hero
    public String[][] displayZone() {
        int topLeftR = mc.getR() - 20;
        int topLeftC = mc.getC() - 40;

        int currR = 0;
        int currC = 0;

        topLeftR = Math.max(0, topLeftR);
        topLeftC = Math.max(0, topLeftC);

        topLeftR = Math.min(topLeftR, rows-41);
        topLeftC = Math.min(topLeftC, cols-81);

        String[][] output = new String[41][81];
        for(int i = topLeftR; i < topLeftR + 41; i++){
            for(int e = topLeftC; e < topLeftC + 81; e++){
                output[currR][currC] = currentFrame.getPos(i,e);
                //System.out.print(output[currX][currY]);
                currC++;
            }
            //System.out.println("");
            currC = 0;
            currR++;
        }
      return output;
    }

    // returns true if coordinate is in a room
    public boolean isRoom(int r, int c) {
        return currentFrame.getMaze()[r-1][c-1].equals(" ") &&
               currentFrame.getMaze()[r][c-1].equals(" ") &&
               currentFrame.getMaze()[r+1][c-1].equals(" ") &&
               currentFrame.getMaze()[r-1][c].equals(" ") &&
               currentFrame.getMaze()[r][c].equals(" ") &&
               currentFrame.getMaze()[r+1][c].equals(" ") &&
               currentFrame.getMaze()[r-1][c+1].equals(" ") &&
               currentFrame.getMaze()[r][c+1].equals(" ") &&
               currentFrame.getMaze()[r+1][c+1].equals(" ");

    }

}
