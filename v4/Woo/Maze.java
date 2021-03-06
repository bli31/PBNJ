// (P)BNJ – Brian Li, Nakib Abedin, Jefford Shau
// APCS pd07
// FP – Dungeon Crawler
// 2022-05-28

public class Maze{
    /*
    Contains Maze
    Contains links to adjacent Mazes
    */

    private String[][] _maze;

    // private Maze northMaze;
    // private Maze eastMaze;
    // private Maze southMaze;
    // private Maze westMaze;



     public Maze(String[][] arr){
         this._maze = arr;
     }

     public String[][] getMaze(){
       return this._maze;
     }

    // public void setNorth(Maze north){
    //     this.northMaze = north;
    // }

    // public void setEast(Maze east){
    //     this.eastMaze = east;
    // }

    // public void setSouth(Maze south){
    //     this.southMaze = south;
    // }

    // public void setWest(Maze west){
    //     this.westMaze = west;
    // }

    // public Maze getNorth(){
    //     return northMaze;
    // }
    // public Maze getEast(){
    //     return eastMaze;
    // }
    // public Maze getSouth(){
    //     return southMaze;
    // }
    // public Maze getWest(){
    //     return westMaze;
    // }

    public void setPos(int x, int y, String cargo){
        _maze[x][y] = cargo;
    }

    public String getPos(int x, int y){
        return _maze[x][y];
    }


    public String toString(){
        // Stringifies the maze
        String retVal = "";
        for(int i = 0; i < _maze.length; i++){
            for(int e = 0; e < _maze[0].length; e++){
                if(_maze[i][e].equals("#"))
                   retVal = retVal +/* "\u001b[42m" + "\u001b[32m" + */  "#" /*+ "\u001b[30m" + "\u001b[40m" */;
               else{
                   retVal += _maze[i][e];
            }
            }
            retVal += "\n";
        }
        return retVal;
    }

}
