package project.android.imageprocessing.filter;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.input.GLTextureOutputRenderer;

public class CompositeFilter extends MultiInputFilter {
	private List<BasicFilter> initialFilters;
	private List<GLTextureOutputRenderer> terminalFilters;
	private List<GLTextureOutputRenderer> inputOutputFilters;
	private List<GLTextureOutputRenderer> filters;

	public CompositeFilter(int numOfInputs) {
		super(numOfInputs);
		initialFilters = new ArrayList<BasicFilter>();
		terminalFilters = new ArrayList<GLTextureOutputRenderer>();
		inputOutputFilters = new ArrayList<GLTextureOutputRenderer>();
		filters = new ArrayList<GLTextureOutputRenderer>();
	}
	
	protected void registerInitialFilter(BasicFilter filter) {
		initialFilters.add(filter);
		registerFilter(filter);
	}
	
	protected void registerTerminalFilter(GLTextureOutputRenderer filter) {
		terminalFilters.add(filter);
		registerFilter(filter);
	}
	
	protected void registerInputOutputFilter(GLTextureOutputRenderer filter) {
		inputOutputFilters.add(filter);
	}
	
	protected void registerFilter(GLTextureOutputRenderer filter) {
		if(!filters.contains(filter)) {
			filters.add(filter);
		}
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
		if(inputOutputFilters.contains(source)) {
			if(!texturesReceived.contains(source)) {
				super.newTextureReady(texture, source);
				for(BasicFilter initialFilter : initialFilters) {
					initialFilter.newTextureReady(texture, source);
				}
			}
		} else if(terminalFilters.contains(source)) {
			super.newTextureReady(texture, source);
		} else {
			for(BasicFilter initialFilter : initialFilters) {
				initialFilter.newTextureReady(texture, source);
			}
		}
	}
	
	@Override
	public void setRenderSize(int width, int height) {
		for(GLTextureOutputRenderer filter : filters) {
			filter.setRenderSize(width, height);
		}
		super.setRenderSize(width, height);
	}
	
	@Override
	public void destroy() {
		super.destroy();
		for(GLTextureOutputRenderer filter : filters) {
			filter.destroy();
		}
	}
	
	
}
