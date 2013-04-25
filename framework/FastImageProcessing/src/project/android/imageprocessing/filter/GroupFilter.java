package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.input.GLTextureOutputRenderer;
import project.android.imageprocessing.output.GLTextureInputRenderer;

/**
 * A multiple filter renderer extension of the BasicFilter. 
 * This class allows for a filter that contains multiple filters to create the output. 
 * This class can be used as the base for a filter which is made up of multiple filters.
 * By itself, this class is not useful because it's fragment shader only uses one texture. 
 * To take advantage of the multiple texture inputs, the getFragmentShader() method should be
 * override to return a more useful fragment shader.
 * @author Chris Batt
 */
public abstract class GroupFilter extends BasicFilter {
	
	private List<BasicFilter> initialFilters;
	private List<BasicFilter> terminalFilters;
	
	/**
	 * Creates a MultiInputFilter with any number of initial filters or filter graphs that produce a
	 * set number of textures which can be used by this filter.
	 */
	public GroupFilter() {
		initialFilters = new ArrayList<BasicFilter>();
		terminalFilters = new ArrayList<BasicFilter>();
	}
	
	protected void registerInitialFilter(BasicFilter filter) {
		initialFilters.add(filter);
	}
	
	protected void registerTerminalFilter(BasicFilter filter) {
		terminalFilters.add(filter);
	}

	/*
	 * If the source is one of the end points of the input filters then it is the result 
	 * of one of the internal filters. When all internal filters have finished we can
	 * draw the multi-input filter. If the source is not in the list of renderers then it 
	 * must be an external input which should be passed to each of the initial renderers
	 * of this multi-input filter.
	 */
	/* (non-Javadoc)
	 * @see project.android.imageprocessing.filter.BasicFilter#newTextureReady(int, project.android.imageprocessing.input.GLTextureOutputRenderer)
	 */
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		if(terminalFilters.contains(source)) {
			setWidth(source.getWidth());
			setHeight(source.getHeight());
			synchronized(getLockObject()) {
				for(GLTextureInputRenderer target : getTargets()) {
					target.newTextureReady(texture, this);
				}
			}
		} else {
			for(BasicFilter initialFilter : initialFilters) {
				initialFilter.newTextureReady(texture, source);
			}
		}
	}
	
	@Override
	public void setRenderSize(int width, int height) {
		for(BasicFilter initialFilter : initialFilters) {
			initialFilter.setRenderSize(width, height);
		}
	}
}
