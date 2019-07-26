/**
 * Created by Peilin on 7/12/19.
 */

/*
* structF:
* It is a representation of F
* F is a boolean representation.
* getResult returns true if the requirement is satisfied.
*
* Type:
*   Type 1 means the structF is comparing a table with a str
*   Type 2 means the structF is comparing a table with a integer
*   Type 3 means the structF is comparing a table with anaother table
*
 */
public class structF {
    //Type 1:

    public String r1; //Name of the table
    public String i1; //Column Name of the table
    public Object LookUp; //String interested

    //Type 2:
    public int intLookUp; //Int interested

    //Type 3:
    public String r2; //Name of the table
    public String i2; //Column Name of the table

    public int type;

    structF(String r1, String col, String str){
        this.type = 1;
        this.r1 = r1;
        this.i1 = col;
        this.LookUp = str;
    }

    structF(String r1, String col, int num){
        this.type = 2;
        this.r1 = r1;
        this.i1 = col;
        this.LookUp = num;
    }

    structF(String r1, String index1, String r2, String index2){
        this.type = 3;
        this.r1 = r1;
        this.i1 = index1;
        this.r2 = r2;
        this.i2 = index2;
    }

    void updateRelation(String r1){
        this.r1 = r1;
    }

    void updateRelation(String r1, String r2){
        this.r1 = r1;
        this.r2 = r2;
    }
}
