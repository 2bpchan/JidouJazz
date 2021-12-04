package scoring;

import musicInfrastructure.Motif;

public abstract class Criteria {
	public String type;
	public String name;
	public double weight;
	public double target;
	public double minPossibleScore;
	public double maxPossibleScore;
	public String getCriteriaName() {
		return this.name;
	}
	/**
	 * @param Motif to score
	 * @return Raw score adjusted to be relative to its distance to the target score
	 */
	public double getAdjustedScore(Motif toScore) {
		double rawScore = this.getRawScore(toScore);
		double maxDistanceFromTarget = this.target;
		if(1.0-this.target > maxDistanceFromTarget) {
			maxDistanceFromTarget = 1.0-this.target;
		}
		//System.out.println("maxDistanceFromTarget: " + maxDistanceFromTarget);
		//System.out.println("target: " + this.target);
		//System.out.println("raw score: " + rawScore);
		double distanceToTarget = Math.abs(this.target-rawScore);
		//System.out.println("distance: " + distanceToTarget);
		double adjustedScore = (maxDistanceFromTarget-distanceToTarget)/maxDistanceFromTarget;		
		return adjustedScore;
	}
	public abstract double getRawScore(Motif toScore);
}
