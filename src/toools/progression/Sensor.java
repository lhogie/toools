package toools.progression;

public class Sensor
{
	public double progressStatus = 0;

	public double getProgress()
	{
		return progressStatus;
	}

	public void set(double target)
	{
		this.progressStatus = target;
	}
}
