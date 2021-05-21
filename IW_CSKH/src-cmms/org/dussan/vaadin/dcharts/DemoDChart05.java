package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.base.elements.PointLabels;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.directions.BarDirections;
import org.dussan.vaadin.dcharts.metadata.locations.PointLabelLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.series.BarRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart05 extends Panel {

	public DemoDChart05() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries();
		dataSeries.newSeries()
			.add(2, 1)
			.add(4, 2)
			.add(6, 3)
			.add(3, 4);
		dataSeries.newSeries()
			.add(5, 1)
			.add(1, 2)
			.add(3, 3)
			.add(4, 4);
		dataSeries.newSeries()
			.add(4, 1)
			.add(7, 2)
			.add(1, 3)
			.add(2, 4);

		SeriesDefaults seriesDefaults = new SeriesDefaults()
			.setRenderer(SeriesRenderers.BAR)
			.setPointLabels(
				new PointLabels()
					.setShow(true)
					.setLocation(PointLabelLocations.EAST)
					.setEdgeTolerance(-15))
			.setShadowAngle(135)
			.setRendererOptions(
				new BarRenderer()
					.setBarDirection(BarDirections.HOTIZONTAL));

		Axes axes = new Axes()
			.addAxis(
				new XYaxis(XYaxes.Y)
					.setRenderer(AxisRenderers.CATEGORY));

		Options options = new Options()
			.setSeriesDefaults(seriesDefaults)
			.setAxes(axes);

		DCharts chart = new DCharts()
			.setDataSeries(dataSeries)
			.setOptions(options)
			.show();
		
		setContent(chart);
	}
}
