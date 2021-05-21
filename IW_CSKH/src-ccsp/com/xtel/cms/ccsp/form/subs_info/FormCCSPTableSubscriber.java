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
				// mod 20200514 , chuyển code sang hàm clickUnreg
				clickUnreg(event, pageIndex, showNotify, filter, true); 
			}
		};
		// mod 20200514: thêm
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
						Button button = new Button("Hủy");
						button.setData(s); 
						button.addClickListener(click); 
						s.setButton(button); 
						
						// mod 20200514 ( thêm button2 ở class CCSPSubscriber)
						Button button2 = new Button("Hủy silent");
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
		tableContainer = new XGUI_TableLayout(showNotify, pageIndex, totalFound, serviceName + " Thông tin thuê bao", data, this, this, filter, true, null);
		
		/*XClickListener clickListener = new XClickListener() {
			@Override
			public void click(BeanItem<?> bean) {
				logger.info(mainApp.getTransid() + ", clicked: " + bean.getItemProperty("msisdn")); 
			}
		};*/
		// manual set visible columns 
		ArrayList<TableHeader> columns = new ArrayList<>();
		columns.add(new TableHeader("msisdn", "Số ĐT"));
		columns.add(new TableHeader("status", "Active", null, "1").setShowIconX(true));
		columns.add(new TableHeader("activeTime", "Ngày ĐK", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("expireTime", "Hạn sử dụng", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("activeChannel", "Kênh ĐK", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("cpid", "Mã CPID"));
		columns.add(new TableHeader("deactiveTime", "Ngày Hủy", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("deactiveChannel", "Kênh Hủy", "yyyy/MM/dd HH:mm:ss"));
		columns.add(new TableHeader("packageId", "Mã Gói"));
		columns.add(new TableHeader("pkgDesc", "Tên Gói"));
		
		if(String.valueOf(serviceName).toLowerCase().contains("amobi")) {
			columns.add(new TableHeader("subService", "Dịch Vụ"));
		}
		if(AppUtils.isCatTienSaPlayService()) {
			columns.add(new TableHeader("scoreRenew", "Điểm gia hạn"));
			columns.add(new TableHeader("scoreBuy", "Điểm mua thêm"));
			columns.add(new TableHeader("scoreUse", "Điểm đã dùng"));
			columns.add(new TableHeader("scoreTotal", "Tổng điểm"));
		}
		columns.add(new TableHeader("feeDesc", "Giá Gói"));
		
		// mod 20200514: sửa tên cột cũ, thêm cột mới
		columns.add(new TableHeader("button", "Hủy"));
		columns.add(new TableHeader("button2", "Hủy silent"));
		
		tableContainer.getTable().addVisibleColumn(columns);
		if(countPkg == null) {
			String str = " - Không active dịch vụ."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			tableContainer.addComponent(title);
			
		} else if(countPkg == 0) {
			String str = " - Đã hủy dịch vụ."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			tableContainer.addComponent(title);
		} else {
			String str = " - Active " + countPkg + " gói."; 
			Label title = new Label("<h3 style='color: blue'>" + str + "</h3>", ContentMode.HTML);
			tableContainer.addComponent(title);
		}
		setContent(tableContainer);
	}

	// mod 20200514: bốc từ trên xuống
	public void clickUnreg(ClickEvent event, final int pageIndex, final boolean showNotify, final String filter, final boolean sendSms) {
		final CCSPSubscriber subs = (CCSPSubscriber) event.getButton().getData();
		
		new LDDelConfirmDialog(null, new IOptionDialogListener() {
			@Override
			public void dialogOptionClicked(String buttonName, int buttonIndex) {
				logger.info(mainApp.getTransid() + ", ==> call unregister: " + subs.getMsisdn() + ", pkg: " + subs.getPackageId());
				
				if(mainApp.getUserBean() != null && mainApp.getUserBean().getMaxUnreg() != null && mainApp.getUserBean().getMaxUnreg() >= 0) {
					if(xdata.isCCSPService == false) { // ccsp ko có cột username nên tạm bỏ qua bước check này
						// check xem quá số lượng chưa
						String sql = "select count(id) from his_" + new SimpleDateFormat("yyyyMM").format(new Date())  
										+ " where action in ('UNREG', 'DELETE') and created_time >= date(now()) and username = '" 
										+ mainApp.getUserBean().getUsername() + "' and result = 0";
						
						Integer count = xbaseDAO.getFirstCell(mainApp.getTransid(), sql, Integer.class);
						
						if(count != null && count >= mainApp.getUserBean().getMaxUnreg()) {
							AppUtils.sendAlarm(mainApp.getTransid(), "Web CSKH quá số lượt hủy cấu hình: " + count);
							Notification.show("Hệ thống bận, vui lòng thử lại sau", Type.ERROR_MESSAGE);
							return;
						} 
					}
				}
				String url = 
						urlUnregTemplate.replace("@pkgcode", subs.getPackageId())
						.replace("@msisdn", subs.getMsisdn())
						.replace("@user", mainApp.getUserBean().getUsername() + ""); 
				
				// mod 20200514: thêm &amp;transid=@transid ở url hủy trong config
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
				boolean b3 = rs.contains("NOT_SUCC") && rs.contains("chưa đăng ký");
				logger.info(mainApp.getTransid() + ", UnregRS: " + rs + " | rs_b1: " + b1 + ", b2: " + b2 + ", b3: " + b3);  
				
				if(b1 || b2 || b3){  
					// thành công
					logger.info(mainApp.getTransid() + ", process unreg ok");  
					Notification.show("Hủy thành công", Type.HUMANIZED_MESSAGE);
					
					if(rs.contains("NOT_SUCC") && rs.contains("chưa đăng ký")) {
						logger.info(mainApp.getTransid() + ", unsync before => update db anyway");  
						// mất đồng bộ, cần update lại bảng subs
						String sql = "update subscriber set status = 3, deactive_time = now(), deactive_channel = 'WCC' where status = 1 and msisdn = '" + subs.getMsisdn() + "' and package_id = '" + subs.getPackageId() + "'";
						xbaseDAO.execSql(mainApp.getTransid(), sql);
						subs.setStatus(3); 
					}
					getPage(pageIndex, showNotify, filter); 
				} else {  
					logger.info(mainApp.getTransid() + ", unreg fail"); 
					Notification.show("Hủy KHÔNG thành công!", Type.ERROR_MESSAGE);
					AppUtils.sendAlarm(mainApp.getTransid(), "Web CSKH không hủy được số ĐT: " + subs.getMsisdn() + ", transid: " + mainApp.getTransid());
				}
			}
		}, "12cm", "Bạn có chắc chắn muốn hủy gói " + subs.getPackageId() + " cho thuê bao " + subs.getMsisdn() + " ?").showDialog(); 
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
