package org.dussan.vaadin.dcharts;

import org.dussan.vaadin.dcharts.base.elements.XYaxis;
import org.dussan.vaadin.dcharts.base.elements.XYseries;
import org.dussan.vaadin.dcharts.base.renderers.MarkerRenderer;
import org.dussan.vaadin.dcharts.data.DataSeries;
import org.dussan.vaadin.dcharts.metadata.XYaxes;
import org.dussan.vaadin.dcharts.metadata.Yaxes;
import org.dussan.vaadin.dcharts.metadata.styles.MarkerStyles;
import org.dussan.vaadin.dcharts.options.Axes;
import org.dussan.vaadin.dcharts.options.AxesDefaults;
import org.dussan.vaadin.dcharts.options.Options;
import org.dussan.vaadin.dcharts.options.Series;
import org.dussan.vaadin.dcharts.renderers.axis.LinearAxisRenderer;

import com.vaadin.ui.Panel;

public class DemoDChart09 extends Panel {

	public DemoDChart09() {
		setImmediate(true);
		
		DataSeries dataSeries = new DataSeries();
		dataSeries.newSeries();
		for (float i = 0; i < 2 * Math.PI; i += 0.4) {
			dataSeries.add(i, Math.cos(i));
		}
		dataSeries.newSeries();
		for (float i = 0; i < 2 * Math.PI; i += 0.4) {
			dataSeries.add(i, Math.sin(i - 0.8));
		}
		dataSeries.newSeries();
		for (float i = 0; i < 2 * Math.PI; i += 0.4) {
			dataSeries.add(i, 2.5 + Math.pow(i / 4, 2));
		}
		dataSeries.newSeries();
		for (float i = 0; i < 2 * Math.PI; i += 0.4) {
			dataSeries.add(i, -2.5 - Math.pow(i / 4, 2));
		}

		Series series = new Series()
			.addSeries(
				new XYseries()
					.setLineWidth(2)
					.setMarkerOptions(
						new MarkerRenderer()
							.setStyle(MarkerStyles.DIAMOND)))
			.addSeries(
				new XYseries(Yaxes.Y2).
					setShowLine(false)
					.setMarkerOptions(
						new MarkerRenderer()
							.setSize(7)
							.setStyle(MarkerStyles.X)))
			.addSeries(
				new XYseries(Yaxes.Y3)
					.setMarkerOptions(
						new MarkerRenderer()
							.setStyle(MarkerStyles.CIRCLE)))
			.addSeries(
				new XYseries(Yaxes.Y4)
					.setLineWidth(5)
					.setMarkerOptions(
						new MarkerRenderer()
							.setStyle(MarkerStyles.FILLED_SQUARE)
							.setSize(10)));

		AxesDefaults axesDefaults = new AxesDefaults()
			.setUseSeriesColor(true)
			.setRendererOptions(
				new LinearAxisRenderer()
					.setAlignTicks(true));

		Axes axes = new Axes()
			.addAxis(
				new XYaxis(XYaxes.Y))
			.addAxis(
				new XYaxis(XYaxes.Y2))
			.addAxis(
				new XYaxis(XYaxes.Y3))
			.addAxis(
				new XYaxis(XYaxes.Y4));

		Options options = new Options()
			.setSeries(series)
			.setAxesDefaults(axesDefaults)
			.setAxes(axes);

		DCharts chart = new DCharts()
			.setDataSeries(dataSeries)
			.setOptions(options)
			.show();
		
		setContent(chart);
	}
}
