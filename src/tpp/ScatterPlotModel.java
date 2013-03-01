package tpp;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import java.util.Vector;

import javax.swing.JOptionPane;

import processing.core.PVector;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.matrix.Matrix;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * A TPPModel with added members for allowing it to be presented and manipulated
 * using via a Scatter Plot GUI
 */
public class ScatterPlotModel extends TPPModel implements Cloneable {
	
	/** The filtered instances */
	private Instances filteredInstances;
	
	private boolean useFilteredInstances = false;

	private boolean zeroInstances = false;

	// == Initialisation ================

	public ScatterPlotModel(int n) {
		super(n);
		edgeModel = new EdgeModel(this);
	}

	/*
	 */
	public void setInstances(Instances data, boolean filtered, ArrayList keptIndices) throws Exception {
		initialise(data, filtered, keptIndices);
	}
		
	protected void initialise(Instances ins, boolean filtered, ArrayList keptIndices) throws Exception {
		super.initialise(ins, filtered, keptIndices);
		initRetinalAttributes();
		isAxisSelected = new boolean[getNumDataDimensions()];
//		if(!projection.getZeroInstances().isEmpty()) {
//			zeroInstances = true;
//		}
	}
	
	public boolean zeroInstances(){
		return zeroInstances;
	}

	// == POINT SELECTION ===========================

	/**
	 * Select the points whose value of the selection attribute is equal to the
	 * given value
	 */
	public void selectPointsByClassValue(String value,
			boolean addToExistingSelection) {
		if (!addToExistingSelection)
			unselectPoints();
		for (int i = 0; i < getNumDataPoints(); i++)
			if (getInstances().instance(i).stringValue(getSelectAttribute())
					.equals(value))
				selectPoint(i);
		fireModelChanged(TPPModelEvent.POINT_SELECTION_CHANGED);
	}

	/**
	 * Select the points whose value of the selection attribute is in the given
	 * range.
	 */
	public void selectPointsByNumericRange(double min, double max,
			boolean addToExistingSelection) {
		if (!addToExistingSelection)
			unselectPoints();
		for (int i = 0; i < getNumDataPoints(); i++)
			if (getInstances().instance(i).value(getSelectAttribute()) >= min
					&& getInstances().instance(i).value(getSelectAttribute()) <= max)
				selectPoint(i);
		fireModelChanged(TPPModelEvent.POINT_SELECTION_CHANGED);
	}

	// == AXIS SELECTION==================

	/**
	 * A boolean array that indicates which axes have been selected. The axes
	 * correspond to the numeric attributes
	 */
	protected boolean[] isAxisSelected;

	/** Unselect the axes */
	public void unselectAxes() {
		isAxisSelected = new boolean[getNumDataDimensions()];
		fireModelChanged(TPPModelEvent.AXIS_SELECTION_CHANGED);
	}

	/** Add a axis to the selection */
	public void selectAxis(int axis) {
		isAxisSelected[axis] = true;
		fireModelChanged(TPPModelEvent.AXIS_SELECTION_CHANGED);
	}

	/** Add axes to the selection */
	public void selectAxes(int[] axes) {
		for (int a : axes)
			isAxisSelected[a] = true;
		fireModelChanged(TPPModelEvent.AXIS_SELECTION_CHANGED);
	}

	/** Are any axes selected? */
	public boolean areAxesSelected() {
		for (int a = 0; a < getNumDataDimensions(); a++)
			if (isAxisSelected[a])
				return true;
		return false;
	}

	public boolean isAxisSelected(int a) {
//		if (isAxisSelected[a])
//			System.out.println("selected axes is: " + a);
		return isAxisSelected[a];
	}

	public void unselectAxis(int i) {
		isAxisSelected[i] = false;
		fireModelChanged(TPPModelEvent.AXIS_SELECTION_CHANGED);
	}

	/**
	 * Determine which axis represents the given attribute. If the attribute is
	 * non-numeric (and hence will not be represented by an axis) then return -1
	 */
	public int getAxisForAttribute(Attribute at) {
		if (at.isNumeric()) {

		}
		return -1;
	}

	/** Move the selected axes by the (dx,dy) */
	public void moveSelectedAxes(double dx, double dy) {
		for (int a = 0; a < getNumDataDimensions(); a++) {
			if (isAxisSelected(a)) {
				getProjection().set(a, 0, getProjection().get(a, 0) + dx);
				getProjection().set(a, 1, getProjection().get(a, 1) + dy);
			}
		}
		project();
	}

	// == COLOR SCHEME ===============================

	protected ColourScheme colors = ColourScheme.DARK;

	/** Get teh color scheme */
	public ColourScheme getColours() {
		return colors;
	}

	/** Set the color scheme */
	public void setColours(ColourScheme colours) {
		colors = colours;
		fireModelChanged(TPPModelEvent.COLOR_SCHEME_CHANGED);
	}

	// == RETINAL ATTRIBUTES ===========================

	/**
	 * The default size of the markers to display (as a proportion of screen
	 * size).
	 */
	static final double MARKER_DEFAULT = 0.01;

	protected double markerSize = MARKER_DEFAULT;

	/** The retinal attributes */
	private Attribute shapeAttribute, colourAttribute, sizeAttribute,
			fillAttribute, selectAttribute;

	double colorAttributeLowerBound;

	double colorAttributeUpperBound;

	double sizeAttributeLowerBound;

	double sizeAttributeUpperBound;

	private Stack<ScatterPlotModel> snapshots;

	public Attribute getShapeAttribute() {
		return shapeAttribute;
	}

	public Attribute getFillAttribute() {
		return fillAttribute;
	}

	public Attribute getSelectAttribute() {
		return selectAttribute;
	}

	public Attribute getColourAttribute() {
		return colourAttribute;
	}

	public Attribute getSizeAttribute() {
		return sizeAttribute;
	}

	public void setSelectAttribute(Attribute selectAttribute) {
		this.selectAttribute = selectAttribute;
	}

	public int[] getDegree() {
		return degree;
	}

	/*
	 * Set which attribute will be used to set the point size
	 */
	public void setSizeAttribute(Attribute at) {
		this.sizeAttribute = at;

		// if the size attribute is numeric then find its range
		if (at != null && at.isNumeric()) {
			double v;
			sizeAttributeLowerBound = getInstances().instance(0).value(at);
			sizeAttributeUpperBound = getInstances().instance(0).value(at);
			for (int i = 1; i < getInstances().numInstances(); i++) {
				v = getInstances().instance(i).value(at);
				if (v > sizeAttributeUpperBound)
					sizeAttributeUpperBound = v;
				if (v < sizeAttributeLowerBound)
					sizeAttributeLowerBound = v;
			}
		}
		System.out.println(sizeAttributeLowerBound);
		System.out.println(sizeAttributeUpperBound);
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	/** Set the size attribute to be node degree */
	public void setGraphSizeAttribute(Graph graph, int index) {
		GraphMetrics metrics = new GraphMetrics(graph, this);

		// create an array with the chosen degree of each node
		switch (index) {
		case 0:
			degree = null;
			System.out.println("degree is now null");
			break;
		case 1:
			degree = metrics.calculateNodeDegree();
			System.out.println("degree is now total degree");
			break;
		case 2:
			degree = metrics.calculateNodeInDegree();
			System.out.println("degree is now in degree");
			break;
		case 3:
			degree = metrics.calculateNodeOutDegree();
			System.out.println("degree is now out degree");
			break;
		}

		// check that the degree isn't null
		if (degree != null) {
			// find the maximum and minimum degree values
			int length = degree.length;
			int i;
			int lowest = degree[0];
			int highest = degree[0];

			for (i = 1; i < length; i++) {
				if (degree[i] < lowest)
					lowest = degree[i];
				if (degree[i] > highest)
					highest = degree[i];
			}

			System.out.println(lowest);
			System.out.println(highest);

			// assign these values to the sizing bounds
			double v;
			sizeAttributeLowerBound = lowest;
			sizeAttributeUpperBound = highest;
			for (int j = 0; j < getInstances().numInstances(); j++) {
				v = degree[j];
				if (v > sizeAttributeUpperBound)
					sizeAttributeUpperBound = v;
				if (v < sizeAttributeLowerBound)
					sizeAttributeLowerBound = v;
			}

			// update the view
		}
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	public void resetSizes(Attribute sizeAttribute) {
		this.sizeAttribute = sizeAttribute;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	public void setColourAttribute(Attribute at) {
		this.colourAttribute = at;

		// if the color attribute is numeric then find its range
		if (at != null && at.isNumeric()) {
			double v;
			colorAttributeLowerBound = getInstances().instance(0).value(at);
			colorAttributeUpperBound = getInstances().instance(0).value(at);
			for (int i = 1; i < getInstances().numInstances(); i++) {
				v = getInstances().instance(i).value(at);
				if (v > colorAttributeUpperBound)
					colorAttributeUpperBound = v;
				if (v < colorAttributeLowerBound)
					colorAttributeLowerBound = v;
			}
		}
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	public void setShapeAttribute(Attribute shapeAttribute) {
		this.shapeAttribute = shapeAttribute;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	public void setFillAttribute(Attribute fillAttribute) {
		this.fillAttribute = fillAttribute;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	public void setMarkerSize(double d) {
		markerSize = d;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	public double getMarkerSize() {
		return markerSize;
	}

	void initRetinalAttributes() {
		shapeAttribute = null;
		sizeAttribute = null;
		fillAttribute = null;

		if (getInstances() != null && getInstances().classIndex() >= 0)
			colourAttribute = getInstances().classAttribute();
		else
			colourAttribute = null;
		selectAttribute = colourAttribute;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);

	}

	/** Remove an attribute, returning the values */
	public double[] removeAttribute(Attribute at) {
		// before removing an attribute from a scatter plot first check whether
		// it is currently being used to draw the markers
		removeRetinalAttribute(at);
		return super.removeAttribute(at);
	}

	/** remove multiple attributes */
	public void removeAttributes(Vector<Attribute> attributes) {
		takeSnapshot();
		for (Attribute at : attributes)
			removeRetinalAttribute(at);
		unselectAxes();
		int[] atx = new int[attributes.size()];
		for (int a = 0; a < atx.length; a++)
			atx[a] = indexOf(attributes.get(a));
		Remove remove = new Remove();
		remove.setAttributeIndicesArray(atx);
		try {
			remove.setInputFormat(instances);
			instances = Filter.useFilter(instances, remove);
			// reinitialise the projection and data etc, preserving the
			// projection
			// NB we have to do this since the Remove filter messes up
			// references to color attributes etc
			double[][] oldProjectionValues = getProjection().copy().getArray();
			initialise(instances, false, null);
			// copy back the values of the projection (except those from the
			// removed attributes)
			((LinearProjection) getProjection()).setValues(MatrixUtils
					.removeRows(oldProjectionValues, atx));
			project();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fireModelChanged(TPPModelEvent.DATA_STRUCTURE_CHANGED);
	}

	/**
	 * If this attribute is being used as a retinal attribute, then set that
	 * attribute to null
	 */
	private void removeRetinalAttribute(Attribute at) {
		if (getShapeAttribute() == at)
			setShapeAttribute(null);
		if (getColourAttribute() == at)
			setColourAttribute(null);
		if (getSizeAttribute() == at)
			setSizeAttribute(null);
		if (getFillAttribute() == at)
			setFillAttribute(null);
		if (getSelectAttribute() == at)
			setSelectAttribute(null);
	}

	// == Selection rectangle =========================

	protected Rectangle rectangle;

	private static final double MARGIN = 0.1;

	/** Initialise a rectangle with corners at the given points */
	public void initRectangle(Point2D p1, Point2D p2) {
		rectangle = new Rectangle(p1, p2);
	}

	/** Create a rectangle that includes all selected points */
	public void drawRectangleAroundSelectedPoints() {
		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		boolean firstSelectedPoint = true;
		for (int i = 0; i < getNumDataPoints(); i++) {
			if (isPointSelected(i)) {
				if (xMin > getView().get(i, 0) || firstSelectedPoint)
					xMin = getView().get(i, 0);
				if (xMax < getView().get(i, 0) || firstSelectedPoint)
					xMax = getView().get(i, 0);
				if (yMin > getView().get(i, 1) || firstSelectedPoint)
					yMin = getView().get(i, 1);
				if (yMax < getView().get(i, 1) || firstSelectedPoint)
					yMax = getView().get(i, 1);
				firstSelectedPoint = false;
			}
		}
		// recall that minY is the top of the rectangle and maxY is the bottom
		// margin is a percentage of the maximum dimension
		double margin = max(xMax - xMin, yMax - yMin) * MARGIN;
		rectangle = new Rectangle(xMin - margin, yMin - margin, xMax + margin,
				yMax + margin);
		setAttributeMeans();
	}

	/** Create a rectangle that includes all selected axes */
	public void drawRectangleAroundSelectedAxes() {
		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		boolean firstSelectedAxis = true;
		for (int i = 0; i < getNumDataDimensions(); i++) {
			if (isAxisSelected(i)) {
				if (xMin > getProjection().get(i, 0) || firstSelectedAxis)
					xMin = getProjection().get(i, 0);
				if (xMax < getProjection().get(i, 0) || firstSelectedAxis)
					xMax = getProjection().get(i, 0);
				if (yMin > getProjection().get(i, 1) || firstSelectedAxis)
					yMin = getProjection().get(i, 1);
				if (yMax < getProjection().get(i, 1) || firstSelectedAxis)
					yMax = getProjection().get(i, 1);
				firstSelectedAxis = false;
			}
		}
		// recall that minY is the top of the rectangle and maxY is the bottom
		// margin is a percentage of the maximum dimension
		double margin = max(xMax - xMin, yMax - yMin) * MARGIN;
		rectangle = new Rectangle(xMin - margin, yMin - margin, xMax + margin,
				yMax + margin);
	}

	/** Select any points within the rectangle */
	public void selectPointsByRectangle() {
		if (rectangle != null) {
			for (int i = 0; i < getNumDataPoints(); i++)
				selectedPoints[i] = rectangle.contains(getView().get(i, 0),
						getView().get(i, 1));
			setAttributeMeans();
			fireModelChanged(TPPModelEvent.POINT_SELECTION_CHANGED);
		}
	}

	/** Select any axes within the rectangle */
	public void selectAxesByRectangle() {
		if (rectangle != null) {
			for (int a = 0; a < getNumDataDimensions(); a++)
				isAxisSelected[a] = rectangle.contains(getProjection()
						.get(a, 0), getProjection().get(a, 1));
			fireModelChanged(TPPModelEvent.AXIS_SELECTION_CHANGED);
		}
	}

	private double max(double a, double b) {
		return (a < b ? b : a);
	}

	// == Other graph decoration =======================

	/** Whether or not to show the axes. */
	protected boolean showAxes = false;

	/** Whether to show axes */
	public void setShowAxes(boolean b) {
		showAxes = b;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean showAxes() {
		return showAxes;
	}

	/** Whether to show the target currently being pursued */
	protected boolean showTarget;

	public boolean showTarget() {
		return showTarget;
	}

	public void setShowTarget(boolean showTarget) {
		this.showTarget = showTarget;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public void createSeries(Attribute indexAttribute, Attribute idAttribute) {
		super.createSeries(indexAttribute, idAttribute);
		setShowSeries(true);
	}

	public void removeSeries() {
		super.removeSeries();
		setShowSeries(false);
	}

	/** whether to show series */
	protected boolean showSeries = false;

	public boolean showSeries() {
		return showSeries;
	}

	/** Show any series in the data */
	public void setShowSeries(boolean show) {
		showSeries = show;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	protected boolean showHierarchicalClustering;

	public void setShowHierarchicalClustering(boolean show) {
		showHierarchicalClustering = show;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean showHierarchicalClustering() {
		return showHierarchicalClustering;
	}

	public Attribute createTestSet(int k) {
		super.createTestSet(k);
		setFillAttribute(getTestAttribute());
		return getTestAttribute();
	}

	// == Transforming date space into device space ===================

	/**
	 * How big is the margin round the points as a proportion of the overall
	 * window
	 */
	private static final double PANEL_MARGIN = .1d;

	/** THe transform for transforming points from daa space to device space */
	private AffineTransform transform;

	/** The size of the device space */
	private int width, height;

	/**
	 * Resize the scatter plot so that it fits in the new window size
	 * 
	 * @return
	 */
	public void resizePlot(int width, int height) {
		this.width = width;
		this.height = height;
		transform = getTransform(width, height);
		fireModelChanged(TPPModelEvent.PROJECTION_CHANGED);
	}

	/** Rescale the scatter plot so that it fits into the existing window */
	public void resizePlot() {
		transform = getTransform(width, height);
		fireModelChanged(TPPModelEvent.PROJECTION_CHANGED);
	}

	/**
	 * Return a transform that will map data space onto device space of the
	 * given width and height, so that the points (and axes, if shown) fit
	 * snugly into the panel
	 * 
	 */
	private AffineTransform fitPointsToWindowAtCurrentProjection(double width,
			double height) {

		// Make sure the origin is included in the range of points
		double xmax = 0;
		double ymax = 0;
		double xmin = 0;
		double ymin = 0;

		// find range of values in the current view
		// (both points and axes -- if shown)
		for (int row = 0; row < getNumDataPoints(); row++) {
			if (getView().get(row, 0) > xmax)
				xmax = getView().get(row, 0);
			if (getView().get(row, 1) > ymax)
				ymax = getView().get(row, 1);
			if (getView().get(row, 0) < xmin)
				xmin = getView().get(row, 0);
			if (getView().get(row, 1) < ymin)
				ymin = getView().get(row, 1);
		}

		if (showAxes())
			for (int axis = 0; axis < getNumDataDimensions(); axis++) {
				if (getProjection().get(axis, 0) > xmax)
					xmax = getProjection().get(axis, 0);
				if (getProjection().get(axis, 0) < xmin)
					xmin = getProjection().get(axis, 0);
				if (getProjection().get(axis, 1) > ymax)
					ymax = getProjection().get(axis, 1);
				if (getProjection().get(axis, 1) < ymin)
					ymin = getProjection().get(axis, 1);
			}

		// The same scaling factor is used on both x and y axes (so that the
		// overall proportions of the selected view are preserved)
		// So find what scaling factor will give the best fit.
		// NB this can end up with large scaling factors which can cause
		// problems:
		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294396
		int MARGIN = (int) ((width < height ? width : height) * PANEL_MARGIN);
		double xRange = xmax - xmin;
		double yRange = ymax - ymin;
		double scaleX = (width - (2 * MARGIN)) / xRange;
		double scaleY = (height - (2 * MARGIN)) / yRange;
		double scale = (scaleX > scaleY ? scaleY : scaleX);
		// System.out.println("scaleX=" + scaleX + "\tscaleY=" + scaleY +
		// "\tscale=" + scale);
		double translationX = (-xmin * scale) + MARGIN;
		double translationY = (-ymin * scale) + MARGIN;
		AffineTransform transform = new AffineTransform();
		transform.setTransform(scale, 0, 0, scale, translationX, translationY);
		return transform;
	}

	/**
	 * Return a transform that will map data space onto device space of the
	 * given width and height, so that the points (and axes, if shown) fit
	 * snugly into the panel, adjusting the projection so that the scale of the
	 * transform is within reasonable limits. <br>
	 * (See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294396 and
	 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4982427 for more on
	 * reasonable limits for AffineTransforms)
	 * 
	 */
	public AffineTransform getTransform(double width, double height) {
		AffineTransform transform = null;

		// only rescale if the panel is not zero
		if (width > 0 && height > 0) {

			// first scale the projection so that it fits into the current
			// window
			transform = fitPointsToWindowAtCurrentProjection(width, height);
			// NB this can produce large/small scaling factors which can
			// cause problems, so adjust the scale so that it is within
			// reasonable limits
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6294396
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4982427

			while (transform.getScaleX() > 100) {
				projection.timesEquals(2);
				project();
				transform = fitPointsToWindowAtCurrentProjection(width, height);
			}
			while (transform.getScaleX() < 1) {
				projection.timesEquals(0.5);
				project();
				transform = fitPointsToWindowAtCurrentProjection(width, height);
			}
		}
		// System.out.println("transform changed");
		fireModelChanged(TPPModelEvent.PROJECTION_CHANGED);
		return transform;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public ScatterPlotModel clone() {
		ScatterPlotModel clone = new ScatterPlotModel(numViewDimensions);
		Instances cloneInstances = new Instances(instances);
		try {
			clone.setInstances(cloneInstances, false, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clone.projection = new LinearProjection(projection);
		clone.project();
		return clone;
	}

	/** Take a snapshot of the current state of the model */
	private void takeSnapshot() {
		if (snapshots == null)
			snapshots = new Stack<ScatterPlotModel>();
		snapshots.push(this.clone());
	}

	/** Undo any changes back to the previous snapshot */
	public void undo() {
		if (snapshots != null && snapshots.size() > 0) {
			ScatterPlotModel previous = snapshots.pop();
			try {
				initialise(previous.instances, false, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			projection = previous.projection;
			project();
			fireModelChanged(TPPModelEvent.DATA_STRUCTURE_CHANGED);
		}
	}

	/** Whether there is a snapshot to undo to. */
	public boolean canUndo() {
		return (snapshots != null && snapshots.size() > 0);
	}

	// ---- Anything to do with the Graph/Network----------------

	// Load the Graph

	protected boolean showGraph = false;
	protected boolean graphLoaded = false;

	private EdgeModel edgeModel;

	public void loadGraph(Graph graph) {
		super.loadGraph(graph);
		setShowGraph(true);
		setGraphLoaded(true);
	}

	// Remove the graph from the model

	public void removeGraph() {
		super.removeGraph();
		setShowGraph(false);
	}

	// Hide the Edges from view

	public void setShowGraph(boolean show) {
		showGraph = show;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean showGraph() {
		return showGraph;
	}

	public void setGraphLoaded(boolean b) {
		graphLoaded = b;
	}

	public boolean graphLoaded() {
		return graphLoaded;
	}

	public EdgeModel getEdgeModel() {
		return edgeModel;
	}
	// Set the transparency level

	protected int transparencyLevel = 25;

	public int getTransparencyLevel() {
		return transparencyLevel;
	}

	public void setTransparencyLevel(int t) {
		transparencyLevel = t;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}
	
	protected int currentTransparency;
	
	public int getTransparency() {
		return currentTransparency;
	}

	public void setTransparency(int t) {
		currentTransparency = t;
	}
	

	protected float beizerCurviness = 0.1f;

	public float getBeizerCurviness() {
		return beizerCurviness;
	}

	public void setBeizerCurviness(float t) {
		beizerCurviness = t;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	protected boolean labels = false;

	public void showLabels(boolean b) {
		labels = b;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean labels() {
		return labels;
	}

	protected boolean highlightedLabels = false;

	public void showHightlightedLabels(boolean b) {
		highlightedLabels = b;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean highlightedLabels() {
		return highlightedLabels;
	}

	protected boolean hoverLabels = false;

	public void showHoverLabels(boolean b) {
		hoverLabels = b;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean hoverLabels() {
		return hoverLabels;
	}

	protected boolean selectedLabels = false;

	public void showSelectedLabels(boolean b) {
		selectedLabels = b;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean selectedLabels() {
		return selectedLabels;
	}

	protected boolean nodeLabelColor = false;

	public void showNodeLabelColor(boolean b) {
		nodeLabelColor = b;
		fireModelChanged(TPPModelEvent.DECORATION_CHANGED);
	}

	public boolean nodeLabelColor() {
		return nodeLabelColor;
	}

	protected double labelSize = 0.25;

	public double getLabelSize() {
		return labelSize;
	}

	public void setLabelSize(double t) {
		labelSize = t;
		fireModelChanged(TPPModelEvent.RETINAL_ATTRIBUTE_CHANGED);
	}

	protected boolean nodeDegreeSet = false;
	protected int[] degree = null;

	private DBConnection dbConnection;

	// public void setNodeDegree(Graph currentGraph) {
	//
	// Instances ins = getInstances();
	// int numberInstances = ins.numInstances();
	// Connection cnxn = null;
	//
	// int i;
	// for (i = 0; i < numberInstances; i++){
	//
	// Iterator<Connection> allConnections =
	// getGraph().getAllConnections().iterator();
	// String currentNode = ins.instance(i).toString(0);
	// int currentDegree = 0;
	//
	// while (allConnections.hasNext()) {
	// cnxn = allConnections.next();
	// if(cnxn.getSourceNode().equals(currentNode))
	// currentDegree++;
	// if(cnxn.getTargetNode().equals(currentNode))
	// currentDegree++;
	// }
	// degree[i] = currentDegree;
	// }
	// }

	public int[] getNodeDegree() {
		return degree;
	}

	/**
	 * Find all the neighbours of a node
	 * 
	 * @param i
	 *            is the index of the node we wish to find a parameter for
	 */
	
		
	public boolean neighbourSelected(int i) {
		// get all neighbours of a node
		int idIndex =  instances.attribute(getEdgeAttributeString()).index(); 
		Iterator<Connection> nbs = getGraph().findNeighbours(
				instances.instance(i).stringValue(idIndex)).iterator();
		boolean result = false;
		while (nbs.hasNext()) {
			Connection nextnbr;
			int j;
			nextnbr = nbs.next();
			// check if the node i is the source node in this connection and
			// that we are wanting to display
			// outgoing edges
			if (nextnbr.getSourceNode().equals(instances.instance(i).stringValue(idIndex)) ){
				// if it is get the instance this target node belongs to
				// Instance target = nextnbr.getTargetInstance();
				// j = indexOf(target);
				j = nextnbr.getTargetIndex();
				// then check if this instance is in the list of selected nodes
				if (isPointSelected(j) && edgeModel.incomingEdges()) {
					result = true;
				}
			} else if (nextnbr.getTargetNode().equals(instances.instance(i).stringValue(idIndex))) {
				// Instance source = nextnbr.getSourceInstance();
				// j = indexOf(source);
				j = nextnbr.getSourceIndex();
				if (isPointSelected(j) && edgeModel.outgoingEdges()) {
					result = true;
				}
			} else {
				result = false;
			}
		}
		return result;
		// return false;
	}

	public void addDatabaseConnection(String username, String password,
			String database, String table) {
		dbConnection = new DBConnection(username, password, database, table);
	}

	public void runQuery() throws SQLException {
		ResultSet rs = null;
		Statement statement = null;
		System.out.println(dbConnection);
		com.mysql.jdbc.Connection db = dbConnection.getConnection();
		String query = "select * from " + dbConnection.getTable() + " where ";
		int j = 0;
		for (int i = 0; i < getInstances().numInstances(); i++) {
			if (isPointSelected(i)) {
				if (j > 0) {
					query += " or ";
				}
				String node = instances.instance(i).stringValue(edgeAttIndex);
				query += " sourceip = '" + node + "' or destip = '" + node
						+ "'";
				j++;
			}

		}
		statement = db.createStatement();
		System.out.println(query);
		rs = statement.executeQuery(query);

		if (rs != null)
			System.out.println("the result set is" + rs.toString());
		else
			System.out.println("result set is null");

		while (rs.next()) {
			System.out.println(rs.getString(2));
		}

		DatabaseTableGUI dbTableGUI = new DatabaseTableGUI(rs);

	}

	private String spectrumColor = "Default";
	private String classColor = "Default";
	private String bgColor = "Light";

	/** Noise added to the view to better separate the points */
	private Matrix noise;
	
	public void setNoise(){
		noise = new Matrix(getNumDataPoints(),
				getNumDataDimensions());
	}
	
	/** Change the noise */
	public void updateNoise() {
		double scale = getTransform().getScaleX();
		Random ran = new Random();
		for (int i = 0; i < noise.getRowDimension(); i++)
			for (int j = 0; j < noise.getColumnDimension(); j++)
				noise.set(i, j, (ran.nextDouble() - 0.5d) * 20d / scale);
	}
	
	public Matrix getNoise(){
		return noise;
	}
	
	public void setBGColor(String selectedItem) {
		bgColor = selectedItem;

	}

	public void setClassColor(String selectedItem) {
		classColor = selectedItem;

	}

	public void setSpectrumColor(String selectedItem) {
		spectrumColor = selectedItem;

	}

	public String getBGColor() {
		return bgColor;

	}

	public String getClassColor() {
		return classColor;

	}

	public String getSpectrumColor() {
		return spectrumColor;

	}

	/**
	 * Set the colours depending on if they are chosen to be viewed by class or
	 * numeric
	 * @param i
	 *            the node the colour applies to
	 * @return
	 */
	Color setColor(int i) {
		Color c = null;
		if (getColourAttribute() == null) {
			c = getColours().getForegroundColor();
		} else if (getColourAttribute().isNominal()) {
			c = getColours().getClassificationColor(
					(int) getInstances().instance(i)
							.value(getColourAttribute()));
		} else if (getColourAttribute().isNumeric()) {
			c = getColours().getColorFromSpectrum(
					getInstances().instance(i)
							.value(getColourAttribute()),
					colorAttributeLowerBound,
					colorAttributeUpperBound);
		}
		return c;
	}

	

}
