Map map = new Map();
PImage[] mapTiles;

int SIZE = 32;

void setup(){
 size(672, 672);
 background(0);
 mapTiles = new PImage[3];
 mapTiles[0] = loadImage("cobblestone.png");
 mapTiles[1] = loadImage("hero.png");
 mapTiles[2] = loadImage("water.png");

}


void draw(){
  int currentX = 0;
  int currentY = 0;
  
  
  for( String[] row : map.displayZone() ){
    
    for( String col : row ){
      //print(col);
      if( col.equals("#") ) image(mapTiles[2],currentX,currentY);
      else if (col.equals("@") ) {image(mapTiles[2],currentX,currentY);}
      else if (col.equals("X")){
        image(mapTiles[0],currentX,currentY);
        image(mapTiles[1],currentX,currentY);
      }
      else{image(mapTiles[0],currentX,currentY);}
      currentX += SIZE;
      
    }
    currentX = 0;
    currentY += SIZE;
    //println("");
  }
  
}

void keyPressed(){
  if(key == 'W' || key == 'w'){
    map.moveUp();
}
  else if(key == 'A' || key == 'a'){
    map.moveLeft();
  }
  else if(key == 'S' || key =='s'){
    map.moveDown();
}
  else if(key == 'D' || key == 'd'){
    map.moveRight();
}
}
