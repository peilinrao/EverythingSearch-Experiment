/**
 * Created by Peilin on 7/8/19.
 */

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;


// The comment within {} are the psuedo code coresponding to my code
/*
* class SPJEXECUTION
* private variables:
* X: a list of the names of the column that we are interested in.
* F: a list of conditions that are required to filter the role we want
*   F example: k1.value = 'a', k2.value = 'b', k1.dId = k2.dId
* T: a list of relation, each relation's struct is represented in relation.java
 */


public class SPJEXECUTION {

    private ArrayList<String> X;
    private ArrayList<structF> F;
    private ArrayList<relation> T;
    private ArrayList<Object> result;


    SPJEXECUTION(ArrayList<String> X, ArrayList<structF> F, ArrayList<relation> T){
        this.X = X;
        this.F = F;
        this.T = T;
        this.result = new ArrayList<>();
    }

    ArrayList<Object> getResult(){
        if(!this.F.isEmpty()){
            System.out.println("Type of first F is: "+this.F.get(0).type);

            // Type 1 means searching for a String, type2 means searching for a Int
            if(this.F.get(0).type==1 || this.F.get(0).type == 2){

                int index;
                for(index = 0; index < this.T.size(); index++){
                    if(this.T.get(index).relationName.equals(this.F.get(0).r1) ){
                        break;
                    }
                }

                if(index != -1){
                    relation temp = this.T.get(index);

                    temp.jump(this.F.get(0).LookUp);
                    if(this.X.contains(temp.columnNames.get(0))){
                        Object val = -1;
                        for(int j = 0; j < temp.distinctValuesOfFirstColumn().size(); j++){
                            if(temp.distinctValuesOfFirstColumn().get(j)==this.F.get(0).LookUp){
                                val = temp.distinctValuesOfFirstColumn().get(j);
                                break;
                            }
                        }
                        System.out.println("Adding result:"+val);
                        this.result.add(val);
                    }

                    this.F.remove(this.F.get(0));
                    this.X.remove(temp.columnNames.get(0));

                    int count;
                    for(count = 0; count < this.T.size(); count++){
                        if(this.T.get(count).relationName.equals(temp.relationName)){
                            this.T.get(count).subRelation();
                        }
                    }

                    SPJEXECUTION SPJ_d = new SPJEXECUTION(this.X, this.F, this.T);
                    ArrayList<Object> subresult = SPJ_d.getResult();
                    if (subresult!= null){
                        this.result.addAll(subresult);
                    }

                    return this.result;

                }else{
                    return null;
                }
            }else if(this.F.get(0).type==3) {
                int index1;
                for (index1 = 0; index1 < this.T.size(); index1++) {
                    if (this.T.get(index1).relationName.split(" ")[0].equals(this.F.get(0).r1)) {
                        break;

                    }
                }

                int index2;
                for (index2 = 0; index2 < this.T.size(); index2++) {
                    if (this.T.get(index2).relationName.split(" ")[0].equals(this.F.get(0).r2)) {
                        break;
                    }
                }


                if (index1 != -1 && index2 != -1) {
                    relation temp1 = this.T.get(index1);

                    relation temp2 = this.T.get(index2);

                    System.out.println("Starting selection between: "+temp1.relationName+" for column "+this.F.get(0).i1 +" and "+temp2.relationName+" for column "+this.F.get(0).i2);


                    int val = Integer.parseInt((String)temp1.distinctValuesOfFirstColumn().get(0));
                    System.out.println(val);
                    while(!temp2.columnNames.get(0).equals(this.F.get(0).i2)){
                        System.out.println("Subrelate temp2");
                        temp2.subRelation();
                    }
                    temp2.jump(val);

                    System.out.println("TEST: "+temp2.distinctValuesOfFirstColumn());

                    if (this.X.contains(temp1.columnNames.get(0))) {
                        System.out.println("CP1!Adding to result...");
                        System.out.println(temp1.distinctValuesOfFirstColumn());
                        this.result.add(temp1.distinctValuesOfFirstColumn().get(0));
                    }
                    if (this.X.contains(temp2.columnNames.get(0))) {
                        System.out.println("CP2!Adding to result...");
                        System.out.println(temp2.distinctValuesOfFirstColumn());
                        this.result.add(temp2.distinctValuesOfFirstColumn().get(0));
                    }

                    int count;
                    for (count = 0; count < this.T.size(); count++) {
                        if (this.T.get(count).relationName.split(" ")[0].equals(temp1.relationName.split(" ")[0])) {
                            this.T.get(count).subRelation();
                            System.out.println("Creating subrelation for table: "+this.T.get(count).relationName.split(" ")[0]);
                        } else if (this.T.get(count).relationName.split(" ")[0].equals(temp2.relationName.split(" ")[0])) {
                            this.T.get(count).subRelation();
                            System.out.println("Creating subrelation for table: "+this.T.get(count).relationName.split(" ")[0]);
                        }
                    }

                    this.F.remove(this.F.get(0));
                    SPJEXECUTION SPJ_dd = new SPJEXECUTION(this.X, this.F, this.T);
                    this.result = SPJ_dd.getResult();

//                    relation Til;
//                    relation Tip;
//                    if ((int) temp1.currentPointerCountAndValue().get(1) < (int) temp2.currentPointerCountAndValue().get(1)) {
//                        Til = temp1;
//                        Tip = temp2;
//                    } else {
//                        Til = temp2;
//                        Tip = temp1;
//                    }
//                    Til.jump(Tip.currentPointerCountAndValue());
                } else {
                    return null;
                }
            }

        }else{
            System.out.println("There is no F");
            System.out.println("X is:"+this.X);
            if(this.T.size()==0 || this.X.size() == 0){
                System.out.println("There is no T or no X");
                return null;
            }

            relation temp3 = this.T.get(1);
            System.out.println("Relation temp3 is: "+temp3.relationName);
            if(this.X.size() != 0){
                if(this.X.contains(temp3.columnNames.get(0))){
                    System.out.println("Adding result: " + temp3.distinctValuesOfFirstColumn().get(0));
                    this.result.add(temp3.distinctValuesOfFirstColumn().get(0));
                }

                this.X.remove(temp3.columnNames.get(0));
                if(this.T.size()==0 || this.X.size() == 0){
                    System.out.println("There is no X");
                    return this.result;
                }
                int count;
                for(count = 0; count < this.T.size(); count++){
                    if(this.T.get(count).relationName.equals(temp3.relationName)){
                        this.T.get(count).subRelation();
                    }
                }

                SPJEXECUTION SPJ_d = new SPJEXECUTION(this.X, this.F, this.T);
                ArrayList<Object> subresult = SPJ_d.getResult();
                if (subresult!= null){
                    this.result.addAll(subresult);
                }
                return this.result;
            }else{
                return null;
            }
        }

        return this.result;
    }

    public static void test1(){
        //Test 1: search for one row
        ArrayList<String> X = new ArrayList<String>();
        X.add("kid");
        X.add("did");
        X.add("tf");
        X.add("blem");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF("KD", "kid", 1);
        F.add(F1);

        ArrayList<relation> T = new ArrayList<relation>(1);
        relation kdsubrel = new relation();
        kdsubrel.initialiseRelationFilePath("src/map.bin", "src/blocks.bin");
        kdsubrel.processMapHeader();
        T.add(kdsubrel);

        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());
    }

    public static void test2(){
        //Test 1: search for one row
        ArrayList<String> X = new ArrayList<String>();
        X.add("kid");
        X.add("len");
        X.add("elen");
        X.add("url");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF("KD", "value", "D", "did");
        structF F2 = new structF("KD", "kid", 0);
        F.add(F2);
        F.add(F1);

        ArrayList<relation> T = new ArrayList<relation>(1);
        relation table_D = new relation();
        table_D.initialiseRelationFilePath("src/map_d.bin", "src/blocks_d.bin");
        table_D.processMapHeader();

        relation table_KD = new relation();
        table_KD.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD.processMapHeader();

        T.add(table_KD);
        T.add(table_D);

        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());
    }

    public static void test3() {
        ArrayList<relation> T = new ArrayList<relation>(1);
        relation table_D = new relation();
        table_D.initialiseRelationFilePath("src/map_d.bin", "src/blocks_d.bin");
        table_D.processMapHeader();
        System.out.println("TEST: "+ table_D.distinctValuesOfFirstColumn());
        System.out.println(table_D.currentPointerCountAndValue());
        table_D.next();
        System.out.println(table_D.currentPointerCountAndValue());
        table_D.subRelation();
        System.out.println(table_D.distinctValuesOfFirstColumn());

        relation table_KD = new relation();
        table_KD.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD.processMapHeader();
        System.out.println("TEST: "+ table_KD.distinctValuesOfFirstColumn());
        System.out.println(table_KD.currentPointerCountAndValue());
        table_KD.next();
        System.out.println(table_KD.currentPointerCountAndValue());
        table_KD.subRelation();
        System.out.println(table_KD.distinctValuesOfFirstColumn());
    }

    public static void main(String[] arg){

        test2();


    }

}




