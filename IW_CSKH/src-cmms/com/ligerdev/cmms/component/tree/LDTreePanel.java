package com.ligerdev.cmms.component.tree;

import org.apache.log4j.Logger;

import com.ligerdev.appbase.utils.textbase.Log4jLoader;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

public class LDTreePanel extends VerticalLayout {
	
	protected static Logger logger = Log4jLoader.getLogger();
	private ComboBox cboSearch = new ComboBox();
	private Button btnSearch = new Button();
	
	private ITreeListener pnTreeProvider;
	private Tree tree;

	public LDTreePanel(Tree tree, ITreeListener treeprovider) {
		setSizeFull();
		setSpacing(false);
		setSizeUndefined();
		setWidth("100%");
		// setMargin(false);
		
		this.tree = tree;
		this.pnTreeProvider = treeprovider;
		this.cboSearch.setFilteringMode(FilteringMode.CONTAINS);
		this.cboSearch.addStyleName("small");
		
		Panel panel = new Panel();
		panel.setSizeFull();
		// setMargin(false);
		
		HorizontalLayout horizon = new HorizontalLayout();
		horizon.setHeightUndefined();
		horizon.setWidth("100%");
		horizon.setSpacing(true);
		
		this.cboSearch.setWidth("100%");
		this.cboSearch.setNullSelectionAllowed(false);
		horizon.addComponent(this.cboSearch);
		horizon.setExpandRatio(this.cboSearch, 1.0F);
		// horizon.setComponentAlignment(this.cboSearch, Alignment.MIDDLE_LEFT);
		
		horizon.addComponent(this.btnSearch);
		horizon.setComponentAlignment(this.btnSearch, Alignment.MIDDLE_RIGHT);
		
		this.btnSearch.setStyleName("link");
		this.btnSearch.setIcon(new ThemeResource("icons/16/search.png"));
		this.btnSearch.setDescription("Search...");
		
		this.btnSearch.addClickListener(new ClickListener() {
			public void buttonClick(Button.ClickEvent event) {
				// String s = BaseUtils.getObjectInfo("", LDTreePanel.this.cboSearch);
				// logger.info(s);
				LDTreePanel.this.filterTree(LDTreePanel.this.cboSearch.getValue());
			}
		});
		panel.setContent(horizon);
		
		Label lbl = new Label();
		lbl.setHeight("3px");
		addComponent(lbl);
		
		addComponent(panel);
		// setExpandRatio(panel, 1.0F);
		
		addComponent(tree);
		// setComponentAlignment(tree, Alignment.TOP_LEFT);
		initComponentLeft();
	}

	public void addItemToCombobox(Object item, String caption, ThemeResource icon){
		this.cboSearch.addItem(item);
		this.cboSearch.setItemCaption(item, caption);
		if(icon != null){
			this.cboSearch.setItemIcon(item, icon);
		}
	}

	private void initComponentLeft() {
		this.cboSearch.setInputPrompt("search ...");
		this.cboSearch.setImmediate(true);
		this.cboSearch.setScrollToSelectedItem(true);
		// this.cboSearch.setDescription("");
		
		this.cboSearch.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				LDTreePanel.this.filterTree(LDTreePanel.this.cboSearch.getValue());
			}
		});
		
		this.tree.setImmediate(true);
		this.tree.setNullSelectionAllowed(false);
		this.tree.addValueChangeListener(new ValueChangeListener() {
			public void valueChange(ValueChangeEvent event) {
				LDTreePanel.this.treeValueChanged(LDTreePanel.this.tree.getValue());
			}
		});
		
		this.tree.addItemClickListener(new ItemClickListener() {
			public void itemClick(ItemClickEvent event) {
				Object item = event.getItemId();
				if (event.isDoubleClick()) {
					if (LDTreePanel.this.tree.isExpanded(item)) {
						LDTreePanel.this.tree.collapseItem(event.getItemId());
					} else {
						LDTreePanel.this.tree.expandItem(event.getItemId());
					}
				}
			}
		});
	}

	public void setButtonSearchTooltip(String tooltip) {
		this.btnSearch.setDescription(tooltip);
	}

	public void setComboBoxSearchTooltip(String tooltip) {
		this.cboSearch.setDescription(tooltip);
	}

	public void setComBoxSearchInputPrompt(String inputPrompt) {
		this.cboSearch.setInputPrompt(inputPrompt);
	}

	private void filterTree(Object obj) {
		if (obj == null) {
			return;
		}
		this.pnTreeProvider.filterTree(obj);
	}

	private void treeValueChanged(Object obj) {
		if (obj == null) {
			return;
		}
		this.pnTreeProvider.treeValueChanged(obj);
	}
}
