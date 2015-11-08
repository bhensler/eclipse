package com.eview.bb;

import com.ibm.sametime.userinfo.userInfobbapi.DetailItem;
import com.ibm.sametime.userinfo.userInfobbapi.RequestContext;
import com.ibm.sametime.userinfo.userInfobbapi.Response;
import com.ibm.sametime.userinfo.userInfobbapi.UserInfoBlackBoxAPI;
import com.ibm.sametime.userinfo.userInfobbapi.UserInfoException;

public class HardcodedBlackbox implements UserInfoBlackBoxAPI {

	public HardcodedBlackbox() {
	}

	public void init() throws UserInfoException {
		// do nothing
		System.out.println("Initalizing HardcodedBlackbox...");
	}

	public Response processRequest(RequestContext req) throws UserInfoException {
		if (req == null) {
			throw new UserInfoException("Invalid request - null value");
		}
		System.out.println("HardcodedBlackbox serving data for user: " + req.getUid());

		// create response object
		Response response = new Response();
		response.setUserFound(true);

		// add data
		DetailItem item = createNewItem("Company", "Some company (" + req.getUid() + ")");
		response.setRetrievedDetail(item.getId(), item);
		item = createNewItem("Department", "Some department(" + req.getUid() + ")");
		response.setRetrievedDetail(item.getId(), item);
		item = createNewItem("Title", "Some title(" + req.getUid() + ")");
		response.setRetrievedDetail(item.getId(), item);
		item = createNewItem("Telephone", "555-5555(" + req.getUid() + ")");
		response.setRetrievedDetail(item.getId(), item);

		// return
		return response;
	}

	private DetailItem createNewItem(String detailName, String value) {
		DetailItem respItem = new DetailItem();
		respItem.setName(detailName);
		respItem.setId(detailName);
		respItem.setType("text/plain");
		respItem.setTextValue(value);
		return respItem;
	}

	public void terminate() {
		System.out.println("Terminating HardcodedBlackbox...");
	}
}