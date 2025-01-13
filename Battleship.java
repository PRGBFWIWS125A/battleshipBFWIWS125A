public class Battleship{
    static int distance(final Coordinate start, final Coordinate end){
        return Math.abs(start.column() - end.column()) + Math.abs(start.row() - end.row());
    }
}