public class Field {

    String field;

    public Field(String field) {
        setField(field);
    }

    public String setField(String Field) {
        if (field.equals("WATER") || field.equals("SHIP") || field.equals("SHIP_HIT") || field.equals("WATER_HIT")) {
            this.field = field;
        } else {
            System.out.println("Ungüliger Feldzustand erkannt. Bitte erneut eingeben.");
        }
    }

    public String getField() {
        return this.field;
    }
}