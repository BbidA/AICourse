package controllers.Astar;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

import java.util.*;

/**
 * Created on 2018/3/18.
 * Description:
 * @author Liao
 */
public class Agent extends AbstractPlayer {


    private Queue<Node> stateQueue;
    private LinkedList<Types.ACTIONS> actionSeq = new LinkedList<>();
    private List<StateObservation> visitedNode = new ArrayList<>();

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        stateQueue = new PriorityQueue<>(Comparator.comparingDouble(n -> evaluateState(n.stateObs)));
        Node target = aStarSearch(new Node(stateObs, null, null));
        getActionSequence(target);
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if (actionSeq.isEmpty())
            throw new IllegalStateException("Action sequence is empty");

        return actionSeq.removeFirst();
    }

    private double evaluateState(StateObservation stateObs) {
        ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
        ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
        Vector2d goalPos = fixedPositions[1].size() == 0 ?
                stateObs.getAvatarPosition() : fixedPositions[1].get(0).position;
        if (movingPositions == null)
            return stateObs.getAvatarPosition().dist(goalPos);
        Vector2d keyPos = movingPositions[0].size() == 0 ?
                stateObs.getAvatarPosition() : movingPositions[0].get(0).position;

        return keyPos.dist(goalPos) + keyPos.dist(stateObs.getAvatarPosition());
    }

    private void getActionSequence(Node target) {
        while (target.parent != null) {
            actionSeq.addFirst(target.action);
            target = target.parent;
        }
    }

    private Node aStarSearch(Node curr) {
        while (!curr.isPlayerWin()) {
            visitedNode.add(curr.stateObs);
            curr.expandChildren(stateQueue, visitedNode);
            curr = stateQueue.poll();
//            System.out.println(curr.stateObs.getAvatarPosition());
//            System.out.println(curr.action);
//            curr.stateObs.getAvailableActions().forEach(System.out::println);
//            System.out.println();
        }

        return curr;
    }

    private static class Node {
        private final StateObservation stateObs;
        private final Node parent;
        private final Types.ACTIONS action;

        private Node(StateObservation stateObservation, Node parent, Types.ACTIONS action) {
            stateObs = stateObservation;
            this.parent = parent;
            this.action = action;
        }

        private void expandChildren(Queue<Node> stateQueue, List<StateObservation> visitedNode) {
            for (Types.ACTIONS action : stateObs.getAvailableActions()) {
                StateObservation stCopy = stateObs.copy();
                stCopy.advance(action);

                if (stCopy.isGameOver() && stCopy.getGameWinner() != Types.WINNER.PLAYER_WINS
                        || visitedNode.stream().anyMatch(stCopy::equalPosition)) {
                    continue;
                }

                Node child = new Node(stCopy, this, action);
                stateQueue.add(child);
            }
        }

        private boolean isPlayerWin() {
            return stateObs.isGameOver() && stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS;
        }
    }
}
