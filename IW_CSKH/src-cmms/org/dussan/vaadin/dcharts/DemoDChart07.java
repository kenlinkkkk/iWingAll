package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.metadata.SeriesToggles;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.renderers.LegendRenderers;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.Legend;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.Series;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.legend.EnhancedLegendRenderer;
import org.dussan.vaadin.dcharts.renderers.series.BlockRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart07 extends Panel {

	public DemoDChart07() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries()
		.newSeries()
			.add(0.9, 120, "Vernors")
			.add(1.8, 140, "Fanta")
			.add(3.2, 90, "Barqs", "{background:'#ddbb33'}")
			.add(4.1, 140, "Arizon Iced Tea")
			.add(4.5, 91, "Red Bull")
			.add(6.8, 17, "Go Girl")
		.newSeries()
			.add(1.3, 44, "Pepsi")
			.add(2.1, 170, "Sierra Mist")
			.add(2.6, 66, "Moutain Dew")
			.add(4, 52, "Sobe")
			.add(5.4, 16, "Amp")
			.add(6, 48, "Aquafina")
		.newSeries()
			.add(1, 59, "Coca-Cola", "{background:'rgb(250, 160, 160)'}")
			.add(2, 50, "Ambasa")
			.add(3, 90, "Mello Yello")
			.add(4, 90, "Sprite")
			.add(5, 71, "Squirt")
			.add(5, 155, "Youki");

	SeriesDefaults seriesDefaults = new SeriesDefaults()
		.setRenderer(SeriesRenderers.BLOCK);

	Legend legend = new Legend()
		.setShow(true)
		.setRenderer(LegendRenderers.ENHANCED)
		.setRendererOptions(
			new EnhancedLegendRenderer()
				.setSeriesToggle(SeriesToggles.SLOW)
				.setSeriesToggleReplot(false));

	Axes axes = new Axes()
		.addAxis(
			new XYaxis(XYaxes.X)
				.setMin(0)
				.setMax(8))
		.addAxis(
			new XYaxis(XYaxes.Y)
				.setMin(0)
				.setMax(200));

	Series series = new Series()
		.addSeries(new XYseries())
		.addSeries(
			new XYseries()
				.setRenderer(SeriesRenderers.BLOCK)
				.setRendererOptions(
					new BlockRenderer()
						.setCss("background:'#A1EED6'")))
		.addSeries(
			new XYseries()
				.setRenderer(SeriesRenderers.BLOCK)
				.setRendererOptions(
					new BlockRenderer()
						.setCss("background:'#D3E4A0'")));

	Options options = new Options()
		.setSeriesDefaults(seriesDefaults)
		.setLegend(legend)
		.setAxes(axes)
		.setSeries(series);

	DCharts chart = new DCharts()
		.setDataSeries(dataSeries)
		.setOptions(options)
		.setEnableDownload(true)
		.setChartImageFormat(ChartImageFormat.GIF)
		.show();
		
		setContent(chart);
	}
}
