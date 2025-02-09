import java.io.*;

public class BattleShip {

    static final int ALL_HIT = 14;

    static final String ENTER_SHIP_COORDINATE_PROMPT = "Geben Sie die %skoordinaten für ein Schiff der Länge %d ein: ";

    static final int SIZE = 10;

    public static void main(final String[] args) {
        System.out.println();
        final Field[][] otherField = BattleShip.initOtherField();
        final Field[][] ownField = BattleShip.initOwnField(otherField);
        while (!BattleShip.endCondition(ownField, otherField)) {
            BattleShip.turn(ownField, otherField);
        }
        BattleShip.outputWinner(ownField, otherField);
    }

    static boolean allHit(final Field[][] field) {
        return BattleShip.countHits(field) == BattleShip.ALL_HIT;
    }

    static int countHits(final Field[][] field) {
        int result = 0;
        for (int column = 0; column < SIZE; column++) {
            for (int row = 0; row < SIZE; row++) {
                if (field[column][row] == Field.SHIP_HIT) {
                    result++;
                }
            }
        }
        return result;
    }

    static int distance(final Coordinate start, final Coordinate end) {
        return Math.abs(start.column() - end.column()) + Math.abs(start.row() - end.row());
    }

    static boolean endCondition(final Field[][] ownField, final Field[][] otherField) {
        return BattleShip.allHit(ownField) || BattleShip.allHit(otherField);
    }

    static void fillWaterHits(final Coordinate shot, final Field[][] field) {
        int columnMin = shot.column();
        while (columnMin > 0 && field[columnMin][shot.row()] == Field.SHIP_HIT) {
            columnMin--;
        }
        int columnMax = shot.column();
        while (columnMax < BattleShip.SIZE - 1 && field[columnMax][shot.row()] == Field.SHIP_HIT) {
            columnMax++;
        }
        int rowMin = shot.row();
        while (rowMin > 0 && field[shot.column()][rowMin] == Field.SHIP_HIT) {
            rowMin--;
        }
        int rowMax = shot.row();
        while (rowMax < BattleShip.SIZE - 1 && field[shot.column()][rowMax] == Field.SHIP_HIT) {
            rowMax++;
        }
        for (int column = columnMin; column <= columnMax; column++) {
            for (int row = rowMin; row <= rowMax; row++) {
                BattleShip.shot(new Coordinate(column, row), field);
            }
        }
    }

    static String getEndCoordinatePrompt(final int length) {
        return String.format(BattleShip.ENTER_SHIP_COORDINATE_PROMPT, "End", length);
    }

    static int getMaxSurroundingColumn(final Coordinate start, final Coordinate end) {
        return Math.min(BattleShip.SIZE - 1, Math.max(start.column(), end.column()) + 1);
    }

    static int getMaxSurroundingRow(final Coordinate start, final Coordinate end) {
        return Math.min(BattleShip.SIZE - 1, Math.max(start.row(), end.row()) + 1);
    }

    static int getMinSurroundingColumn(final Coordinate start, final Coordinate end) {
        return Math.max(0, Math.min(start.column(), end.column()) - 1);
    }

    static int getMinSurroundingRow(final Coordinate start, final Coordinate end) {
        return Math.max(0, Math.min(start.row(), end.row()) - 1);
    }

    static Coordinate getRandomCoordinate() {
        return new Coordinate(Utility.getRandomInt(BattleShip.SIZE), Utility.getRandomInt(BattleShip.SIZE));
    }

    static Coordinate getRandomEndCoordinate(final Coordinate start, final int distance) {
        int choices = 0;
        if (start.column() >= distance) {
            choices++;
        }
        if (start.column() < BattleShip.SIZE - distance) {
            choices++;
        }
        if (start.row() >= distance) {
            choices++;
        }
        if (start.row() < BattleShip.SIZE - distance) {
            choices++;
        }
        int skip = Utility.getRandomInt(choices);
        if (start.column() >= distance) {
            skip--;
            if (skip < 0) {
                return new Coordinate(start.column() - distance, start.row());
            }
        }
        if (start.column() < BattleShip.SIZE - distance) {
            skip--;
            if (skip < 0) {
                return new Coordinate(start.column() + distance, start.row());
            }
        }
        if (start.row() >= distance) {
            skip--;
            if (skip < 0) {
                return new Coordinate(start.column(), start.row() - distance);
            }
        }
        return new Coordinate(start.column(), start.row() + distance);
    }

    static Coordinate getRandomUnshotCoordinate(final Field[][] field) {
        int choices = 0;
        for (int column = 0; column < BattleShip.SIZE; column++) {
            for (int row = 0; row < BattleShip.SIZE; row++) {
                if (field[column][row] == Field.WATER || field[column][row] == Field.SHIP) {
                    choices++;
                }
            }
        }
        int choice = Utility.getRandomInt(choices);
        for (int column = 0; column < BattleShip.SIZE; column++) {
            for (int row = 0; row < BattleShip.SIZE; row++) {
                if (field[column][row] == Field.WATER || field[column][row] == Field.SHIP) {
                    choice--;
                    if (choice <= 0) {
                        return new Coordinate(column, row);
                    }
                }
            }
        }
        throw new IllegalStateException("No unshot field found and not won!");
    }

    static String getStartCoordinatePrompt(final int length) {
        return String.format(BattleShip.ENTER_SHIP_COORDINATE_PROMPT, "Start", length);
    }

    static Field[][] initOtherField() {
        final Field[][] otherField = new Field[BattleShip.SIZE][BattleShip.SIZE];
        BattleShip.setAllWater(otherField);
        for (int length = 5; length > 1; length--) {
            final Coordinate start = BattleShip.getRandomCoordinate();
            final Coordinate end = BattleShip.getRandomEndCoordinate(start, length - 1);
            if (BattleShip.validPosition(start, end, length, otherField)) {
                BattleShip.placeShip(start, end, otherField);
            } else {
                length++;
            }
        }
        return otherField;
    }

    static Field[][] initOwnField(final Field[][] otherField) {
        final Field[][] ownField = new Field[BattleShip.SIZE][BattleShip.SIZE];
        BattleShip.setAllWater(ownField);
        for (int length = 5; length > 1; length--) {
            BattleShip.showFields(ownField, otherField);
            final Coordinate start = BattleShip.readStartCoordinate(length);
            final Coordinate end = BattleShip.readEndCoordinate(length);
            if (BattleShip.validPosition(start, end, length, ownField)) {
                BattleShip.placeShip(start, end, ownField);
            } else {
                length++;
            }
        }
        return ownField;
    }

    static boolean isValidCoordinate(final String input) {
        return input.matches("[A-Ja-j]([1-9]|10)");
    }

    static boolean noConflict(final Coordinate start, final Coordinate end, final Field[][] field) {
        for (
            int column = BattleShip.getMinSurroundingColumn(start, end);
            column <= BattleShip.getMaxSurroundingColumn(start, end);
            column++
        ) {
            for (
                int row = BattleShip.getMinSurroundingRow(start, end);
                row <= BattleShip.getMaxSurroundingRow(start, end);
                row++
            ) {
                if (field[column][row] != Field.WATER) {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean onOneLine(final Coordinate start, final Coordinate end) {
        return start.column() == end.column() || start.row() == end.row();
    }

    static void outputWinner(final Field[][] ownField, final Field[][] otherField) {
        BattleShip.showFields(ownField, otherField);
        if (BattleShip.allHit(otherField)) {
            System.out.println("Du hast gewonnen!");
        } else {
            System.out.println("Der Computer hat gewonnen!");
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

    static Coordinate readCoordinate(final String prompt) {
        String input = "";
        while (!BattleShip.isValidCoordinate(input)) {
            System.out.print(prompt);
            try {
                input = Utility.readStringFromConsole();
            } catch (final IOException e) {
                input = "";
            }
            if ("exit".equals(input)) {
                System.exit(0);
            }
        }
        return BattleShip.toCoordinate(input);
    }

    static Coordinate readEndCoordinate(final int length) {
        return BattleShip.readCoordinate(BattleShip.getEndCoordinatePrompt(length));
    }

    static Coordinate readStartCoordinate(final int length) {
        return BattleShip.readCoordinate(BattleShip.getStartCoordinatePrompt(length));
    }

    static void setAllWater(final Field[][] field) {
        for (int col = 0; col < SIZE; col++) {
            for (int row = 0; row < SIZE; row++) {
                field[col][row] = Field.WATER;
            }
        }
    }

    static boolean shipSunk(final Coordinate shot, final Field[][] field) {
        int column = shot.column();
        while (column < BattleShip.SIZE - 1 && field[column][shot.row()] == Field.SHIP_HIT) {
            column++;
        }
        if (field[column][shot.row()] == Field.SHIP) {
            return false;
        }
        column = shot.column();
        while (column > 0 && field[column][shot.row()] == Field.SHIP_HIT) {
            column--;
        }
        if (field[column][shot.row()] == Field.SHIP) {
            return false;
        }
        int row = shot.row();
        while (row < BattleShip.SIZE -1 && field[shot.column()][row] == Field.SHIP_HIT) {
            row++;
        }
        if (field[shot.column()][row] == Field.SHIP) {
            return false;
        }
        row = shot.row();
        while (row > 0 && field[shot.column()][row] == Field.SHIP_HIT) {
            row--;
        }
        if (field[shot.column()][row] == Field.SHIP) {
            return false;
        }
        return true;
    }

    static void shot(final Coordinate shot, final Field[][] field) {
        switch (field[shot.column()][shot.row()]) {
        case SHIP:
            field[shot.column()][shot.row()] = Field.SHIP_HIT;
            if (BattleShip.shipSunk(shot, field)) {
                BattleShip.fillWaterHits(shot, field);
            }
            break;
        case WATER:
            field[shot.column()][shot.row()] = Field.WATER_HIT;
            break;
        default:
            // do nothing
        }
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

    static void showFields(final Field[][] ownField, final Field[][] otherField) {
        System.out.println("    A B C D E F G H I J        A B C D E F G H I J ");
        BattleShip.showSeparatorLine();
        for (int row = 0; row < BattleShip.SIZE; row++) {
            BattleShip.showRow(row, ownField, otherField);
            BattleShip.showSeparatorLine();
        }
        System.out.println();
    }

    static void showRow(final int row, final Field[][] ownField, final Field[][] otherField) {
        BattleShip.showRowNumber(row);
        System.out.print(" |");
        for (int column = 0; column < BattleShip.SIZE; column++) {
            BattleShip.showField(ownField[column][row], true);
            System.out.print("|");
        }
        System.out.print("   ");
        BattleShip.showRowNumber(row);
        System.out.print(" |");
        for (int column = 0; column < BattleShip.SIZE; column++) {
            BattleShip.showField(otherField[column][row], false);
            System.out.print("|");
        }
        System.out.println();
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

    static void turn(final Field[][] ownField, final Field[][] otherField) {
        BattleShip.showFields(ownField, otherField);
        BattleShip.shot(
            BattleShip.readCoordinate("Geben Sie die Koordinaten Ihres nächsten Schusses ein: "),
            otherField
        );
        BattleShip.shot(BattleShip.getRandomUnshotCoordinate(ownField), ownField);
    }

    static boolean validPosition(
        final Coordinate start,
        final Coordinate end,
        final int length,
        final Field[][] field
    ) {
        return BattleShip.onOneLine(start, end)
            && BattleShip.distance(start, end) == length - 1
            && BattleShip.noConflict(start, end, field);
    }

}
