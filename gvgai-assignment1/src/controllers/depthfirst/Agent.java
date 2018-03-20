package controllers.depthfirst;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.*;

/**
 * Created on 2018/3/15.
 * Description:
 * @author Liao
 */
public class Agent extends AbstractPlayer {

    private List<StateObservation> visitedState = new ArrayList<>();
    private LinkedList<Types.ACTIONS> actionSeq = new LinkedList<>();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        dfs(stateObs);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (actionSeq.isEmpty())
            throw new IllegalStateException("Action sequence is empty");

        return actionSeq.removeFirst();
    }

    private boolean dfs(StateObservation curr) {
        visitedState.add(curr);

        for (Types.ACTIONS action : curr.getAvailableActions()) {
            StateObservation stCopy = curr.copy();
            stCopy.advance(action);

            if (isVisited(stCopy)) {
                continue;
            }

            // Update action sequence and state set
            actionSeq.add(action);

            // If find the path successfully
            if (stCopy.isGameOver() && stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
                return true;
            }

            // Go on searching
            if (dfs(stCopy))
                return true;

            // Wrong path
            actionSeq.removeLast();
        }


        return false;
    }

    private boolean isVisited(StateObservation stateObservation) {
        return visitedState.stream().anyMatch(s -> s.equalPosition(stateObservation));
    }
}
