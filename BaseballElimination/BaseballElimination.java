import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {

	private HashMap<String, Integer> teamNameMap = null;
	private ArrayList<String> teamNameInOrder = null;
	private int[] wins = null;
	private int[] losses = null;
	private int[] remaining = null;
	private int teamCount = 0;
	private int[][] against = null;
	private FordFulkerson solution = null;
	private ArrayList<String> cut = null;

	public BaseballElimination(String filename) { // create a baseball division
													// from given filename in
													// format specified below
		In stdIn = new In(filename);
		teamCount = stdIn.readInt();
		teamNameInOrder = new ArrayList<String>(teamCount);
		teamNameMap = new HashMap<String, Integer>();
		wins = new int[teamCount];
		losses = new int[teamCount];
		remaining = new int[teamCount];
		against = new int[teamCount][teamCount];

		for (int i = 0; i < teamCount; i++) {
			String teamName = stdIn.readString();
			teamNameInOrder.add(teamName);
			teamNameMap.put(teamName, i);
			wins[i] = stdIn.readInt();
			losses[i] = stdIn.readInt();
			remaining[i] = stdIn.readInt();
			for (int j = 0; j < teamCount; j++)
				against[i][j] = stdIn.readInt();
		}
	}

	public int numberOfTeams() { // number of teams
		return teamCount;
	}

	public Iterable<String> teams() { // all teams
		return teamNameInOrder;
	}

	public int wins(String team) { // number of wins for given team
		validateTeamName(team);
		return wins[getTeamNumber(team)];
	}

	public int losses(String team) { // number of losses for given team
		validateTeamName(team);
		return losses[getTeamNumber(team)];
	}

	public int remaining(String team) { // number of remaining games for given
										// team
		validateTeamName(team);
		return remaining[getTeamNumber(team)];
	}

	public int against(String team1, String team2) { // number of remaining
														// games between team1
														// and team2
		validateTeamName(team1);
		validateTeamName(team2);
		int team1Number = getTeamNumber(team1);
		int team2Number = getTeamNumber(team2);
		return against[team1Number][team2Number];
	}

	public boolean isEliminated(String team) { // is given team eliminated?
		validateTeamName(team);
		int teamNumber = getTeamNumber(team);
		solution = null;
		cut = null;
		// Trivial elimination
		int maxWinsTeamNumber = 0;
		for (int i = 0; i < teamCount; i++) {
			if (wins[i] > wins[maxWinsTeamNumber])
				maxWinsTeamNumber = i;
		}
		if (wins[teamNumber] + remaining[teamNumber] < wins[maxWinsTeamNumber]) {
			if (cut == null)
				cut = new ArrayList<String>();
			cut.add(teamNameInOrder.get(maxWinsTeamNumber));
			return true;
		}

		// Nontrivial elimination
		int againstCount = (1 + teamCount) * teamCount / 2 - teamCount;
		int source = againstCount + teamCount;
		int sink = source + 1;
		FlowNetwork baseballNetwork = buildFlowNetwork(source, sink, teamNumber);
		solution = new FordFulkerson(baseballNetwork, source, sink);
		double realFlowVaule = solution.value();
		double answerFlowValue = 0;
		for (int i = 0; i < teamCount; i++) {
			for (int j = i + 1; j < teamCount; j++)
				answerFlowValue += against[i][j];
		}

		if (realFlowVaule != answerFlowValue) {
			if (cut == null)
				cut = new ArrayList<String>();
			for (int i = 0; i < teamCount; i++) {
				if (team.compareTo(teamNameInOrder.get(i)) != 0) {
					if(solution.inCut(i + againstCount))
						cut.add(teamNameInOrder.get(i));
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public Iterable<String> certificateOfElimination(String team) {
		// subset R of teams that eliminates given team; null if not eliminated
		validateTeamName(team);
		if (!isEliminated(team))
			return null;
		else
			return cut;
	}

	private int getTeamNumber(String team) {
		validateTeamName(team);
		return teamNameMap.get(team).intValue();
	}

	private void validateTeamName(String team) {
		// if the team is not in the hashmap, throw a IllegalArgumentException
		if (!teamNameMap.containsKey(team))
			throw new IllegalArgumentException();
	}

	private FlowNetwork buildFlowNetwork(int source, int sink, int teamNumber) {
		int nodeNumber = 0;
		FlowNetwork baseballNetwork = new FlowNetwork(sink + 1);
		// source -> each against game
		for (int i = 0; i < teamCount; i++) {
			for (int j = i + 1; j < teamCount; j++) {
				double capacity = against[i][j];
				FlowEdge edge = new FlowEdge(source, nodeNumber, capacity);
				baseballNetwork.addEdge(edge);
				nodeNumber++;
			}
		}
		// each against game -> two team
		int againstCount = nodeNumber;
		nodeNumber = 0;
		for (int i = 0; i < teamCount; i++) {
			for (int j = i + 1; j < teamCount; j++) {
				double capacity = Double.MAX_VALUE;
				FlowEdge edge = new FlowEdge(nodeNumber, againstCount + i,
						capacity);
				baseballNetwork.addEdge(edge);
				edge = new FlowEdge(nodeNumber, againstCount + j, capacity);
				baseballNetwork.addEdge(edge);
				nodeNumber++;
			}
		}
		// each team -> sink
		for (int i = 0; i < teamCount; i++) {
			double capacity = wins[teamNumber] + remaining[teamNumber]
					- wins[i];
			if (capacity <= 0)
				capacity = 0;
			FlowEdge edge = new FlowEdge(i + againstCount, sink, capacity);
			baseballNetwork.addEdge(edge);
		}
		return baseballNetwork;
	}

	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team))
					StdOut.print(t + " ");
				StdOut.println("}");
			} else {
				StdOut.println(team + " is not eliminated");
			}
		}
	}
}
