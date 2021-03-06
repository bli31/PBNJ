// PBNJ - Brian Li, Nakib Abedin, Jefford Shau
// APCS pd07
// Final Project -- Dungeon Crawler
// 2022-06-10

public class Maze{
    /*
        Contains Maze Class
    */

    private String[][] _maze;

    public Maze(String[][] arr){
      this._maze = arr;
    }

    public String[][] getMaze(){
      return this._maze;
    }

    // sets a tile to cargo
    public void setPos(int r, int c, String cargo){
        _maze[r][c] = cargo;
    }

    // gets the cargo associated with the tile
    public String getPos(int r, int c){
        return _maze[r][c];
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
