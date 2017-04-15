package MileStone1;

import java.util.ArrayList;

public class PositionalPosting {

    private int docId;
    private ArrayList<Integer> positions;

    @Override
    public String toString() {
        return "PositionalPosting{" + "docId=" + docId + ", positions=" + positions + '}';
    }

    public int getDocId() {
        return docId;
    }

    public PositionalPosting() {
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public void setPositions(ArrayList<Integer> positions) {
        this.positions = positions;
    }

    public PositionalPosting(int docId, ArrayList<Integer> positions) {
        this.docId = docId;
        this.positions = positions;
    }

}
