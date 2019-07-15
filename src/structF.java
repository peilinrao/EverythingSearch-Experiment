/**
 * Created by Peilin on 7/12/19.
 */

/*
* structF:
* It is a representation of F
* F is a boolean representation.
* getResult returns true if the requirement is satisfied.
* for now, structF takes up to four
*
 */
public class structF {
    //Type 1: compare between a relation with a str
    public relation r1;
    public int i1;
    public String strLookUp;
    //Type 2: compare between a relation with a int
    public int intLookUp;
    //Type 3: compare between two relation
    public relation r2;
    public int i2;
    //Which type is constructed
    public int type;

    structF(relation r1, int index, String str){
        this.type = 1;
        this.r1 = r1;
        this.i1 = index;
        this.strLookUp = str;
    }

    structF(relation r1, int index, int num){
        this.type = 2;
        this.r1 = r1;
        this.i1 = index;
        this.intLookUp = num;
    }

    structF(relation r1, int index1, relation r2, int index2){
        this.type = 3;
        this.r1 = r1;
        this.i1 = index1;
        this.r2 = r2;
        this.i2 = index2;

    }

    void updateRelation(relation r1){
        this.r1 = r1;
    }

    void updateRelation(relation r1, relation r2){
        this.r1 = r1;
        this.r2 = r2;
    }
}
