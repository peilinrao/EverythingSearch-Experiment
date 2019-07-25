/**
 * Created by Peilin on 7/8/19.
 */

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
            if(this.F.get(0).type==1 || this.F.get(0).type == 2){

                int index;
                for(index = 0; index < this.T.size(); index++){
                    if(this.T.get(index).relationName.equals(this.F.get(0).r1) ){
                        break;
                    }
                }

                if(index != -1){
                    relation temp = this.T.get(index);
                    if(this.F.get(0).type==2){
                        temp.jump(this.F.get(0).intLookUp);
                    }else{
                        temp.jump(this.F.get(0).strLookUp);
                    }

                    if(this.X.contains(temp.columnNames.get(0))){
                        System.out.println("Adding result:" + temp.distinctValuesOfFirstColumn().get(0));
                        this.result.add(temp.distinctValuesOfFirstColumn().get(0));
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
                    if (this.T.get(index1).relationName.equals(this.F.get(0).r1)) {
                        break;
                    }
                }

                int index2;
                for (index2 = 0; index2 < this.T.size(); index2++) {
                    if (this.T.get(index2).relationName.equals(this.F.get(0).r1)) {
                        break;
                    }
                }


                if (index1 != -1 && index2 != -1) {
                    relation temp1 = this.T.get(index1);
                    relation temp2 = this.T.get(index2);


                    ArrayList<structF> F_dd = this.F;
                    ArrayList<String> X_dd = this.X;
                    ArrayList<relation> T_dd = this.T;

                    F_dd.remove(this.F.get(0));
                    X_dd.remove(temp1.columnNames.get(0));
                    X_dd.remove(temp2.columnNames.get(0));


                    int count;
                    for (count = 0; count < T_dd.size(); count++) {
                        if (T_dd.get(count).relationName.equals(temp1.relationName)) {
                            T_dd.get(count).subRelation();
                        } else if (T_dd.get(count).relationName.equals(temp2.relationName)) {
                            T_dd.get(count).subRelation();
                        }
                    }

                    SPJEXECUTION SPJ_dd = new SPJEXECUTION(X_dd, F_dd, T_dd);
                    this.result = SPJ_dd.getResult();
                    if (this.X.contains(temp1.columnNames.get(0))) {
                        this.result.add(temp1.distinctValuesOfFirstColumn().get(0));
                    }
                    if (this.X.contains(temp2.columnNames.get(0))) {
                        this.result.add(temp2.distinctValuesOfFirstColumn().get(0));
                    }

                    relation Til;
                    relation Tip;
                    if ((int) temp1.currentPointerCountAndValue().get(1) < (int) temp2.currentPointerCountAndValue().get(1)) {
                        Til = temp1;
                        Tip = temp2;
                    } else {
                        Til = temp2;
                        Tip = temp1;
                    }
                    Til.jump(Tip.currentPointerCountAndValue());
                } else {
                    return null;
                }
            }

        }else{
            System.out.println("There is no F");
            if(this.T.size()==0 || this.X.size() == 0){
                System.out.println("There is no T or no X");
                return null;
            }

            relation temp3 = this.T.get(0);
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

    public static void main(String[] arg){
        //Testing
        ArrayList<String> X = new ArrayList<String>();
        X.add("kid");
        X.add("did");
        X.add("tf");
        X.add("blem");

        ArrayList<structF> F = new ArrayList<structF>();
        structF F1 = new structF("KD", "kid", 2);
        F.add(F1);

        ArrayList<relation> T = new ArrayList<relation>(1);
        relation kdsubrel = new relation();
        kdsubrel.initialiseRelationFilePath("src/map.bin", "src/blocks.bin");
        kdsubrel.processMapHeader();
        T.add(kdsubrel);

        SPJEXECUTION SPJ_d = new SPJEXECUTION(X, F, T);
        System.out.println("Showing result:"+SPJ_d.getResult());



    }

}




