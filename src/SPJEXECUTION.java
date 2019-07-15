/**
 * Created by Peilin on 7/8/19.
 */

import java.util.List;
import java.util.ListIterator;

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

    private List<Integer> X;
    private List<structF> F;
    private List<relation> T;
    private List<Integer> result;
    int val;


    SPJEXECUTION(List<Integer> X, List<structF> F, List<relation> T){
        this.X = X;
        this.F = F;
        this.T = T;
    }

    List<Integer> getResult(){
        this.result.clear();
        for(int i = 0; i < F.size(); i++){
            if(this.F.get(i).type==1 || this.F.get(i).type == 2){
                int index = this.T.indexOf(this.F.get(i).r1);
                if(index != -1){
                    relation temp = this.T.get(index);
                    if(this.F.get(i).type==1){
                        temp.jump(this.F.get(i).intLookUp);
                    }else{
                        temp.jump(this.F.get(i).strLookUp);
                    }

                    List<structF> F_d = this.F;
                    List<Integer> X_d = this.X;
                    List<relation> T_d = this.T;
                    ListIterator<relation> iterator_d = T_d.listIterator();

                    F_d.remove(this.F.get(i));
                    X_d.remove(temp.C());

                    while (iterator_d.hasNext()) {
                        relation next = iterator_d.next();
                        if (next.equals(temp)) {
                            iterator_d.set(temp.subRelation());
                        }
                    }

                    SPJEXECUTION SPJ_d = new SPJEXECUTION(X_d, F_d, T_d);
                    this.result = SPJ_d.getResult();
                    if(this.X.contains(temp.C())){
                        this.result.add(temp.getDataInt(temp.C()));
                    }

                    return this.result;

                }else{
                    return null;
                }
            }else if(this.F.get(i).type==3){
                int index1 = this.T.indexOf(this.F.get(i).r1);
                int index2 = this.T.indexOf(this.F.get(i).r2);
                if(index1 != -1 && index2 != -1){
                    relation temp1 = this.T.get(index1);
                    relation temp2 = this.T.get(index2);

                    List<structF> F_dd = this.F;
                    List<Integer> X_dd = this.X;
                    List<relation> T_dd = this.T;
                    ListIterator<relation> iterator_dd = T_dd.listIterator();

                    F_dd.remove(this.F.get(i));
                    X_dd.remove(temp1.C());
                    X_dd.remove(temp2.C());

                    while (iterator_dd.hasNext()) {
                        relation next = iterator_dd.next();
                        if (next.equals(temp1)) {
                            iterator_dd.set(temp1.subRelation());
                        }else if (next.equals(temp2)){
                            iterator_dd.set(temp2.subRelation());
                        }
                    }

                    SPJEXECUTION SPJ_dd = new SPJEXECUTION(X_dd, F_dd, T_dd);
                    this.result = SPJ_dd.getResult();
                    if(this.X.contains(temp1.C())){
                        this.result.add(temp1.getDataInt(temp1.C()));
                    }
                    if(this.X.contains(temp2.C())){
                        this.result.add(temp2.getDataInt(temp2.C()));
                    }

                    relation Til;
                    relation Tip;
                    if(temp1.curr()<temp2.curr()){
                        Til = temp1;
                        Tip = temp2;
                    }else{
                        Til = temp2;
                        Tip = temp1;
                    }
                    Til.jump(Tip.curr());
                }else{
                    return null;
                }

            }else{
                int smallestValue = this.T.get(0).numVals();
                int smallestIndex = 0;
                for(int d = 0; d < T.size();d++){
                    if (this.T.get(d).numVals() < smallestValue){
                        smallestValue = this.T.get(d).numVals();
                        smallestIndex = d;
                    }
                }
                relation temp3 = this.T.get(smallestIndex);
                while(!temp3.empty()){
                    int val = temp3.curr();
                    List<structF> F_ddd = this.F;
                    List<Integer> X_ddd = this.X;
                    List<relation> T_ddd = this.T;
                    ListIterator<relation> iterator_d = T_ddd.listIterator();

                    F_ddd.remove(this.F.get(i));
                    X_ddd.remove(temp3.C());

                    while (iterator_d.hasNext()) {
                        relation next = iterator_d.next();
                        if (next.equals(temp3)) {
                            iterator_d.set(temp3.subRelation());
                        }
                    }

                    SPJEXECUTION SPJ_d = new SPJEXECUTION(X_ddd, F_ddd, T_ddd);
                    this.result = SPJ_d.getResult();
                    if(this.X.contains(temp3.first().cols())){
                        this.result.add(temp3.getDataInt(temp3.C()));
                    }

                    return this.result;
                }
            }


        }

        return this.result;
    }
}




