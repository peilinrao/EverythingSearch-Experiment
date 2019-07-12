/**
 * Created by Peilin on 7/8/19.
 */

import java.util.List;
import java.util.ListIterator;

// The comment within {} are the psuedo code coresponding to my code

public class SPJEXECUTION {

    List<Integer> X;
    List<Integer> F;
    List<relation> T;
    List<Integer> result;
    int val;


    SPJEXECUTION(List<Integer> X, List<Integer> F, List<relation> T){
        this.X = X;
        this.F = F;
        this.T = T;
    }

    List<Integer> getResult(){
        //{result = none}
        this.result.clear();

        //get Ti
        int smallestValue = this.T.get(0).numVals();
        int index = 0;
        for(int i = 0; i < T.size();i++){
            if (this.T.get(i).numVals() < smallestValue){
                smallestValue = this.T.get(i).numVals();
                index = i;
            }
        }

        /*
        * if Ti.C = val in F then
        *   Ti.jump(val)
        *   if Ti.curr() == val then
        *       Replace every occurrence of Ti.C in F with val to obtain F'
        *       result = spjExecution(X-{Ti.C}, F', [T1,...,Ti.subRelation(),...,Tn])
        *       If Ti.C is in X append a column with fixed value val to result
        *       return result
        *   else
        *       return none
        *   end if
        * end if
        *
         */
        this.val = this.T.get(index).C();
        if (this.F.contains(this.val)){
            this.T.get(index).jump(this.val);
            if (this.T.get(index).curr() == this.val) {
                List<Integer> F_d = this.F;
                List<Integer> X_d = this.X;

                ListIterator<Integer> iterator = F_d.listIterator();
                while (iterator.hasNext()) {
                    Integer next = iterator.next();
                    if (next.equals(this.T.get(index).C())) {
                        iterator.set(this.val);
                    }
                }
                int TiCinX = 0;
                if (X_d.contains(this.T.get(index).C())) {
                    TiCinX = 1;
                    X_d.remove(this.T.get(index).C());
                }

                List<relation> newrelation = this.T;
                ListIterator<relation> iterator_r = newrelation.listIterator();
                while (iterator_r.hasNext()) {
                    relation next = iterator_r.next();
                    if (next.equals(this.T.get(index))) {
                        iterator_r.set(this.T.get(index).subRelation());
                    }
                }

                SPJEXECUTION newSPJ = new SPJEXECUTION(X_d, F_d, newrelation);
                this.result = newSPJ.getResult();
                if(TiCinX == 1){
                    //Append a column with fixed value val to result
                    //because there should be a pointer from M(location) to v(column)
                    this.result.add(this.T.get(index).curr());
                }
                return this.result;
            }else{
                return null;
            }
        }

        // {if Ti1.C = Ti2.C = ... = Tik.C in F}
        int flagAllSame = 1;
        this.T.get(index).reset(); //Initialize before tranverse
        int record = this.T.get(index).curr(); //Here int is illy defined
        while(this.T.get(index)!=null){
            if (this.T.get(index).curr() == record){
                this.T.get(index).next();
            }else{
                flagAllSame = 0;
                break;
            }
        }

        if(flagAllSame == 1 && this.F.contains(record)){
            /*
            * while for any j, Tij.empty() != true do:
            *   if there exist a val for any j, Tij.curr() = val then
            *       Remove each atom like Tij.C = Til.C from F to obtain F'
            *       Replace each occurrence of Tij.C with val in F' to obtain F''
            *       Replace each Tij in T iwht Tij.subRelation() to obtain T'
            *       subResult = spjExecution(X-{Ti1.C...Tik.C}, F'', T')
            *       Add col with constant value "val" corresponding to each Tij.C in X to the subresult
            *       result += subResult
            *   end if
            *   Til = argmin(Tij.curr())
            *   Tip = argmin{j!=l}(Tij.curr())
            *   Til.jump(Tip.curr())
            * end while
            * return result
            *
            *
            *
             */

            this.T.get(index).reset();
            int min_num = this.T.get(index).curr();
            while(this.T.get(index)!=null){
                if (this.T.get(index).curr() < min_num){
                    min_num = this.T.get(index).curr();
                }
                this.T.get(index).next();
            }
            relation Til = this.T.get(index).jump(min_num);
            //get Tip
            this.T.get(index).reset();
            int sec_min_num = this.T.get(index).curr();
            while(this.T.get(index)!=null){
                if (this.T.get(index).curr() < sec_min_num && this.T.get(index).curr() != min_num){
                    sec_min_num = this.T.get(index).curr();
                }
                this.T.get(index).next();
            }
            relation Tip = this.T.get(index).jump(sec_min_num);
            Til.jump(Tip.curr());

            // Add initialization
            while(!this.T.get(index).empty()){
                if (this.T.get(index).curr() != -1){
                    List<Integer> tempF = this.F;

                    this.T.get(index).reset();
                    while(this.T.get(index)!=null){
                        if (this.T.get(index).C() == Til.C()){
                            tempF.remove(this.T.get(index).C());
                        }
                        ListIterator<Integer> newFIterator = tempF.listIterator();

                        while(newFIterator.hasNext()) {
                            if (newFIterator.next() == this.T.get(index).C()){
                                newFIterator.set(this.val);
                            }
                        }
                        this.T.get(index).next();
                    }

                    List<relation> tempT = this.T;
                    tempT.get(index).reset();
                    while(tempT.get(index)!=null){
                        tempT.set(index,tempT.get(index).subRelation());
                        tempT.get(index).next();
                    }

                    List<Integer> tempX = this.X;
                    this.T.get(index).reset();
                    while(this.T.get(index)!=null){
                        if(tempX.contains(this.T.get(index).C())) {
                            tempX.remove(this.T.get(index).C());
                        }
                        this.T.get(index).next();
                    }
                    SPJEXECUTION newSPJ = new SPJEXECUTION(tempX, tempF, tempT);
                    List<Integer> subResult = newSPJ.getResult();
                    this.T.get(index).reset();
                    while(this.T.get(index)!=null){
                        subResult.add(this.T.get(index).C());
                        this.T.get(index).next();
                    }
                    result.addAll(subResult);
                }
                // Getting Til
                this.T.get(index).reset();
                min_num = this.T.get(index).curr();
                while(this.T.get(index)!=null){
                    if (this.T.get(index).curr() < min_num){
                        min_num = this.T.get(index).curr();
                    }
                    this.T.get(index).next();
                }
                Til = this.T.get(index).jump(min_num);
                //get Tip
                this.T.get(index).reset();
                sec_min_num = this.T.get(index).curr();
                while(this.T.get(index)!=null){
                    if (this.T.get(index).curr() < sec_min_num && this.T.get(index).curr() != min_num){
                        sec_min_num = this.T.get(index).curr();
                    }
                    this.T.get(index).next();
                }
                Tip = this.T.get(index).jump(sec_min_num);
                Til.jump(Tip.curr());
                //Go to next while loop
                this.T.get(index).next();
            }
        }

        /*
        * while Ti.empty() != true do
        *   val = Ti.curr()
        *   replace each occurence of Ti.C in F with val to obtain F'
        *   replace each Ti in T with Ti.subRelation() to obtain T'
        *   subresult = spjExecution(X-{Ti.C}, F', T')
        *   Add col with constant value "val" to subresult if Ti.first in X
        *   result += subresult
        *   return result
         */
        while(!this.T.get(index).empty()){
            this.val = this.T.get(index).curr();
            List<Integer> X_new;
            List<Integer> F_new;
            List<relation> T_new;

            F_new = this.F;
            if( F_new.contains(this.T.get(index).C())){
                F_new.remove(this.T.get(index).C());
            }
            T_new = this.T;
            ListIterator<relation> iterator_T_new = T_new.listIterator();
            while (iterator_T_new.hasNext()) {
                relation next = iterator_T_new.next();
                if (next.equals(this.T.get(index))) {
                    iterator_T_new.set(this.T.get(index).subRelation());
                }
            }
            X_new = this.X;
            if(X_new.contains(this.T.get(index))){
                X_new.remove(this.T.get(index));
            }

            SPJEXECUTION SPJ_new = new SPJEXECUTION(X_new, F_new, T_new);
            List<Integer> subResult = SPJ_new.getResult();
            if (this.X.contains(this.T.get(index).first())){
                subResult.add(this.val);
            }
            result.addAll(subResult);
            return result;
        }




        return this.result;
    }
}
