package tpp;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.core.Attribute;

@SuppressWarnings("serial")
public class GraphOptionsTab extends JPanel implements ActionListener,
		ItemListener, ChangeListener {

	ScatterPlotModel spModel;
	private EdgeModel edgeModel;
	private PointModel pointModel;
	private GraphModel graphModel;

	private JButton loadGraphButton;
	private JCheckBox directedCheckBox;
	private JCheckBox filterEdgeCheckBox;
	private JCheckBox edgeWeightsCheckBox;

//	private JRadioButton sourceRB;
//	private JRadioButton targetRB;
//	private JRadioButton mixedRB;
//	private JRadioButton noneRB;
	
	private JLabel edgeColorLabel;
	private JComboBox<String> edgeColorCombo; 
	private String[] edgeColorOptions = {"None", "Source", "Target", "Mixed"};
	
//	private JRadioButton straightRB;
//	private JRadioButton curvedRB;
//	private JRadioButton hideRB;
//	private JRadioButton bundledRB;
//	private JRadioButton fannedRB;
	
	private JLabel edgeShapeLabel;
	private JComboBox<String> edgeShapeCombo;
	private String[] edgeShapeOptions = {"Straight", "Curved", "Bundled", "Fanned", "Intelligent", "Hidden"};

	private JCheckBox incomingCB;
	private JCheckBox outgoingCB;
	private JSlider transparencySlider;
	
	private JComboBox graphSizeCombo;

	private JCheckBox showLabelsCheckBox;
	private JCheckBox nodeColorCB;
	
//	private JCheckBox showHighlightedLabels;
//	private JCheckBox showHoverLabels;
//	private JCheckBox showSelectedLabels;
	
	private JLabel labelsLabel;
	private JComboBox<String> labelsCombo;
	private String[] labelsOptions = {"All", "Highlighted", "Selected", "Hover"};

	private JSlider labelSlider;
	
	private JCheckBox edgeWeightFilterCB;

	private Border raisedetched = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	private Dimension min = new Dimension(100, 20);
	private JCheckBox labelSizesLabel;
	private JCheckBox labelFilterLabel;
	private JComboBox<String> labelSizeCB;
	private JComboBox<String> labelFilterCB;
	private JLabel labelFilterLowerValue;
	private JLabel labelFilterUpperValue;
	private RangeSlider labelFilter;



	public GraphOptionsTab(ScatterPlotModel spModel) {

		super();
		this.spModel = spModel;
		edgeModel = spModel.getEdgeModel();
		pointModel = spModel.getPointModel();
		graphModel = spModel.getGraphModel();
		init();
		setVisible(true);
		
	}

	private void init() {

		setLayout(new GridBagLayout());
		GridBagConstraints graphGrid = new GridBagConstraints();

		graphGrid.fill = GridBagConstraints.BOTH;
		graphGrid.weightx = 1.0;
		graphGrid.weighty = 1.0;

		addGraphOptionsPanel(this, graphGrid);

		// if(spModel.graphLoaded())
		enableGraphButtons();

	}

	private void addGraphOptionsPanel(JPanel graphPanel,
			GridBagConstraints graphGrid) {

		graphGrid.fill = GridBagConstraints.HORIZONTAL;
		graphGrid.weightx = 1.0;
		graphGrid.gridy = 0;
		graphGrid.gridx = 0;
		graphGrid.insets = new Insets(1, 1, 1, 1);

		addLoadGraph(graphPanel, graphGrid);

		graphGrid.gridx = 0;
		graphGrid.gridy++;

		addDrawEdgeOptions(graphPanel, graphGrid);

//		graphGrid.gridx = 0;
//		graphGrid.gridy++;
//
//		addEdgeType(graphPanel, graphGrid);
//
		graphGrid.gridx = 0;
		graphGrid.gridy++;

		chooseVisibleEdges(graphPanel, graphGrid);
//
//		graphGrid.gridx = 0;
//		graphGrid.gridy++;
//
//		addSliders(graphPanel, graphGrid);
//
		graphGrid.gridx = 0;
		graphGrid.gridy++;

		addLabelsPanel(graphPanel, graphGrid);

		graphGrid.gridx = 0;
		graphGrid.gridy++;

		addEdgeFilterPanel(graphPanel, graphGrid);
	}

	private void addLoadGraph(JPanel panel, GridBagConstraints grid) {

		JPanel loadGraphPanel = new JPanel();
		loadGraphPanel.setLayout(new GridBagLayout());
		GridBagConstraints loadGraphGrid = new GridBagConstraints();

		loadGraphGrid.fill = GridBagConstraints.BOTH;
		loadGraphGrid.weightx = 1.0;
		loadGraphGrid.gridy = 0;
		loadGraphGrid.insets = new Insets(0, 0, 0, 0);

		TitledBorder border = BorderFactory.createTitledBorder(raisedetched,
				"Load graph options:");
		loadGraphPanel.setBorder(border);

		loadGraphButton = new JButton("Load Graph");

		directedCheckBox = new JCheckBox("Show Arrows");
		directedCheckBox.setEnabled(false);
		directedCheckBox.setSelected(edgeModel.arrowedEdges());

		filterEdgeCheckBox = new JCheckBox("Filter Edges");
		filterEdgeCheckBox.setEnabled(false);
		filterEdgeCheckBox.setSelected(edgeModel.filterAllEdges());

		edgeWeightsCheckBox = new JCheckBox("Weight Edges");
		edgeWeightsCheckBox.setEnabled(false);
		edgeWeightsCheckBox.setSelected(edgeModel.viewEdgeWeights());

		loadGraphButton.addActionListener(this);
		directedCheckBox.addActionListener(this);
		filterEdgeCheckBox.addActionListener(this);
		edgeWeightsCheckBox.addActionListener(this);

		loadGraphButton.setToolTipText("Load a new graph from a csv file");
		directedCheckBox
				.setToolTipText("Indicate if the graph is directed or not");
		filterEdgeCheckBox
				.setToolTipText("Show only the edges of selected nodes");

		loadGraphGrid.gridy++;
		loadGraphGrid.gridx = 0;
		loadGraphGrid.gridwidth = 1;

		loadGraphPanel.add(loadGraphButton, loadGraphGrid);

		loadGraphGrid.gridx = 1;
		loadGraphGrid.gridwidth = 1;

		loadGraphPanel.add(directedCheckBox, loadGraphGrid);

		loadGraphGrid.gridx = 2;
		loadGraphGrid.gridwidth = 1;

		loadGraphPanel.add(filterEdgeCheckBox, loadGraphGrid);

		loadGraphGrid.gridx = 3;
		loadGraphGrid.gridwidth = 1;

		loadGraphPanel.add(edgeWeightsCheckBox, loadGraphGrid);

		panel.add(loadGraphPanel, grid);
	}

	private void addDrawEdgeOptions(JPanel viewOptionsPanel,
			GridBagConstraints viewOptionsGrid) {

		JPanel edgePanel = new JPanel();
		edgePanel.setLayout(new GridBagLayout());
		GridBagConstraints edgeGrid = new GridBagConstraints();

		edgeGrid.fill = GridBagConstraints.BOTH;
		edgeGrid.weightx = 1.0;
		edgeGrid.gridy = 0;
		edgeGrid.insets = new Insets(0, 0, 0, 0);

		TitledBorder border = BorderFactory.createTitledBorder(raisedetched,
				"Edge Color and Shape:");
		edgePanel.setBorder(border);

//		sourceRB = new JRadioButton("Source");
//		targetRB = new JRadioButton("Target");
//		mixedRB = new JRadioButton("Mixed");
//		noneRB = new JRadioButton("None");
//
//		sourceRB.setSelected(edgeModel.sourceColorEdges());
//		targetRB.setSelected(edgeModel.targetColorEdges());
//		mixedRB.setSelected(edgeModel.mixedColorEdges());
//		noneRB.setSelected(edgeModel.defaultColorEdges());
//
//		sourceRB.setEnabled(false);
//		targetRB.setEnabled(false);
//		mixedRB.setEnabled(false);
//		noneRB.setEnabled(false);
//
//		ButtonGroup group = new ButtonGroup();
//		group.add(noneRB);
//		group.add(mixedRB);
//		group.add(sourceRB);
//		group.add(targetRB);
//
//		sourceRB.addItemListener(this);
//		targetRB.addItemListener(this);
//		mixedRB.addItemListener(this);
//		noneRB.addItemListener(this);
//
//		noneRB.setToolTipText("Color all edges grey");
//		mixedRB.setToolTipText("Color edges based on a combination of their source and target nodes");
//		sourceRB.setToolTipText("Color edge based on source node color");
//		targetRB.setToolTipText("Color edges based on their target node color");
		
		edgeColorLabel = new JLabel("Edge color:", JLabel.RIGHT);
		edgeColorCombo = new JComboBox<>(edgeColorOptions);
		edgeColorCombo.setEnabled(false);
		edgeColorCombo.addActionListener(this);
		edgeColorCombo.setSelectedItem("None");
		
		edgeShapeLabel = new JLabel("Edge shape:", JLabel.RIGHT);
		edgeShapeCombo = new JComboBox<>(edgeShapeOptions);
		edgeShapeCombo.setEnabled(false);
		edgeShapeCombo.addActionListener(this);

		edgeGrid.gridx = 0;
		edgeGrid.gridwidth = 1;
		
		edgePanel.add(edgeColorLabel, edgeGrid);

		edgeGrid.gridx = 1;
		edgeGrid.gridwidth = 1;

		edgePanel.add(edgeColorCombo, edgeGrid);

		edgeGrid.gridx = 2;
		edgeGrid.gridwidth = 1;

		edgePanel.add(edgeShapeLabel, edgeGrid);

		edgeGrid.gridx = 3;
		edgeGrid.gridwidth = 1;

		edgePanel.add(edgeShapeCombo, edgeGrid);

//		edgeColorPanel.add(noneRB, edgeColorGrid);
//
//		edgeColorGrid.gridx = 1;
//		edgeColorGrid.gridwidth = 1;
//
//		edgeColorPanel.add(sourceRB, edgeColorGrid);
//
//		edgeColorGrid.gridx = 2;
//		edgeColorGrid.gridwidth = 1;
//
//		edgeColorPanel.add(targetRB, edgeColorGrid);
//
//		edgeColorGrid.gridx = 3;
//		edgeColorGrid.gridwidth = 1;
//
//		edgeColorPanel.add(mixedRB, edgeColorGrid);

		viewOptionsGrid.gridx = 0;
		viewOptionsGrid.gridy++;

		viewOptionsPanel.add(edgePanel, viewOptionsGrid);

	}

//	private void addEdgeType(JPanel viewOptionsPanel,
//			GridBagConstraints viewOptionsGrid) {
//
//		JPanel edgeStylePanel = new JPanel();
//		edgeStylePanel.setLayout(new GridBagLayout());
//		GridBagConstraints edgeStyleGrid = new GridBagConstraints();
//
//		edgeStyleGrid.fill = GridBagConstraints.BOTH;
//		edgeStyleGrid.weightx = 1.0;
//		edgeStyleGrid.gridy = 0;
//		edgeStyleGrid.insets = new Insets(0, 0, 0, 0);
//
//		TitledBorder border = BorderFactory.createTitledBorder(raisedetched,
//				"Pick an edge style:");
//		edgeStylePanel.setBorder(border);
//
//		straightRB = new JRadioButton("Straight");
//		curvedRB = new JRadioButton("Curved");
//		hideRB = new JRadioButton("Hide");
//		bundledRB = new JRadioButton("Bundle");
//		fannedRB = new JRadioButton("Fan");
//
//		straightRB.setSelected(edgeModel.straightEdges());
//		curvedRB.setSelected(edgeModel.bezierEdges());
//		bundledRB.setSelected(edgeModel.bundledEdges());
//		fannedRB.setSelected(edgeModel.fannedEdges());
//		hideRB.setSelected(!spModel.showGraph());
//
//		straightRB.setEnabled(false);
//		curvedRB.setEnabled(false);
//		hideRB.setEnabled(false);
//		bundledRB.setEnabled(false);
//		fannedRB.setEnabled(false);
//
//		ButtonGroup group = new ButtonGroup();
//		group.add(straightRB);
//		group.add(curvedRB);
//		group.add(hideRB);
//		group.add(bundledRB);
//		group.add(fannedRB);
//
//		straightRB.addItemListener(this);
//		curvedRB.addItemListener(this);
//		hideRB.addItemListener(this);
//		bundledRB.addItemListener(this);
//		fannedRB.addItemListener(this);
//
//		straightRB.setToolTipText("Use straight edges");
//		curvedRB.setToolTipText("Use curved edges");
//		hideRB.setToolTipText("Hide all edges");
//		bundledRB.setToolTipText("Bundle edges from centroids");
//		fannedRB.setToolTipText("Fan edges");
//
//		edgeStyleGrid.gridx = 0;
//		edgeStyleGrid.gridwidth = 1;
//
//		edgeStylePanel.add(straightRB, edgeStyleGrid);
//
//		edgeStyleGrid.gridx = 1;
//		edgeStyleGrid.gridwidth = 1;
//
//		edgeStylePanel.add(curvedRB, edgeStyleGrid);
//
//		edgeStyleGrid.gridx = 2;
//		edgeStyleGrid.gridwidth = 1;
//
//		edgeStylePanel.add(bundledRB, edgeStyleGrid);
//
//		edgeStyleGrid.gridx = 3;
//		edgeStyleGrid.gridwidth = 1;
//
//		edgeStylePanel.add(fannedRB, edgeStyleGrid);
//
//		edgeStyleGrid.gridx = 4;
//		edgeStyleGrid.gridwidth = 1;
//
//		edgeStylePanel.add(hideRB, edgeStyleGrid);
//
//		viewOptionsGrid.gridx = 0;
//		viewOptionsGrid.gridy++;
//
//		viewOptionsPanel.add(edgeStylePanel, viewOptionsGrid);
//
//	}

	private void chooseVisibleEdges(JPanel panel, GridBagConstraints grid) {

		JPanel edgeDirectionPanel = new JPanel();
		edgeDirectionPanel.setLayout(new GridBagLayout());
		GridBagConstraints edgeDirectionGrid = new GridBagConstraints();

		edgeDirectionGrid.fill = GridBagConstraints.BOTH;
		edgeDirectionGrid.weightx = 1.0;
		edgeDirectionGrid.gridy = 0;
		edgeDirectionGrid.insets = new Insets(0, 0, 0, 0);

		TitledBorder border = BorderFactory.createTitledBorder(raisedetched,
				"Set which edges are visible:");
		edgeDirectionPanel.setBorder(border);

		incomingCB = new JCheckBox("Incoming");
		outgoingCB = new JCheckBox("Outgoing");

		incomingCB.setSelected(edgeModel.incomingEdges());
		outgoingCB.setSelected(edgeModel.outgoingEdges());

		incomingCB.setEnabled(false);
		outgoingCB.setEnabled(false);

		incomingCB.addActionListener(this);
		outgoingCB.addActionListener(this);

		incomingCB.setToolTipText("Show a node's incoming links");
		outgoingCB.setToolTipText("Show a node's outgoing links");
		
		JLabel transparencyLabel = new JLabel("Transparency: ", JLabel.LEFT);
		transparencySlider = new JSlider(0, 255, spModel.getTransparencyLevel());
		transparencySlider.setEnabled(false);
		transparencySlider.setValue((int) (spModel.getTransparencyLevel()));
		transparencySlider.addChangeListener(this);
		transparencySlider.setToolTipText("Change the amount of transparency for the non-highlighted point and edges");
		
		
		edgeDirectionGrid.gridy++;
		edgeDirectionGrid.gridx = 0;
		edgeDirectionGrid.gridwidth = 1;

		edgeDirectionPanel.add(incomingCB, edgeDirectionGrid);

		edgeDirectionGrid.gridx = 1;
		edgeDirectionGrid.gridwidth = 1;

		edgeDirectionPanel.add(outgoingCB, edgeDirectionGrid);
		
		edgeDirectionGrid.gridx = 2;
		edgeDirectionGrid.gridwidth = 1;

		edgeDirectionPanel.add(transparencyLabel, edgeDirectionGrid);
		
		edgeDirectionGrid.gridx = 3;
		edgeDirectionGrid.gridwidth = GridBagConstraints.REMAINDER;

		edgeDirectionPanel.add(transparencySlider, edgeDirectionGrid);
		
		addGraphSizeSelector(edgeDirectionPanel, edgeDirectionGrid);

		panel.add(edgeDirectionPanel, grid);

	}

	private void addSliders(JPanel viewOptionsPanel,
			GridBagConstraints viewOptionsGrid) {

		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridBagLayout());
		GridBagConstraints sliderGrid = new GridBagConstraints();

		sliderGrid.fill = GridBagConstraints.BOTH;
		sliderGrid.weightx = 1.0;
		sliderGrid.gridy = 0;
		sliderGrid.insets = new Insets(0, 0, 0, 0);

		TitledBorder border = BorderFactory.createTitledBorder(raisedetched,
				"Select transparency and node size:");
		sliderPanel.setBorder(border);

		JLabel transparencyLabel = new JLabel("Transparency: ", JLabel.LEFT);

		sliderGrid.gridy = 0;
		sliderGrid.gridx = 0;
		sliderGrid.gridwidth = 1;

		sliderPanel.add(transparencyLabel, sliderGrid);

		transparencySlider = new JSlider(0, 255, spModel.getTransparencyLevel());
		transparencySlider.setEnabled(false);
		transparencySlider.setValue((int) (spModel.getTransparencyLevel()));
		transparencySlider.addChangeListener(this);
		transparencySlider
				.setToolTipText("Change the amount of transparency for the non-highlighted point and edges");

		sliderGrid.gridx = 1;
		sliderGrid.gridwidth = 2;
		sliderPanel.add(transparencySlider, sliderGrid);

		addGraphSizeSelector(sliderPanel, sliderGrid);

		viewOptionsPanel.add(sliderPanel, viewOptionsGrid);

	}

	private void addGraphSizeSelector(JPanel panel, GridBagConstraints grid) {

		String[] degreeOptions = { "None", "Degree", "In Degree", "Out Degree" };
		graphSizeCombo = new JComboBox(degreeOptions);
		graphSizeCombo.setEnabled(false);

		graphSizeCombo.setMinimumSize(min);
		graphSizeCombo.setSelectedIndex(0);
		graphSizeCombo.addActionListener(this);
		graphSizeCombo
				.setToolTipText("Choose which graph metric is used to determine the size of each point");

		JLabel sizeAttributeSelectorLabel = new JLabel("Size points by: ",
				JLabel.LEFT);

		grid.gridy++;
		grid.gridx = 0;
		grid.gridwidth = 1;

		panel.add(sizeAttributeSelectorLabel, grid);

		grid.gridx = 1;
		grid.gridwidth = GridBagConstraints.REMAINDER;

		panel.add(graphSizeCombo, grid);
	}

	private void addLabelsPanel(JPanel viewOptionsPanel,
			GridBagConstraints viewOptionsGrid) {

		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new GridBagLayout());
		GridBagConstraints labelGrid = new GridBagConstraints();

		labelGrid.fill = GridBagConstraints.BOTH;
		labelGrid.weightx = 1.0;
		labelGrid.gridy = 0;
		labelGrid.insets = new Insets(0, 0, 0, 0);

		TitledBorder border = BorderFactory.createTitledBorder(raisedetched,
				"Show the node labels:");
		labelPanel.setBorder(border);

		showLabelsCheckBox = new JCheckBox("Show Labels");
		nodeColorCB = new JCheckBox("Node Color");
		labelsLabel = new JLabel("Label options:", JLabel.LEFT);
		labelsCombo = new JComboBox<String>(labelsOptions);	

		showLabelsCheckBox.setSelected(pointModel.labels());
		nodeColorCB.setSelected(pointModel.nodeLabelColor());

		showLabelsCheckBox.setEnabled(false);
		nodeColorCB.setEnabled(false);
		labelsCombo.setEnabled(false);

		showLabelsCheckBox.addActionListener(this);
		nodeColorCB.addActionListener(this);
		labelsCombo.addActionListener(this);

		showLabelsCheckBox.setToolTipText("Show the point's label or number");
		nodeColorCB.setToolTipText("Color labels the same as their node's colour");

		labelGrid.gridx = 0;
		labelPanel.add(showLabelsCheckBox, labelGrid);

		labelGrid.gridx = 1;
		labelPanel.add(nodeColorCB, labelGrid);

		labelGrid.gridx = 2;
		labelPanel.add(labelsLabel, labelGrid);

		labelGrid.gridx = 3;
		labelPanel.add(labelsCombo, labelGrid);

		JLabel labelSizeLabel = new JLabel("Label Size: ", JLabel.RIGHT);

		labelGrid.gridy++;
		labelGrid.gridx = 0;
		labelPanel.add(labelSizeLabel, labelGrid);

		labelSlider = new JSlider(1, 100, (int) (pointModel.getLabelSize() * 100));
		labelSlider.setValue((int) (pointModel.getLabelSize() * 100));
		labelSlider.addChangeListener(this);
		labelSlider.setToolTipText("Change the size of the labels");
		labelSlider.setEnabled(false);

		labelGrid.gridx = 1;
		labelGrid.gridwidth = GridBagConstraints.REMAINDER;
		labelPanel.add(labelSlider, labelGrid);
		
		labelSizesLabel = new JCheckBox("Size labels");
		labelFilterLabel = new JCheckBox("Filter labels");
		
		labelSizesLabel.setEnabled(false);
		labelFilterLabel.setEnabled(false);
		
		labelSizesLabel.addActionListener(this);
		labelFilterLabel.addActionListener(this);
		
		String[] degreeOptions = { "None", "Degree", "In Degree", "Out Degree" };
		
		labelSizeCB = new JComboBox<>(degreeOptions);
		labelFilterCB = new JComboBox<>(degreeOptions);
		
		labelSizeCB.setEnabled(false);
		labelFilterCB.setEnabled(false);
				
		labelSizeCB.addItemListener(this);
		labelFilterCB.addItemListener(this);
				
		labelFilterLowerValue = new JLabel();
		labelFilterUpperValue = new JLabel();

		labelFilter = new RangeSlider();
		
		if (spModel.showGraph() && labelFilterLabel.isSelected()) {
			labelFilter.setMinimum((int) Math.floor(graphModel.getMinLabelFilterDegree()));
			labelFilter.setMaximum((int) Math.ceil(graphModel.getMaxLabelFilterDegree()));

			labelFilter.setValue((int) Math.floor(graphModel.getMinLabelFilterDegree()));
			labelFilter.setUpperValue((int) Math.ceil(graphModel.getMaxLabelFilterDegree()));
		} else {
			labelFilter.setMinimum(0);
			labelFilter.setMaximum(0);

			labelFilter.setValue(0);
			labelFilter.setUpperValue(0);
		}

		// Initialize value display.
		labelFilterLowerValue.setText(String.valueOf(labelFilter.getValue()));
		labelFilterUpperValue.setText(String.valueOf(labelFilter.getUpperValue()));
		
		labelFilterLowerValue.setHorizontalAlignment(JLabel.LEFT);
		labelFilterUpperValue.setHorizontalAlignment(JLabel.RIGHT);

		labelFilter.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				labelFilterLowerValue.setText(String.valueOf(slider.getValue()));
				labelFilterUpperValue.setText(String.valueOf(slider.getUpperValue()));
				pointModel.setLowerFilterDegreeRange(slider.getValue());
				pointModel.setUpperFilterDegreeRange(slider.getUpperValue());
			}
		});
		
		labelGrid.gridy++;
		labelGrid.gridx = 0;
		labelGrid.gridwidth = 1;
		labelPanel.add(labelSizesLabel, labelGrid);
		
		labelGrid.gridx++;
		labelPanel.add(labelSizeCB, labelGrid);
		
		labelGrid.gridx++;
		labelPanel.add(labelFilterLabel, labelGrid);
		
		labelGrid.gridx++;
		labelPanel.add(labelFilterCB, labelGrid);
		
		labelGrid.gridy++;
		labelGrid.gridx = 0;
		labelGrid.gridwidth = 3;
		labelPanel.add(labelFilterLowerValue, labelGrid);

		labelGrid.gridx = 3;
		labelPanel.add(labelFilterUpperValue, labelGrid);

		labelGrid.gridy++;
		labelGrid.gridx = 0;
		labelGrid.gridwidth = GridBagConstraints.REMAINDER;
		labelPanel.add(labelFilter, labelGrid);
		
		viewOptionsPanel.add(labelPanel, viewOptionsGrid);
	}

	private void addEdgeFilterPanel(JPanel viewOptionsPanel,
			GridBagConstraints viewOptionsGrid) {

		JPanel filterPanel = new JPanel();
		filterPanel.setLayout(new GridBagLayout());
		GridBagConstraints filterGrid = new GridBagConstraints();

		filterGrid.fill = GridBagConstraints.BOTH;
		filterGrid.weightx = 1.0;
		filterGrid.gridy = 0;
		filterGrid.insets = new Insets(0, 0, 0, 0);

		TitledBorder border = BorderFactory.createTitledBorder(raisedetched, "Edge weight filter");
		filterPanel.setBorder(border);

		edgeWeightFilterCB = new JCheckBox("Filter edges according to their weight");
		edgeWeightFilterCB.setEnabled(false);
		edgeWeightFilterCB.setSelected(edgeModel.filterEdgesByWeight());
		edgeWeightFilterCB.addActionListener(this);

		final JLabel rangeSliderLowerValue = new JLabel();
		final JLabel rangeSliderUpperValue = new JLabel();

		RangeSlider rangeSlider = new RangeSlider();

		if (spModel.showGraph()) {
			rangeSlider.setMinimum((int) Math.floor(edgeModel.getMinEdgeWeight()));
			rangeSlider.setMaximum((int) Math.ceil(edgeModel.getMaxEdgeWeight()));

			rangeSlider.setValue((int) Math.floor(edgeModel.getMinEdgeWeight()));
			rangeSlider.setUpperValue((int) Math.ceil(edgeModel.getMaxEdgeWeight()));
		} else {
			rangeSlider.setMinimum(0);
			rangeSlider.setMaximum(0);

			rangeSlider.setValue(0);
			rangeSlider.setUpperValue(0);
		}

		// Initialize value display.
		rangeSliderLowerValue.setText(String.valueOf(rangeSlider.getValue()));
		rangeSliderUpperValue.setText(String.valueOf(rangeSlider.getUpperValue()));
		
		rangeSliderLowerValue.setHorizontalAlignment(JLabel.LEFT);
		rangeSliderUpperValue.setHorizontalAlignment(JLabel.RIGHT);

		rangeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				RangeSlider slider = (RangeSlider) e.getSource();
				rangeSliderLowerValue.setText(String.valueOf(slider.getValue()));
				rangeSliderUpperValue.setText(String.valueOf(slider.getUpperValue()));
				edgeModel.setLowerEdgeWeightRange(slider.getValue());
				edgeModel.setUpperEdgeWeightRange(slider.getUpperValue());
			}
		});

		filterGrid.gridy = 0;
		filterGrid.gridx = 0;
		filterPanel.add(edgeWeightFilterCB, filterGrid);
		
		filterGrid.gridy = 1;
		filterGrid.gridx = 0;
		filterPanel.add(rangeSliderLowerValue, filterGrid);

		filterGrid.gridx++;
		filterPanel.add(rangeSliderUpperValue, filterGrid);

		filterGrid.gridy = 2;
		filterGrid.gridx = 0;
		filterGrid.gridwidth = GridBagConstraints.REMAINDER;
		filterPanel.add(rangeSlider, filterGrid);

		viewOptionsPanel.add(filterPanel, viewOptionsGrid);

	}

	private void enableGraphButtons() {
		directedCheckBox.setEnabled(true);
		filterEdgeCheckBox.setEnabled(true);
		edgeWeightsCheckBox.setEnabled(true);
		edgeColorCombo.setEnabled(true);
		edgeShapeCombo.setEnabled(true);
//		noneRB.setEnabled(true);
//		sourceRB.setEnabled(true);
//		targetRB.setEnabled(true);
//		mixedRB.setEnabled(true);
//		straightRB.setEnabled(true);
//		curvedRB.setEnabled(true);
//		hideRB.setEnabled(true);
//		bundledRB.setEnabled(true);
//		fannedRB.setEnabled(true);
		incomingCB.setEnabled(true);
		outgoingCB.setEnabled(true);
		transparencySlider.setEnabled(true);
		graphSizeCombo.setEnabled(true);
		showLabelsCheckBox.setEnabled(true);
//		showHighlightedLabels.setEnabled(true);
//		showHoverLabels.setEnabled(true);
//		showSelectedLabels.setEnabled(true);
		nodeColorCB.setEnabled(true);
		labelsCombo.setEnabled(true);
		labelSlider.setEnabled(true);
		labelSizesLabel.setEnabled(true);
		labelSizeCB.setEnabled(true);
		labelFilterLabel.setEnabled(true);
		labelFilterCB.setEnabled(true);
		edgeWeightFilterCB.setEnabled(true);

	}

	private void selectGraphImportProperties() {

		String[] stringAttributes = new String[spModel.getStringAttributes()
				.size()];

		Vector<Attribute> vStringAttributes = spModel.getStringAttributes();
		for (int i = 0; i < vStringAttributes.size(); i++) {
			stringAttributes[i] = vStringAttributes.get(i).name();
		}

		GraphImportGUI graphImportGUI = new GraphImportGUI(this,
				stringAttributes);
		enableGraphButtons();

	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == loadGraphButton)
			try {
				selectGraphImportProperties();
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (event.getSource() == filterEdgeCheckBox) {
			edgeModel.setFilterAllEdges(filterEdgeCheckBox.isSelected());
		}

		if (event.getSource() == directedCheckBox) {
			edgeModel.setArrowedEdges(directedCheckBox.isSelected());
		}

		if (event.getSource() == edgeWeightsCheckBox) {
			edgeModel.setViewEdgeWeights(edgeWeightsCheckBox.isSelected());
		}
		
		if(event.getSource() == edgeColorCombo){
			String item = (String) edgeColorCombo.getSelectedItem();
				edgeModel.setDefaultColorEdges(item.equals("None"));
				edgeModel.setSourceColorEdges(item.equals("Source"));
				edgeModel.setTargetColorEdges(item.equals("Target"));
				edgeModel.setMixedColorEdges(item.equals("Mixed"));
		}	
		
		if(event.getSource() == edgeShapeCombo){
			String item = (String) edgeShapeCombo.getSelectedItem();
				edgeModel.setStraightEdges(item.equals("Straight"));
				edgeModel.setBezierEdges(item.equals("Curved"));
				edgeModel.setBundledEdges(item.equals("Bundled"));
				edgeModel.setFannedEdges(item.equals("Fanned"));
				edgeModel.setIntelligentEdges(item.equals("Intelligent"));
				spModel.setShowGraph(!item.equals("Hide"));
		}

		if (event.getSource() == incomingCB) {
			edgeModel.showIncomingEdges(incomingCB.isSelected());
		}

		if (event.getSource() == outgoingCB) {
			edgeModel.showOutgoingEdges(outgoingCB.isSelected());
		}

		if (event.getSource() == graphSizeCombo) {
			// sizeCombo.setSelectedIndex(0);
			pointModel.setSizeAttribute(null);
			int[] d = graphModel.setGraphSizeAttribute(graphSizeCombo.getSelectedIndex());
			graphModel.setNodeSize(d);
		}

		if (event.getSource() == showLabelsCheckBox) {
			pointModel.showLabels(showLabelsCheckBox.isSelected());
		}

		if (event.getSource() == nodeColorCB) {
			pointModel.showNodeLabelColor(nodeColorCB.isSelected());
		}

		if(event.getSource() == labelsCombo && showLabelsCheckBox.isSelected()){
			String item = (String) labelsCombo.getSelectedItem();
				pointModel.showHightlightedLabels(item.equals("Highlighted"));
				pointModel.showSelectedLabels(item.equals("Selected"));
				pointModel.showHoverLabels(item.equals("Hover"));
		}
		
		if (event.getSource() == labelSizesLabel && showLabelsCheckBox.isSelected()) {
			pointModel.setSizeLabels(labelSizesLabel.isSelected());
			int sizeBy = labelSizeCB.getSelectedIndex();
			int[] d = graphModel.setGraphSizeAttribute(sizeBy);
			graphModel.setLabelSize(d);
			pointModel.setSizeLabels(labelSizesLabel.isSelected());
		}
		
		if (event.getSource() == labelFilterLabel && showLabelsCheckBox.isSelected()) {
			pointModel.setFilterLabels(labelSizesLabel.isSelected());
			int filterBy = labelFilterCB.getSelectedIndex();
			int[] d = graphModel.setGraphSizeAttribute(filterBy);
			graphModel.setLabelFilter(d);
			pointModel.setFilterLabels(labelFilterLabel.isSelected());
			
		}

		if (event.getSource() == edgeWeightFilterCB) {
			edgeModel.setFilterEdgesByWeight(edgeWeightFilterCB.isSelected());
		}

	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		
		if(event.getStateChange() == ItemEvent.SELECTED) {
			if(event.getSource() == labelSizeCB){
				if(labelSizesLabel.isSelected() && showLabelsCheckBox.isSelected()){
					int[] d = graphModel.setGraphSizeAttribute(labelSizeCB.getSelectedIndex());
					graphModel.setLabelSize(d);
				}
			}
		}
			
		if(event.getStateChange() == ItemEvent.SELECTED) {
			if(event.getSource() == labelFilterCB) {
				if (labelFilterLabel.isSelected() && showLabelsCheckBox.isSelected()) {
					int[] d = graphModel.setGraphSizeAttribute(labelFilterCB.getSelectedIndex());
					graphModel.setLabelFilter(d);
					labelFilter.setMinimum((int) Math.floor(graphModel.getMinLabelFilterDegree()));
					labelFilter.setMaximum((int) Math.ceil(graphModel.getMaxLabelFilterDegree()));
					labelFilter.setValue((int) Math.floor(graphModel.getMinLabelFilterDegree()));
					labelFilter.setUpperValue((int) Math.ceil(graphModel.getMaxLabelFilterDegree()));
					labelFilterLowerValue.setText(String.valueOf(labelFilter.getValue()));
					labelFilterUpperValue.setText(String.valueOf(labelFilter.getUpperValue()));
				}
			}
		}
	}

	/** Marker slider state has changed */
	public void stateChanged(ChangeEvent e) {

		if (transparencySlider == (JSlider) e.getSource())
			spModel.setTransparencyLevel(transparencySlider.getValue());

		// if (beizerSlider == (JSlider) e.getSource())
		// spModel.setBeizerCurviness((float)beizerSlider.getValue()/100f);

		if (labelSlider == (JSlider) e.getSource())
			pointModel.setLabelSize(labelSlider.getValue() / 100d);
	}

}