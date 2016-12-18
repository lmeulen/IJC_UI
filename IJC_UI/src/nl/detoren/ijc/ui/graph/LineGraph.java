package nl.detoren.ijc.ui.graph;

import java.awt.BasicStroke;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;

public class LineGraph extends AbstractGraph{
	private static final long serialVersionUID = 1L;

	public LineGraph(String title, String labelX, String labelY, boolean legend) {
		super(title, labelX, labelY, legend);
	}

	@Override
	protected void createChart() {
		chart = ChartFactory.createLineChart(title, labelX, labelY, (CategoryDataset) dataset, PlotOrientation.VERTICAL,
				legend, true, false);
		CategoryPlot plot = chart.getCategoryPlot();

		LineAndShapeRenderer renderer = new LineAndShapeRenderer();
		for (int i = 0; i < ((CategoryDataset)dataset).getRowCount(); i++) {
			renderer.setSeriesStroke(i, new BasicStroke(3.0f));
		}
		plot.setRenderer(renderer);

		setRange(plot);
	}

	@Override
	protected void setRange(Plot plot) {
		if (plot instanceof CategoryPlot) {
			CategoryPlot p = (CategoryPlot) plot;
			CategoryDataset data = p.getDataset();
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
