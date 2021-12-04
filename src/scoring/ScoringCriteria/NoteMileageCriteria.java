package scoring.ScoringCriteria;

import musicInfrastructure.Measure;
import musicInfrastructure.Motif;
import musicInfrastructure.Note;
import scoring.Criteria;

public class NoteMileageCriteria extends Criteria {
	// max note mileage possible (all 16th notes and maximum jumpage) => 23*16*8 = 2944
	public NoteMileageCriteria() {
		this.type = "pitch";
		this.minPossibleScore = 0;
		this.maxPossibleScore = 2944;
		this.target = 0.03;
		this.weight = 0.5;
	}
	public NoteMileageCriteria(double target) {
		this.type = "pitch";
		this.minPossibleScore = 0;
		this.maxPossibleScore = 2944;
		this.target = target;
	}
	
	@Override
	public double getRawScore(Motif toScore) {
		int distance = 0;
		
		for(Measure m : toScore.getMeasures()) {
			int currentNoteValue = m.getNotes().get(0).getValue();
			for(Note n : m.getNotes()) {
				if(n.getValue() != -1) {
					distance += Math.abs(currentNoteValue - n.getValue());
					currentNoteValue = n.getValue();					
				}
			}
		}
		
		return ((double) distance)/maxPossibleScore;
	}

}
