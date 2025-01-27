import java.io.*;

public class BattleShip {

    static final String ENTER_SHIP_COORDINATE_PROMPT = "Geben Sie die %skoordinaten für ein Schiff der Länge %d ein: ";

    static int SIZE = 10;

    static int countHits(final Field[][] field) {
        int result = 0;
        for (int column = 0; column < BattleShip.SIZE; column++) {
            for (int row = 0; row < BattleShip.SIZE; row++) {
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

    static void fillWaterHits(final Coordinate shot, final Field[][] field) {
        int row = shot.row();
        int col = shot.column();
        while (col < BattleShip.SIZE && field[col][row] == Field.SHIP_HIT) {
            col++;
        }
        if (field[col][row] != Field.SHIP_HIT) {
            col--;
        }
        final int maxcol = col;
        while (col >= 0 && field[col][row] == Field.SHIP_HIT) {
            col--;
        }
        if (field[col][row] != Field.SHIP_HIT) {
            col++;
        }
        final int mincol = col;
        while (row < BattleShip.SIZE && field[col][row] == Field.SHIP_HIT) {
            row++;
        }
        if (field[col][row] != Field.SHIP_HIT) {
            row--;
        }
        final int maxrow = row;
        while (row >= 0 && field[col][row] == Field.SHIP_HIT) {
            row--;
        }
        if (field[col][row] != Field.SHIP_HIT) {
            row++;
        }
        final int minrow = row;
        final Coordinate start = new Coordinate(mincol, minrow);
        final Coordinate end = new Coordinate(maxcol, maxrow);
        for (
            col = BattleShip.getMinSurroundingColumn(start, end);
            col <= BattleShip.getMaxSurroundingColumn(start, end);
            col++
        ) {
            for (
                row = BattleShip.getMinSurroundingRow(start, end);
                row <= BattleShip.getMaxSurroundingRow(start, end);
                row++
            ) {
                BattleShip.shot(new Coordinate(col, row), field);
            }
        }
    }

    static String getEndCoordinatePrompt(final int length) {
        return String.format(BattleShip.ENTER_SHIP_COORDINATE_PROMPT, "End", length);
    }

    static int getMaxSurroundingColumn(final Coordinate start, final Coordinate end) {
        return Math.min((Math.max(start.column(), end.column()) + 1), BattleShip.SIZE - 1);
    }

    static int getMaxSurroundingRow(final Coordinate start, final Coordinate end) {
        return Math.min((Math.max(start.row(), end.row()) + 1), BattleShip.SIZE - 1);
    }

    static int getMinSurroundingColumn(final Coordinate start, final Coordinate end) {
        return Math.max((Math.min(start.column(), end.column()) - 1), 0);
    }

    static int getMinSurroundingRow(final Coordinate start, final Coordinate end) {
        return Math.max((Math.min(start.row(), end.row()) - 1), 0);
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
        int count = 0;
        for (int column = 0; column < BattleShip.SIZE; column++) {
            for (int row = 0; row < BattleShip.SIZE; row++) {
                switch (field[column][row]) {
                case SHIP:
                case WATER:
                    count++;
                }
            }
        }
        if (count == 0) {
            throw new IllegalStateException();
        }
        final Coordinate[] candidates = new Coordinate[count];
        count = 0;
        for (int column = 0; column < BattleShip.SIZE; column++) {
            for (int row = 0; row < BattleShip.SIZE; row++) {
                switch (field[column][row]) {
                case SHIP:
                case WATER:
                    candidates[count] = new Coordinate(column, row);
                    count++;
                }
            }
        }
        return candidates[Utility.getRandomInt(count)];
    }

    static String getStartCoordinatePrompt(final int length) {
        return String.format(BattleShip.ENTER_SHIP_COORDINATE_PROMPT, "Start", length);
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
        while (!input.equals("exit") && !BattleShip.isValidCoordinate(input)) {
            System.out.println(prompt);
            try {
                input = Utility.readStringFromConsole();
            } catch (final IOException e) {
                // Do nothing
            }
        }
        if (input.equals("exit")) {
            System.exit(0);
        }
        return BattleShip.toCoordinate(input);
    }

    static void setAllWater(final Field[][] field) {
        for (int col = 0; col < BattleShip.SIZE; col++) {
            for (int row = 0; row < BattleShip.SIZE; row++) {
                field[col][row] = Field.WATER;
            }
        }
    }

    static boolean shipSunk(final Coordinate shot, final Field[][] field) {
        if (field[shot.column()][shot.row()] == Field.WATER_HIT) {
            return false;
        }
        int row = shot.row();
        int col = shot.column();
        while (col < BattleShip.SIZE && field[col][row] == Field.SHIP_HIT) {
            col++;
        }
        if (field[col][row] == Field.SHIP) {
            return false;
        }
        row = shot.row();
        col = shot.column();
        while (col >= 0 && field[col][row] == Field.SHIP_HIT) {
            col--;
        }
        if (field[col][row] == Field.SHIP) {
            return false;
        }
        row = shot.row();
        col = shot.column();
        while (row < BattleShip.SIZE && field[col][row] == Field.SHIP_HIT) {
            row++;
        }
        if (field[col][row] == Field.SHIP) {
            return false;
        }
        row = shot.row();
        col = shot.column();
        while (row >= 0 && field[col][row] == Field.SHIP_HIT) {
            row--;
        }
        if (field[col][row] == Field.SHIP) {
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

    static void showFields(final Field[][] ownfield, final Field[][] otherfield) {
        System.out.println("    A B C D E F G H I J        A B C D E F G H I J");
        BattleShip.showSeparatorLine();
        for (int row = 0; row < BattleShip.SIZE; row++) {
            BattleShip.showRow(row, ownfield, otherfield);
            BattleShip.showSeparatorLine();
        }
        System.out.println();
    }

    static void showRow(final int row, final Field[][] ownField, final Field[][] otherField) {
        BattleShip.showRowNumber(row);
        System.out.print(" |");
        for (int col = 0; col < BattleShip.SIZE; col++) {
            BattleShip.showField(ownField[col][row], true);
            System.out.print("|");
        }
        System.out.print("   ");
        BattleShip.showRowNumber(row);
        System.out.print(" |");
        for (int col = 0; col < BattleShip.SIZE; col++) {
            BattleShip.showField(otherField[col][row], false);
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

}