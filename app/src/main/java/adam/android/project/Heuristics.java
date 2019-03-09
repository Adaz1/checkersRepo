package adam.android.project;

import sac.State;
import sac.StateFunction;

/**
 * Created by Adam on 17.02.2018.
 */

public class Heuristics extends StateFunction {
    public double calculate(State state) {
        Checkers checkers = (Checkers) state;
        /*System.out.println("BLACK VALUE:      " + checkers.blackPiecesAmount);
        System.out.println("WHITE VALUE:      " + checkers.whitePiecesAmount);*/
        if (checkers.blackPiecesAmount == 0) {
            return Double.POSITIVE_INFINITY;
        }
        else if (checkers.whitePiecesAmount == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        else {
            return checkers.whitePiecesAmount - checkers.blackPiecesAmount;
            //return Math.random() % 10;
        }

    }
}
