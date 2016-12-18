package nl.detoren.ijc.ui.graph;

import java.awt.BasicStroke;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYDataset;


/**
 * Usage:
 *  XYGraph g = new XYGraph("titel", "X", "Y", true);
 *  g.initialize(dataset);
 * @author Leo.vanderMeulen
 *
 */
public class XYGraph extends AbstractGraph {
	private static final long serialVersionUID = 1L;

	public XYGraph(String title, String labelX, String labelY, boolean legend) {
		super(title, labelX, labelY, legend);
	}

	protected void createChart() {
		chart = ChartFactory.createXYLineChart(title, labelX, labelY, (XYDataset) dataset, PlotOrientation.VERTICAL,
				legend, true, false);
		XYPlot plot = chart.getXYPlot();

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		for (int i = 0; i < ((XYDataset)dataset).getSeriesCount(); i++) {
			renderer.setSeriesStroke(i, new BasicStroke(3.0f));
		}
		plot.setRenderer(renderer);

		setRange(plot);
	}

	protected void setRange(Plot plot) {
		if (plot instanceof XYPlot) {
			XYPlot p = (XYPlot) plot;
			XYDataset data = p.getDataset();
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			min = DatasetUtilities.findMinimumRangeValue(data).intValue();
			max = DatasetUtilities.findMaximumRangeValue(data).intValue();
			min = (min / 100) * 100;
			max = ((max / 100) + 1) * 100;
			NumberAxis range = (NumberAxis) p.getRangeAxis();
			range.setRange(min, max);
		}
	}

}
