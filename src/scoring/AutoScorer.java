package scoring;
import java.util.ArrayList;

import musicInfrastructure.Motif;
import scoring.ScoringCriteria.NoLone16thNotesCriteria;
import scoring.ScoringCriteria.NoteMileageCriteria;

public class AutoScorer {
	//the name of this is totally not a Symphogear reference
	ArrayList<Criteria> criteriaList;
	public AutoScorer() {
		criteriaList = new ArrayList<Criteria>();
		criteriaList.add(new NoteMileageCriteria());
		criteriaList.add(new NoLone16thNotesCriteria());
	}
	/**
	 * @param Motif to score
	 * @return List of unweighed Criteria scores
	 */
	public ArrayList<Double> scoreAll(Motif melody) {
		ArrayList<Double> scores = new ArrayList<Double>();
		for(Criteria c : criteriaList) {
			scores.add(c.getAdjustedScore(melody));
		}
		return scores;
	}
	/**
	 * @param Motif to score
	 * @return A weighted average of Criteria scores using the weight of each Criteria
	 */
	public double masterScore(Motif melody) {
		double numerator = 0.0;
		double denominator = 0.0;
		for(Criteria c : criteriaList) {
			numerator += c.getAdjustedScore(melody) * c.weight;
			denominator += c.weight;
		}
		return numerator/denominator;
	}
}
