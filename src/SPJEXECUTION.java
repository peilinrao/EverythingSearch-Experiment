/**
 * Created by Peilin on 7/8/19.
 */

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;
import java.lang.Math;



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

    Runtime rt = Runtime.getRuntime();
    long total = rt.totalMemory();
    long free = rt.freeMemory();



    SPJEXECUTION(ArrayList<String> X, ArrayList<structF> F, ArrayList<relation> T){
        this.X = X;
        this.F = F;
        this.T = T;
        this.result = new ArrayList<>();
    }

    ArrayList<Object> getResult(){
        System.out.println("Memory used:"+(total-free)/1024.0/1024.0+" MB");
        if(!this.F.isEmpty()){
            System.out.println("Type of first F is: "+this.F.get(0).type);

            // Type 1 means searching for a String
            // Type 2 means searching for a Int
            // Type 3 means searching for the row that selects two columns from two tables

            if(this.F.get(0).type==1 || this.F.get(0).type == 2){
                // Now we are in the situation that we are searching for a String or a Int

                // Step 1: find the table of interest
                int index = this.F.get(0).r1;

                /* Step 2: if we indeed found the table
                 * use jump to go to the location of interest
                 * add append the value of first column we are interested in to the results
                 * remove the structF from F
                 * remove that column name from X
                 * subrelate the table
                 * and start next sub_SPJEXECUTION
                 */
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
                            System.out.println(this.T.get(count).distinctValuesOfFirstColumn());
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

                /*
                 * If the F have type 3, which means the selection between two tables
                 * find the two tables (relations) first
                 */

                int index1 = this.F.get(0).r1;
                int index2 = this.F.get(0).r2;

                /*
                 * If the two relations exists:
                 * find the column in relation 2 that satisfies the requirements of that in relation 1
                 * jump to the val
                 * append to the results
                 * create subrelations for both relations
                 * start next sub-SPJEXECUTION
                 */

                if (index1 != -1 && index2 != -1) {
                    relation temp1 = this.T.get(index1);

                    relation temp2 = this.T.get(index2);

                    System.out.println("Starting selection between: "+temp1.relationName+" for column "+this.F.get(0).i1 +" and "+temp2.relationName+" for column "+this.F.get(0).i2);

                    while(!temp1.columnNames.get(0).equals(this.F.get(0).i1)){
                        System.out.println("Subrelating temp1");
                        temp1.subRelation();
                    }

                    while(!temp2.columnNames.get(0).equals(this.F.get(0).i2)){
                        System.out.println("Subrelating temp2");
                        temp2.subRelation();
                    }

                    ArrayList<Object> temp_list = temp1.distinctValuesOfFirstColumn();
                    for(int i = 0; i < temp_list.size(); i++){
                        Object val = temp_list.get(i);
                        temp2.jump(val);
                        if(temp2.currentPointerCountAndValue() != null){
                            if (temp2.currentPointerCountAndValue().get(0).equals( val)) {
                                System.out.println("found common for " + temp_list.get(i));
                                break;
                            }
                        }
                        temp2.resetPointer();
                    }



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

                    temp1.subRelation();
                    temp2.subRelation();

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

            /* If there is no F left, we just need to append the columns of interest
             * find the tables that has the column of interest in it
             * append the first column, subrelate the table
             * start next sub-SPJEXECUTION
             */

            System.out.println("There is no F");
            System.out.println("X is:"+this.X);
            if(this.T.size()==0 || this.X.size() == 0){
                System.out.println("There is no T or no X");
                return null;
            }

            relation temp3 = this.T.get(1);
            System.out.println("Relation temp3 is: "+temp3.relationName);
            System.out.println("temp3's content is: "+temp3.distinctValuesOfFirstColumn());
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
        /*Test 1:
        *
        * Search for the row satisfies:
        * 1. KD's kid == 1
        */
        long startTime = System.nanoTime();
        ArrayList<String> X = new ArrayList<String>();
        X.add("kid");
        X.add("did");
        X.add("tf");
        X.add("blem");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF(0, "kid", 1);
        F.add(F1);

        ArrayList<relation> T = new ArrayList<relation>(1);
        relation kdsubrel = new relation();
        kdsubrel.initialiseRelationFilePath("src/map.bin", "src/blocks.bin");
        kdsubrel.processMapHeader();
        T.add(kdsubrel);

        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());
        long endTime = System.nanoTime();
        System.out.println("Runtime:"+(endTime-startTime)/(Math.pow(10,9))+"s");
    }

    public static void test2(){
        /*Test 2:
        *
        * Search for the row satisfies:
        * 1. KD's did == D's did
        * 2. KD's kid = 0
        */


        long startTime = System.nanoTime();


        ArrayList<String> X = new ArrayList<String>();
        X.add("kid");
        X.add("len");
        X.add("elen");
        X.add("url");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF(1, "value", 0, "did");
        structF F2 = new structF(1, "kid", 0);
        F.add(F2);
        F.add(F1);

        ArrayList<relation> T = new ArrayList<relation>(1);
        relation table_D = new relation();
        table_D.initialiseRelationFilePath("src/map_d.bin", "src/blocks_d.bin");
        table_D.processMapHeader();
        T.add(table_D);

        relation table_KD = new relation();
        table_KD.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD.processMapHeader();
        T.add(table_KD);

        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());

        long endTime = System.nanoTime();
        System.out.println("Runtime:"+(endTime-startTime)/(Math.pow(10,9))+"s");
    }

    public static void test3() {
        //Experiment 1

        long startTime = System.nanoTime();

        ArrayList<relation> T = new ArrayList<relation>(1);
//        relation table_K1 = new relation();
//        table_K1.initialiseRelationFilePath("src/map_k.bin", "src/blocks_k.bin");
//        table_K1.processMapHeader();
//        T.add(table_K1);
//        relation table_K1_D = new relation();
//        table_K1_D.initialiseRelationFilePath("src/map_k.bin", "src/blocks_k.bin");
//        table_K1_D.processMapHeader();
//        T.add(table_K1_D);
        relation table_KD1 = new relation();
        table_KD1.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD1.processMapHeader();
        T.add(table_KD1);
        relation table_K2 = new relation();
        table_K2.initialiseRelationFilePath("src/map_k.bin", "src/blocks_k.bin");
        table_K2.processMapHeader();
        T.add(table_K2);
        relation table_KD2 = new relation();
        table_KD2.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD2.processMapHeader();
        T.add(table_KD2);
        relation table_D1 = new relation();
        table_D1.initialiseRelationFilePath("src/map_d.bin", "src/blocks_d.bin");
        table_D1.processMapHeader();
        T.add(table_D1);

        ArrayList<String> X = new ArrayList<String>();
        X.add("idf");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF(0, "kid", 1, "kid");   F.add(F1);
        structF F5 = new structF(0, "value", "transferred");    F.add(F5);
//        structF F2 = new structF(2, "kid", 3, "kid");   F.add(F2);
//        structF F6 = new structF(2, "value", "patients");       F.add(F6);
//        structF F3 = new structF(1, "value", 3, "value");   F.add(F3);
//        structF F4 = new structF(3, "value", 4, "did");   F.add(F4);



        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());

        long endTime = System.nanoTime();
        System.out.println("Runtime:"+(endTime-startTime)/(Math.pow(10,9))+"s");

    }

    public static void test4() {
        //Experiment 1

        long startTime = System.nanoTime();

        ArrayList<relation> T = new ArrayList<relation>(1);


        relation table_KD1 = new relation();
        table_KD1.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD1.processMapHeader();
        T.add(table_KD1);

        relation table_KD2 = new relation();
        table_KD2.initialiseRelationFilePath("src/map_kd.bin", "src/blocks_kd.bin");
        table_KD2.processMapHeader();
        T.add(table_KD2);

        relation table_D1 = new relation();
        table_D1.initialiseRelationFilePath("src/map_d.bin", "src/blocks_d.bin");
        table_D1.processMapHeader();
        T.add(table_D1);

        relation table_D2 = new relation();
        table_D2.initialiseRelationFilePath("src/map_d.bin", "src/blocks_d.bin");
        table_D2.processMapHeader();
        T.add(table_D2);

        ArrayList<String> X = new ArrayList<String>();
        X.add("idf");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF(0, "kid", 7267);   F.add(F1);
        structF F2 = new structF(1, "kid", 13560); F.add(F2);
        structF F3 = new structF(0, "value", 1, "value"); F.add(F3);


        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());

        long endTime = System.nanoTime();
        System.out.println("Runtime:"+(endTime-startTime)/(Math.pow(10,9))+"s");

    }


    public static void main(String[] arg){
        test4();



    }

}




