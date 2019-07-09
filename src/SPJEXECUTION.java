/**
 * Created by Peilin on 7/8/19.
 */

import java.util.List;
import java.util.logging.XMLFormatter;

public class SPJEXECUTION {

    List<Integer> X;
    List<Integer> F;
    List<relation> T;
    List<Integer> result;

    SPJEXECUTION(List<Integer> X, List<Integer> F, List<relation> T){
        this.X = X;
        this.F = F;
        this.T = T;
    }

    List<Integer> getResult(){
        this.result = null;

        //
        int smallestValue = this.T.get(0).numVals();
        int indexOfSmallestValue = 0;
        for(int i = 0; i < T.size();i++){
            if (this.T.get(i).numVals() < smallestValue){
                smallestValue = this.T.get(i).numVals();
                indexOfSmallestValue = i;
            }
        }


        return this.result;
    }
}
