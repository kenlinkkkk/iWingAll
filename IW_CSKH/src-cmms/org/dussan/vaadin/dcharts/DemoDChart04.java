package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.events.click.ChartDataClickEvent;
import org.dussan.vaadin.dcharts.events.click.ChartDataClickHandler;
import org.dussan.vaadin.dcharts.events.mouseenter.ChartDataMouseEnterEvent;
import org.dussan.vaadin.dcharts.events.mouseenter.ChartDataMouseEnterHandler;
import org.dussan.vaadin.dcharts.events.mouseleave.ChartDataMouseLeaveEvent;
import org.dussan.vaadin.dcharts.events.mouseleave.ChartDataMouseLeaveHandler;
import org.dussan.vaadin.dcharts.events.rightclick.ChartDataRightClickEvent;
import org.dussan.vaadin.dcharts.events.rightclick.ChartDataRightClickHandler;
import org.dussan.vaadin.dcharts.metadata.renderers.SeriesRenderers;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.SeriesDefaults;
import org.dussan.vaadin.dcharts.renderers.series.PieRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart04 extends Panel {

	public DemoDChart04() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries()
		.newSeries()
		.add("none", 23)
		.add("error", 0)
		.add("click", 5)
		.add("impression", 25);

	SeriesDefaults seriesDefaults = new SeriesDefaults()
		.setRenderer(SeriesRenderers.PIE)
		.setRendererOptions(
			new PieRenderer()
				.setShowDataLabels(true));

	Options options = new Options()
		.setCaptureRightClick(true)
		.setSeriesDefaults(seriesDefaults);

	DCharts chart = new DCharts();

	chart.setEnableChartDataMouseEnterEvent(true);
	chart.setEnableChartDataMouseLeaveEvent(true);
	chart.setEnableChartDataClickEvent(true);
	chart.setEnableChartDataRightClickEvent(true);

	chart.addHandler(new ChartDataMouseEnterHandler() {
		@Override
		public void onChartDataMouseEnter(ChartDataMouseEnterEvent event) {
			// showNotification("CHART DATA MOUSE ENTER", event.getChartData());
		}
	});

	chart.addHandler(new ChartDataMouseLeaveHandler() {
		@Override
		public void onChartDataMouseLeave(ChartDataMouseLeaveEvent event) {
			// showNotification("CHART DATA MOUSE LEAVE", event.getChartData());
		}
	});

	chart.addHandler(new ChartDataClickHandler() {
		@Override
		public void onChartDataClick(ChartDataClickEvent event) {
			// showNotification("CHART DATA CLICK", event.getChartData());
		}
	});

	chart.addHandler(new ChartDataRightClickHandler() {
		@Override
		public void onChartDataRightClick(ChartDataRightClickEvent event) {
			// showNotification("CHART DATA RIGHT CLICK", event.getChartData());
		}
	});

	chart.setDataSeries(dataSeries)
		.setOptions(options)
		.show();
		
		setContent(chart);
	}
}
