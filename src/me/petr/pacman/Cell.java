package me.petr.pacman;

public class Cell {
   private CellType cellType;

   public Cell(CellType cellType) {
       this.cellType = cellType;
   }

    public CellType getCellType() {
        return cellType;
    }
}
