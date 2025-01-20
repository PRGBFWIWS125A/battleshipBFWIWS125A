public class BattleShip{
    
    static int SIZE = 10;
    
    static final String ENTER_SHIP_COORDINATE_PROMPT = "Geben Sie die %skoordinaten für ein Schiff der Länge %d ein: ";

    static int distance(final Coordinate start, final Coordinate end){
        return Math.abs(start.column() - end.column()) + Math.abs(start.row() - end.row());
    }
    
    static Coordinate getRandomCoordinate() {
        return new Coordinate(Utility.getRandomInt(SIZE),Utility.getRandomInt(SIZE));
    }
    
    static int getMaxSurroundingColumn(final Coordinate start, final Coordinate end){
        return Math.min((Math.max(start.column(),end.column())+1),SIZE - 1);
    }

    static int getMaxSurroundingRow(final Coordinate start, final Coordinate end){
        return Math.min((Math.max(start.row(),end.row())+1),SIZE - 1);
    }

    static int getMinSurroundingColumn(final Coordinate start, final Coordinate end){
        return Math.max((Math.min(start.column(),end.column())-1),0);
    }

    static int getMinSurroundingRow(final Coordinate start, final Coordinate end){
        return Math.max((Math.min(start.row(),end.row())-1),0);
    }

    static String getStartCoordinatePrompt(final int length) {
        return String.format(BattleShip.ENTER_SHIP_COORDINATE_PROMPT, "Start", length);
    }

    static String getEndCoordinatePrompt(final int length) {
        return String.format(BattleShip.ENTER_SHIP_COORDINATE_PROMPT, "End", length);
    }

    static boolean isValidCoordinate(final String input) {
        return input.matches("[A-Ja-j]([1-9]|10)");
    }

    static boolean onOneLine(final Coordinate start, final Coordinate end) {
        return start.column() == end.column() || start.row() == end.row();
    }

    static void showRowNumber(final int row) {
        if (row < 9) {
            System.out.print(" ");
        }
        System.out.print(String.valueOf(row + 1));
    }

    static void showSeparatorLine() {
        System.out.println("   +-+-+-+-+-+-+-+-+-+-+      +-+-+-+-+-+-+-+-+-+-+");
    }

    static Coordinate toCoordinate(final String input) {
        return new Coordinate(input.toUpperCase().charAt(0) - 65, Integer.parseInt(input.substring(1)) - 1);
    }

    static void showField(final Field field, final boolean showShips) {
        switch (field) {
        case SHIP:
            System.out.print(showShips ? "O" : " ");
            break;
        case SHIP_HIT:
            System.out.print("*");
            break;
        case WATER_HIT:
            System.out.print("X");
            break;
        case WATER:
        default:
            System.out.print(" ");
        }
    }

    static void placeShip(final Coordinate start, final Coordinate end, final Field[][] field) {
        if (start.column() == end.column()) {
            for (int row = Math.min(start.row(), end.row()); row <= Math.max(start.row(), end.row()); row++) {
                field[start.column()][row] = Field.SHIP;
            }
        } else {
            for (
                int column = Math.min(start.column(), end.column());
                column <= Math.max(start.column(), end.column());
                column++
            ) {
                field[column][start.row()] = Field.SHIP;
            }
        }
    }

}