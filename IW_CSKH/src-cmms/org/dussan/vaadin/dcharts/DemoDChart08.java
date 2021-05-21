package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.metadata.DataLabels;
import org.dussan.vaadin.dcharts.metadata.LegendPlacements;
import org.dussan.vaadin.dcharts.metadata.locations.LegendLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Legend;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.series.DonutRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart08 extends Panel {

	public DemoDChart08() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries();
		dataSeries.newSeries()
			.add("a", 6)
			.add("b", 8)
			.add("c", 14)
			.add("d", 20);
		dataSeries.newSeries()
			.add("a", 8)
			.add("b", 12)
			.add("c", 6)
			.add("d", 9);

		SeriesDefaults seriesDefaults = new SeriesDefaults()
			.setRenderer(SeriesRenderers.DONUT)
			.setRendererOptions(
				new DonutRenderer()
					.setSliceMargin(3)
					.setStartAngle(-90))
					.setShowLabel(true)
					.setLabel(DataLabels.VALUE.name());

		Legend legend = new Legend()
			.setShow(true)
			.setPlacement(LegendPlacements.OUTSIDE_GRID)
			.setLocation(LegendLocations.WEST);

		Highlighter highlighter = new Highlighter()
			.setShow(true)
			.setShowTooltip(true)
			.setTooltipAlwaysVisible(true)
			.setKeepTooltipInsideChart(true);

		Options options = new Options()
			.setSeriesDefaults(seriesDefaults)
			.setLegend(legend)
			.setHighlighter(highlighter);

		DCharts chart = new DCharts()
			.setDataSeries(dataSeries)
			.setOptions(options)
			.show();
		
		setContent(chart);
	}
}
