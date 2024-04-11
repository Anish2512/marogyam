package com.his.web;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Scope;
import java.util.Date;
import java.text. DateFormat;

@SuppressWarnings("unchecked")
@Component("ticketManagedBean")
@Scope("session")
public class TicketManagedBean extends BaseBean { 
	private Boolean forPdfAndExcel = Boolean.TRUE;
 	
	private List<TktTickets> tickets = new ArrayList<>();
 	private int rowsPerPage = 20;
 	@Resource(name = "ticketingBaseService")
 	private ITicketingBaseService ticketingBaseService;
 	public List<TktTickets> getTickets(){
 		if(!BeanUtils.isNullOrEmpty(getSearchForm().getSearchUtil().getCurrentSearch())) {
			tickets = getTicketingBaseService().search("fetchAlltickets.Q",
				new Object[] {  getUserPreferences().getBranch().getId() }, getSearchForm().getSearchUtil().getCurrentSearch(),
				getSearchForm().getSearchUtil().getSortExpression(), 0, -1);
		}else {
			tickets = getTicketingBaseService().find("fetchAllTickets.Q");
		} 
		return tickets;
	}
	public void setTickets (List<TktTickets> tickets) {
		this.tickets = tickets;
	}
 	public TicketSearchBarForm getSearchForm() {
 		return (TicketSearchBarForm) getSpringBean("ticketSearchBar");
 	}
	public int getRowsPerPage() {
		return rowsPerPage;
	}
	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}
	public ITicketingBaseService getTicketingBaseService() {
		return ticketingBaseService;
	}

	public void exportToExcel() {
		forPdfAndExcel = Boolean.FALSE;
		Map<String, Object> data = new HashMap<String, Object>();
		Object[] object = null;
		List<Object[]> objList = new Linkedlist<Object[]>();
		try {
			for(int i=0, j=0; i<getTickets().size(); i++) {
				Object temp = getTickets().get(i);
				Object[] obj = (Object[]) temp;
				object = new Object[] {j=j+1, obj[1], obj[2], obj [3], obj[4], obj[5], obj[6]};
				objList.add(object);
			}
			data.put("result", objlist);
			data.put("searchCriterias", getSearchCriteriaforExport());
			ExportUtil.exporttoExcel (getExcelExportTempaltePath()+"DigitalDudeTickets.xls", "DigitalDudeTickets.xls", data);
			forPdfAndExcel = Boolean.TRUE;
		} catch (Exception e) {
			addError (KEY_ERROR_DETAIL, e.getMessage());
			Log.error(e.getMessage(), e);
		}
	}


	private List<Object[]> getSearchCriteriaforExport() {
		List<Object[]> searchCriteria = new ArrayList<Object[]>();
		SearchUtil searchUtil = getSearchForm().getSearchUtil();
		for (Iterator<?> iterator = searchUtil.getCurrentSearch().iterator(); iterator.hasNext();) {
			SearchCriteriaDetail scd = (SearchCriteriaDetail) iterator.next();
			if (scd.getPropertyName() == null || scd.getPropertyName().trim().length() == 0)
				continue;
			Object[] objects = new Object[3]; 
			if(!BeanUtils.isNull(searchUtil.getColumns())){
				for (Iterator<?> iterator2 = searchUtil.getColumns().iterator(); iterator2.hasNext();) {
					SelectItem option = (SelectItem) iterator2.next();
					if (option.getValue().equals(scd.getPropertyName())) {
						objects[0] = option.getLabel();
						break;	
					}
				}
			}
			objects[1] = scd.getOperator();
        		objects[2] = scd.getValue(); 
        		searchCriteria.add(objects);
		}
		return SearchCriteria;
	}
}