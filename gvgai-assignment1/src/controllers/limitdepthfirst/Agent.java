package controllers.limitdepthfirst;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2018/3/18.
 * Description:
 * @author Liao
 */
public class Agent extends AbstractPlayer {

    private LinkedList<Types.ACTIONS> actionSeq = new LinkedList<>();
    private List<StateObservation> visitedState = new ArrayList<>();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        dls(stateObs);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (actionSeq.isEmpty())
            throw new IllegalStateException("Action sequence is empty");

        return actionSeq.removeFirst();
    }

    private void dls(StateObservation stateObs) {
        int level = 0;
        while (!dls(stateObs, level, 0)) {
            level++;
            visitedState.clear();
        }
    }

    private boolean dls(StateObservation stateObservation, int targetLevel, int currLevel) {
        // Check level
        if (currLevel == targetLevel)
            return stateObservation.isGameOver() && stateObservation.getGameWinner() == Types.WINNER.PLAYER_WINS;

        // Go on searching
        visitedState.add(stateObservation);

        for (Types.ACTIONS action : stateObservation.getAvailableActions()) {
            StateObservation stCopy = stateObservation.copy();
            stCopy.advance(action);

            if (isVisited(stCopy))
                continue;

            actionSeq.add(action);
            if (dls(stCopy, targetLevel, currLevel + 1))
                return true;

            // Fail to find the path
            actionSeq.removeLast();
        }

        return false;
    }

    private boolean isVisited(StateObservation stateObs) {
        return visitedState.stream().anyMatch(s -> s.equalPosition(stateObs));
    }
}
