/**
 * Created by Peilin on 7/8/19.
 */

import java.util.List;
import java.util.ListIterator;
import java.util.logging.XMLFormatter;

public class SPJEXECUTION {

    List<Integer> X;
    List<Integer> F;
    List<relation> T;
    List<Integer> result;
    int val;
    //Type about result is illy defined for now.
    //It should be a list

    SPJEXECUTION(List<Integer> X, List<Integer> F, List<relation> T){
        this.X = X;
        this.F = F;
        this.T = T;
    }

    List<Integer> getResult(){
        this.result = null;

        //get Ti
        int smallestValue = this.T.get(0).numVals();
        int index = 0;
        for(int i = 0; i < T.size();i++){
            if (this.T.get(i).numVals() < smallestValue){
                smallestValue = this.T.get(i).numVals();
                index = i;
            }
        }

        this.val = this.T.get(index).C();
        if (this.F.contains(this.val)){
            this.T.get(index).jump(this.val);
            if (this.T.get(index).curr() == this.val) {
                List<Integer> F_d = this.F;

                ListIterator<Integer> iterator = F_d.listIterator();
                while (iterator.hasNext()) {
                    Integer next = iterator.next();
                    if (next.equals(this.T.get(index).C())) {
                        iterator.set(this.val);
                    }
                }
                int TiCinX = 0;
                if (this.X.contains(this.T.get(index).C())) {
                    TiCinX = 1;
                    this.X.remove(this.T.get(index).C());
                }

                List<relation> newrelation = this.T;
                ListIterator<relation> iterator_r = newrelation.listIterator();
                while (iterator_r.hasNext()) {
                    relation next = iterator_r.next();
                    if (next.equals(this.T.get(index))) {
                        iterator_r.set(this.T.get(index).subRelation());
                    }
                }

                SPJEXECUTION newSPJ = new SPJEXECUTION(this.X, F_d, newrelation);
                this.result = newSPJ.getResult();
                if(TiCinX == 1){
                    //Append a column with fixed value val to result
                    //The following line is not complete
                    //because there should be a pointer from M(location) to v(column)
                    //For now i call it Mtov but it can be simplified
                    this.result.add(this.T.get(index).Mtov(this.T.get(index).M(this.val)));
                }
                return this.result;
            }else{
                return null;
            }
        }

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

        // Part 2
        if(flagAllSame == 1 && this.F.contains(record)){
            //Adding some initialization to prevent error
            // Getting Til
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
