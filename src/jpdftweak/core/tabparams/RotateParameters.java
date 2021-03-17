/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jpdftweak.core.tabparams;

/**
 *
 * @author vasilis
 */
public class RotateParameters {
    
    private int portraitCount;
    private int landscapeCount;
    
    private boolean isPortrait;
    private boolean isLandscape;
    
    /*
     * index 0 for lower limit
     * index 1 for upper limit
     */
    private double[] portraitLimits;
    private double[] landscapeLimits;

    public int getPortraitCount() {
        return portraitCount;
    }

    public void setPortraitCount(int portraitCount) {
        this.portraitCount = portraitCount;
    }

    public int getLandscapeCount() {
        return landscapeCount;
    }

    public void setLandscapeCount(int landscapeCount) {
        this.landscapeCount = landscapeCount;
    }

    public boolean isPortrait() {
        return isPortrait;
    }

    public void setIsPortrait(boolean isPortrait) {
        this.isPortrait = isPortrait;
    }

    public boolean isLandscape() {
        return isLandscape;
    }

    public void setIsLandscape(boolean isLandscape) {
        this.isLandscape = isLandscape;
    }

    public double[] getPortraitLimits() {
        return portraitLimits;
    }
    
    public double getPortraitLowerLimit() {
        return portraitLimits[0];
    }
    
    public double getPortraitUpperLimit() {
        return portraitLimits[1];
    }

    public void setPortraitLimits(double[] portraitLimits) {
        this.portraitLimits = portraitLimits;
    }

    public double[] getLandscapeLimits() {
        return landscapeLimits;
    }
    
    public double getLandscapeLowerLimit() {
        return landscapeLimits[0];
    }
    
    public double getLandscapeUpperLimit() {
        return landscapeLimits[1];
    }

    public void setLandscapeLimits(double[] landscapeLimits) {
        this.landscapeLimits = landscapeLimits;
    }
    
    
    
}
