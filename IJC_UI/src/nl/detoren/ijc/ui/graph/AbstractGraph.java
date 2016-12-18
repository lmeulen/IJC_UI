package nl.detoren.ijc.ui.graph;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;

public abstract class AbstractGraph extends JPanel {

	private static final long serialVersionUID = 1L;
	protected ChartPanel internalPanel;
	protected JFreeChart chart;
	protected String title;
	protected String labelX;
	protected String labelY;
	protected boolean legend;
	protected Dataset dataset;

	public AbstractGraph() {
		super();
	}

	public AbstractGraph(String title, String labelX, String labelY, boolean legend) {
		super();
		this.title = title;
		this.labelX = labelX;
		this.labelY = labelY;
		this.legend = legend;
		dataset = null;
	}

	public void initialize(Dataset set) {
		this.dataset = set;
		setLayout(new java.awt.BorderLayout());
		createChart();
		this.internalPanel = new ChartPanel(chart);
		add(internalPanel, BorderLayout.CENTER);
		internalPanel.validate();
	}

	protected abstract void createChart();

	protected abstract void setRange(Plot plot);

}