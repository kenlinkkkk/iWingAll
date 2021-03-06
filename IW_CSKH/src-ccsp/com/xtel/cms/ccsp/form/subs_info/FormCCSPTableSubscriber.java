package com.xtel.cms.ccsp.form.subs_info;

import java.util.ArrayList;
import java.util.Date;

import com.ibm.icu.text.SimpleDateFormat;
import com.ligerdev.appbase.utils.BaseUtils;
import com.ligerdev.appbase.utils.http.HttpClientUtils;
import com.ligerdev.cmms.component.AppCmmsUtils;
import com.ligerdev.cmms.component.dialog.IOptionDialogListener;
import com.ligerdev.cmms.component.dialog.LDDelConfirmDialog;
import com.ligerdev.cmms.component.dialog.LDInputDialog;
import com.ligerdev.cmms.component.table.DialogSubmitResult;
import com.ligerdev.cmms.component.table.LDTableSimple;
import com.ligerdev.cmms.component.table.XGUI_TableLayout;
import com.ligerdev.cmms.component.table.TableHeader;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.xtel.cms.ccsp.CCSPAppUtils;
import com.xtel.cms.ccsp.db.orm.CCSPPkgPolicy;
import com.xtel.cms.ccsp.db.orm.CCSPSubscriber;
import com.xtel.cms.utils.AppUtils;
import com.xtel.cms.utils.XDataBean;

public class FormCCSPTableSubscriber extends LDTableSimple<CCSPSubscriber>{
	
	private FormCCSPLayoutSubsInfo formMultiComponent;
	private String urlUnregTemplate = null;
	private String serviceName;
	private int totalFound = 0;
	private XDataBean xdata;
	
	public FormCCSPTableSubscriber(XDataBean xdata) {
		this.baseDAO = xdata.baseDAO;
		this.xbaseDAO = xdata.xbaseDAO;
		this.serviceName = xdata.serviceName;
		this.urlUnregTemplate = xdata.urlUnregTemplate;
		this.xdata = xdata;
		logger.info(mainApp.getTransid() + ", handler class: " + this.getClass().getName() + ", serviceName: " + serviceName); 
	}
	
	@Override
	public void getPage(final int pageIndex, final boolean showNotify, final String filter) {
		logger.info(mainApp.getTransid() + ", =====> pageIndex: " + pageIndex + ", showNtf: " + showNotify + ", filter: " + filter);
		int index = (pageIndex - 1) * AppCmmsUtils.getPageSize();
		String msisdn = formMultiComponent.msisdnBox.getValue();
		
		if(msisdn != null) {
			msisdn = msisdn.replace(" ", "").replace("'", "").replace("\"", "");
			msisdn = BaseUtils.formatMsisdn(msisdn, "84", "84");
		}
		String newFormat = BaseUtils.getOtherFormat(msisdn);
		String sql = "select * from subscriber where msisdn in ('" + msisdn + "', '" + newFormat + "') order by active_time";	// + AppCmmsUtils.getFilterRestrictionSql(filter, null, "msisdn"); 
		totalFound = AppCmmsUtils.countRecord(sql, xbaseDAO);
		ArrayList<CCSPSubscriber> listSub = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPSubscriber.class, sql, index, AppCmmsUtils.getPageSize());
		
		Button.ClickListener click = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// mod 20200514 , chuy???n code sang h??m clickUnreg
				clickUnreg(event, pageIndex, showNotify, filter, true); 
			}
		};
		// mod 20200514: th??m
		Button.ClickListener click2 = new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				clickUnreg(event, pageIndex, showNotify, filter, false); 
			}
		};
		Integer countPkg = null;
		
		if(listSub != null && listSub.size() > 0) {
			countPkg = 0;
			
			// ----------------------------
			sql = "select * from pkg_policy where status = 1";
			ArrayList<CCSPPkgPolicy> listCCSPPkgPolicy = xbaseDAO.getListBySql(mainApp.getTransid(), CCSPPkgPolicy.class, sql, null, null);
			
			if(listCCSPPkgPolicy != null && listCCSPPkgPolicy.size() > 0) {
				for(CCSPSubscriber s: listSub) {
					CCSPPkgPolicy p = CCSPAppUtils.getPkgPolicy(listCCSPPkgPolicy, s.getPackageId());
					if(p == null) {
						continue;
					}
					s.setFeeDesc(p.getFeeDesc()); 
					s.setPkgDesc(p.getDesc()); 
					
					if(AppUtils.isCatTienSaPlayService()) {
						s.setScoreBuy(s.getNoteint1());
						int score4renew = 0;
						if("CAT1".equalsIgnoreCase(s.getPackageId()) || "C1".equalsIgnoreCase(s.getPackageId())){
							score4renew = 30;
						} else if("CAT7".equalsIgnoreCase(s.getPackageId()) || "C7".equalsIgnoreCase(s.getPackageId())){
							score4renew = 100;
						} else if("CAT30".equalsIgnoreCase(s.getPackageId()) || "C30".equalsIgnoreCase(s.getPackageId())){
							score4renew = 300;
						}
						int scoreRenew = BaseUtils.parseInt(s.getSubnote2(), 0) * score4renew;   
						s.setScoreRenew(scoreRenew);
						
						int total = scoreRenew + s.getNoteint1() - s.getNoteint2();
						total = total < 0 ? 0 : total;
						
						s.setScoreTotal(total);
						s.setScoreUse(s.getNoteint2());
					} 
					if(s.getStatus() == 1) {
						Button button = new Button("H???y");
						button.setData(s); 
						button.addClickListener(click); 
						s.setButton(button); 
						
						// mod 20200514 ( th??m button2 ??? class CCSPSubscriber)
						Button button2 = new Button("H???y silent");
						button2.setData(s); 
						button2.addClickListener(click2); 
						s.setButton2(button2); 
						countPkg ++;
					}
					if(String.valueOf(serviceName).toLowerCase().contains("amobi")) {
						s.setSubService(p.getNote1()); 
					}
				} 
			}
		}
		BeanItemContainer<CCSPSubscriber> data = new BeanItemContainer<CCSPSubscriber>(CCSPSubscriber.class, listSub);
		tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, serviceName + " Th??ng tin thu?? bao", data, this, this, filter, true, null);
		
		/*XClickListener clickListener = new XClickListener() {
			@Override
			public void click(BeanItem<?> bean) {
				logger.info(mainApp.getTransid() + ", clicked: " + bean.getItemProperty("msisdn")); 
			}
		};*/
		// manual set visible columns 
		ArrayList<TableHeader> columns = new ArrayList<>();
		columns.add(new TableHeader("msisdn", "S??? ??T"));
		columns.add(new TableHeader("status", "Active", null, "1").setShowIconX(true));
		columns.add(new TableHeader("activeTime", "Ng??y ??K", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("expireTime", "H???n s??? d???ng", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("activeChannel", "K??nh ??K", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("cpid", "M?? CPID"));
		columns.add(new TableHeader("deactiveTime", "Ng??y H???y", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("deactiveChannel", "K??nh H???y", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("packageId", "M?? G??i"));
		columns.add(new TableHeader("pkgDesc", "T??n G??i"));
		
		if(String.valueOf(serviceName).toLowerCase().contains("amobi")) {
			columns.add(new TableHeader("subService", "D???ch V???"));
		}
		if(AppUtils.isCatTienSaPlayService()) {
			columns.add(new TableHeader("scoreRenew", "??i???m gia h???n"));
			columns.add(new TableHeader("scoreBuy", "??i???m mua th??m"));
			columns.add(new TableHeader("scoreUse", "??i???m ???? d??ng"));
			columns.add(new TableHeader("scoreTotal", "T???ng ??i???m"));
		}
		columns.add(new TableHeader("feeDesc", "Gi?? G??i"));
		
		// mod 20200514: s???a t??n c???t c??, th??m c???t m???i
		columns.add(new TableHeader("button", "H???y"));
		columns.add(new TableHeader("button2", "H???y silent"));
		
		tableContainer.getTable().addVisibleColumn(columns);
		if(countPkg == null) {
			String str = " - Kh??ng active d???ch v???."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			tableContainer.addComponent(title);
			
		} else if(countPkg == 0) {
			String str = " - ???? h???y d???ch v???."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			tableContainer.addComponent(title);
		} else {
			String str = " - Active " + countPkg + " g??i."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			tableContainer.addComponent(title);
		}
		setContent(tableContainer);
	}

	// mod 20200514: b???c t??? tr??n xu???ng
	public void clickUnreg(ClickEvent event, final int pageIndex, final boolean showNotify, final String filter, final boolean sendSms) {
		final CCSPSubscriber subs = (CCSPSubscriber) event.getButton().getData();
		
		new LDDelConfirmDialog(null, new IOptionDialogListener() {
			@Override
			public void dialogOptionClicked(String buttonName, int buttonIndex) {
				logger.info(mainApp.getTransid() + ", ==> call unregister: " + subs.getMsisdn() + ", pkg: " + subs.getPackageId());
				
				if(mainApp.getUserBean() != null && mainApp.getUserBean().getMaxUnreg() != null && mainApp.getUserBean().getMaxUnreg() >= 0) {
					if(xdata.isCCSPService == false) { // ccsp ko c?? c???t username n??n t???m b??? qua b?????c check n??y
						// check xem qu?? s??? l?????ng ch??a
						String sql = "select count(id) from his_" + new SimpleDateFormat("yyyyMM").format(new Date())  
										+ " where action in ('UNREG', 'DELETE') and created_time >= date(now()) and username = '" 
										+ mainApp.getUserBean().getUsername() + "' and result = 0";
						
						Integer count = xbaseDAO.getFirstCell(mainApp.getTransid(), sql, Integer.class);
						
						if(count != null && count >= mainApp.getUserBean().getMaxUnreg()) {
							AppUtils.sendAlarm(mainApp.getTransid(), "Web CSKH qu?? s??? l?????t h???y c???u h??nh: " + count);
							Notification.show("H??? th???ng b???n, vui l??ng th??? l???i sau", Type.ERROR_MESSAGE);
							return;
						} 
					}
				}
				String url = 
						urlUnregTemplate.replace("@pkgcode", subs.getPackageId())
						.replace("@msisdn", subs.getMsisdn())
						.replace("@user", mainApp.getUserBean().getUsername() + ""); 
				
				// mod 20200514: th??m &amp;transid=@transid ??? url h???y trong config
				if(sendSms == false) {
					url = url.replace("@transid", "WI" + mainApp.getTransid());
				} else {
					url = url.replace("@transid", mainApp.getTransid());
				}
				logger.info(mainApp.getTransid() + ", URL unregister: " + url); 
				String rs = HttpClientUtils.getDefault(mainApp.getTransid(), url, 22000) + "";
				rs = BaseUtils.unescapeHtml(rs);
				boolean b1 = rs.contains("_SUCC") && rs.contains("NOT_SUCC") == false;
				boolean b2 = rs.startsWith("0|") || rs.startsWith("1|");
				boolean b3 = rs.contains("NOT_SUCC") && rs.contains("ch??a ????ng k??");
				logger.info(mainApp.getTransid() + ", UnregRS: " + rs + " | rs_b1: " + b1 + ", b2: " + b2 + ", b3: " + b3);  
				
				if(b1 || b2 || b3){  
					// th??nh c??ng
					logger.info(mainApp.getTransid() + ", process unreg ok");  
					Notification.show("H???y th??nh c??ng", Type.HUMANIZED_MESSAGE);
					
					if(rs.contains("NOT_SUCC") && rs.contains("ch??a ????ng k??")) {
						logger.info(mainApp.getTransid() + ", unsync before => update db anyway");  
						// m???t ?????ng b???, c???n update l???i b???ng subs
						String sql = "update subscriber set status = 3, deactive_time = now(), deactive_channel = 'WCC' where status = 1 and msisdn = '" + subs.getMsisdn() + "' and package_id = '" + subs.getPackageId() + "'";
						xbaseDAO.execSql(mainApp.getTransid(), sql);
						subs.setStatus(3); 
					}
					getPage(pageIndex, showNotify, filter); 
				} else {  
					logger.info(mainApp.getTransid() + ", unreg fail"); 
					Notification.show("H???y KH??NG th??nh c??ng!", Type.ERROR_MESSAGE);
					AppUtils.sendAlarm(mainApp.getTransid(), "Web CSKH kh??ng h???y ???????c s??? ??T: " + subs.getMsisdn() + ", transid: " + mainApp.getTransid());
				}
			}
		}, "12cm", "B???n c?? ch???c ch???n mu???n h???y g??i " + subs.getPackageId() + " cho thu?? bao " + subs.getMsisdn() + " ?").showDialog(); 
	}
	
	@Override
	public void showAddDialog() {
	}

	@Override
	public void showSearchDialog() {
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public void exportTable() {
	}

	@Override
	public void showEditDialog(CCSPSubscriber selectedItem, Object colID) {
	}

	@Override
	public void showCopyDialog(CCSPSubscriber selectedItem) {
	}

	@Override
	public int deleteSelectedItem(ArrayList<CCSPSubscriber> listSelected) {
		return 0;
	}

	@Override
	public DialogSubmitResult dialogSubmit_(CCSPSubscriber item, LDInputDialog<CCSPSubscriber> dialog) {
		return null;
	}

	public FormCCSPLayoutSubsInfo getFormMultiComponent() {
		return formMultiComponent;
	}

	public void setFormMultiComponent(FormCCSPLayoutSubsInfo formMultiComponent) {
		this.formMultiComponent = formMultiComponent;
	}

	public int getTotalFound() {
		return totalFound;
	}

	public void setTotalFound(int totalFound) {
		this.totalFound = totalFound;
	}

}
