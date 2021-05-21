package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.base.elements.PointLabels;
import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.data.Ticks;
import org.dussan.vaadin.dcharts.metadata.TooltipAxes;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.Yaxes;
import org.dussan.vaadin.dcharts.metadata.locations.TooltipLocations;
import org.dussan.vaadin.dcharts.metadata.renderers.AxisRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Highlighter;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.options.Title;
import org.dussan.vaadin.dcharts.renderers.series.BarRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart01 extends Panel {

	public DemoDChart01() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries()
		.add(14, 3, 4, -3, 5, 2, -3, -7);

	SeriesDefaults seriesDefaults = new SeriesDefaults()
		.setRenderer(SeriesRenderers.BAR)
		.setRendererOptions(
			new BarRenderer()
				.setWaterfall(true)
				.setVaryBarColor(true))
		.setPointLabels(
			new PointLabels()
				.setHideZeros(true))
		.setYaxis(Yaxes.Y2);

	Highlighter highlighter = new Highlighter()
		.setShow(true)
		.setShowTooltip(true)
		.setTooltipAlwaysVisible(true)
		.setKeepTooltipInsideChart(true)
		.setTooltipLocation(TooltipLocations.NORTH)
		.setTooltipAxes(TooltipAxes.XY_BAR);

	Axes axes = new Axes()
		.addAxis(
			new XYaxis()
				.setRenderer(AxisRenderers.CATEGORY)
				.setTicks(
					new Ticks()
						.add("2008", "Apricots", "Tomatoes", "Potatoes", "Rhubarb", "Squash", "Grapes", "Peanuts", "2009")))
//				.setTickRenderer(TickRenderers.CANVAS)
//				.setTickOptions(
//					new CanvasAxisTickRenderer()
//						.setAngle(-90)
//						.setFontSize("10pt")
//						.setShowMark(false)
//						.setShowGridline(false))
		.addAxis(
			new XYaxis(XYaxes.Y2)
				.setMin(0)
				.setTickInterval(5));

		Title title = new Title("Crop Yield Charnge, 2008 to 2009");
	
		Options options = new Options()
			.setSeriesDefaults(seriesDefaults)
			.setHighlighter(highlighter)
			.setAxes(axes)
			.setTitle(title);
	
		DCharts chart = new DCharts()
			.setDataSeries(dataSeries)
			.setOptions(options);
		
		chart.setCaption("Crop Yield Charnge, 2008 to 2009");
		chart.show();
		
		setContent(chart);
	}
}
