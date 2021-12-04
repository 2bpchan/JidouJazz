package scoring.ScoringCriteria;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import musicInfrastructure.Motif;
import scoring.Criteria;

public class NoLone16thNotesCriteria extends Criteria {

	public NoLone16thNotesCriteria() {
		this.type = "rhythm";
		this.minPossibleScore = 0;
		this.maxPossibleScore = 42; //assuming max compression of 8 measures, highly unlikely
		this.target = 0;//we don't want any/we want as little as possible
		this.weight = 1;
	}
	@Override
	public double getRawScore(Motif toScore) {
		// TODO Auto-generated method stub
		String rhythm = toScore.getContinuousRhythmString();
		String patternString = "^[sS][^sS]|(?=([^sS][sS][^sS]))|[^sS][sS]$";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(rhythm);
		double occurrences = 0.0;
		while(matcher.find()) {
			occurrences++;
		}
		//System.out.println("regex located " + occurrences + " occurences");
		return occurrences/maxPossibleScore;
	}

}
