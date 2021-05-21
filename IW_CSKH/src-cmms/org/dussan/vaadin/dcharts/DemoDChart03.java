package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.LegendPlacements;
import org.dussan.vaadin.dcharts.metadata.SeriesToggles;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Legend;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.Series;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.legend.EnhancedLegendRenderer;
import org.dussan.vaadin.dcharts.renderers.tick.AxisTickRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart03 extends Panel {

	public DemoDChart03() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries();
		dataSeries.add(200, 600, 700, 1000);
		dataSeries.add(460, -210, 690, 820);
		dataSeries.add(-260, -440, 320, 200);

		SeriesDefaults seriesDefaults = new SeriesDefaults()
			.setFillToZero(true)
			.setRenderer(SeriesRenderers.BAR);

		Series series = new Series()
			.addSeries(
				new XYseries()
					.setLabel("Hotel"))
			.addSeries(
				new XYseries()
					.setLabel("Event Regristration"))
			.addSeries(
				new XYseries()
					.setLabel("Airfare"));

		Legend legend = new Legend()
			.setShow(true)
			.setRendererOptions(
				new EnhancedLegendRenderer()
					.setSeriesToggle(SeriesToggles.SLOW)
					.setSeriesToggleReplot(true))
			.setPlacement(LegendPlacements.OUTSIDE_GRID);

		Axes axes = new Axes()
			.addAxis(
				new XYaxis()
					.setRenderer(AxisRenderers.CATEGORY)
					.setTicks(
						new Ticks()
							.add("May", "June", "July", "August")))
			.addAxis(
				new XYaxis(XYaxes.Y)
					.setPad(1.05f)
					.setTickOptions(
						new AxisTickRenderer()
							.setFormatString("$%d")));

		Options options = new Options()
			.setSeriesDefaults(seriesDefaults)
			.setSeries(series)
			.setLegend(legend)
			.setAxes(axes);

		DCharts chart = new DCharts()
			.setDataSeries(dataSeries)
			.setOptions(options)
			.show();
		
		setContent(chart);
	}
}
