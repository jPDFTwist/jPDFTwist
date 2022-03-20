package jpdftweak.core.tabparams;

import jpdftweak.core.PageDimension;

/**
 *
 * @author Vasilis Naskos
 */
public class ScaleParameters {

	private boolean isPortrait;
	private boolean isLandscape;

	private double[] portraitLimits;
	private double[] landscapeLimits;

	private boolean noEnlarge;
	private boolean preserveAspectRatio;

	private int justify;
	private int justifyPortrait;
	private int justifyLandscape;

	private PageDimension pageDim;
	private PageDimension landscapePageDim;
	private PageDimension portraitPageDim;

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

	public boolean isNoEnlarge() {
		return noEnlarge;
	}

	public void setNoEnlarge(boolean noEnlarge) {
		this.noEnlarge = noEnlarge;
	}

	public boolean isPreserveAspectRatio() {
		return preserveAspectRatio;
	}

	public void setPreserveAspectRatio(boolean preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public int getJustify() {
		return justify;
	}

	public void setJustify(int justify) {
		this.justify = justify;
	}

	public int getJustifyPortrait() {
		return justifyPortrait;
	}

	public void setJustifyPortrait(int justifyPortrait) {
		this.justifyPortrait = justifyPortrait;
	}

	public int getJustifyLandscape() {
		return justifyLandscape;
	}

	public void setJustifyLandscape(int justifyLandscape) {
		this.justifyLandscape = justifyLandscape;
	}

	public PageDimension getPageDim() {
		return pageDim;
	}

	public void setPageDim(PageDimension pageDim) {
		this.pageDim = pageDim;
	}

	public PageDimension getLandscapePageDim() {
		return landscapePageDim;
	}

	public void setLandscapePageDim(PageDimension landscapePageDim) {
		this.landscapePageDim = landscapePageDim;
	}

	public PageDimension getPortraitPageDim() {
		return portraitPageDim;
	}

	public void setPortraitPageDim(PageDimension portraitPageDim) {
		this.portraitPageDim = portraitPageDim;
	}

}
