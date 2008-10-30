package geogebra.kernel;

import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class SliderAnimator implements ActionListener {
	
	private GeoNumeric num;
	private Timer timer;
	private int direction = 1; // either 1 or -1
	private AnimationUpdater animationUpdater;
	
	public SliderAnimator(AnimationUpdater animationUpdater, GeoNumeric num) {
		this.num = num;
		this.animationUpdater = animationUpdater;
		
		timer = new Timer(1, this);
		
	}
	
	public void startAnimation(boolean start) {
		if (start && !timer.isRunning()) timer.start();
		else if (!start && timer.isRunning()) timer.stop();
	}
	
	public void stopAnimation() {
		timer.stop();
	}
	
	public boolean isAnimating() {
		return timer.isRunning();
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		
		//Application.debug("speed "+num.getAnimationSpeed()+" type = "+num.getAnimationType());
		
		double val = num.getDouble();
		double min = num.getIntervalMin();
		double max = num.getIntervalMax();
		double step = num.getAnimationStep();
		double speed = num.getAnimationSpeed();
		
		val += num.getAnimationStep() * direction * (speed < 0 ? -1 : +1);
		
		switch (num.getAnimationType()) {
		
		case GeoElement.ANIMATION_CYCLIC:
			
			if (val > max) val = min;
			else if (val < min) val = max;
		
			break;
		
		default: //GeoElement.ANIMATION_TOANDFRO:
			
			if (val > max) {
				val = max;
				direction *= -1;
			} else if (val < min) {
				val = min;
				direction *= -1;				
			}
			
			break;
		
		
		}
		
		
		num.setValue(val);
		animationUpdater.updateCascadeRepaintDelayed(num);
		
		//num.updateCascade();
		//num.updateRepaint();
		
		// getAnimationSpeed 1 -> 5secs to get from one end of the slider to the other
		timer.setDelay((int)(5000.0 * step / (max - min) / Math.abs(num.getAnimationSpeed())));

	}

}
