package com.sametime.bb;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import com.ibm.sametime.userinfo.userInfobbapi.DetailItem;
import com.ibm.sametime.userinfo.userInfobbapi.RequestContext;
import com.ibm.sametime.userinfo.userInfobbapi.Response;
import com.ibm.sametime.userinfo.userInfobbapi.UserInfoBlackBoxAPI;
import com.ibm.sametime.userinfo.userInfobbapi.UserInfoException;

public class ConnectionsStandaloneBlackbox implements UserInfoBlackBoxAPI {

	// constants
	private static final String SQL = "SELECT EMPL.PROF_UID UID,PROF_DISPLAY_NAME DSP_NAME, PROF_MAIL_LOWER EMAIL, " + PROF_MOBILE MOBILE, PROF_PAGER PAGER,PROF_TITLE TITLE, PROF_TIMEZONE TIMEZONE,PROF_TELEPHONE_NUMBER PHONE," + "
	PROF_BLOG_URL BLOG_URL, EMPL.
	PROF_DEPARTMENT_NUMBER DEPT_NO, DEPT.
	PROF_DEPARTMENT_TITLE DEPT_NAME," + "
	EMPL.PROF_ORGANIZATION_IDENTIFIER ORG_IDENT, ORG.
	PROF_ORGANIZATION_TITLE ORG_NAME, EMPL.
	PROF_ISO_COUNTRY_CODE CTRY_ISOCODE," + "
	CTRY.PROF_COUNTRY_DESC CTRY_NAME, P.
	PROF_IMAGE PHOTO_BYTES, P.
	PROF_FILE_TYPE PHOTO_MIMETYPE
	FROM EMPINST.
	EMPLOYEE EMPL LEFT" + "
	OUTER JOIN
	EMPINST.PHOTO P
	ON EMPL.PROF_UID=
	P.PROF_UID LEFT
	OUTER JOIN
	EMPINST.DEPARTMENT DEPT ON" + "EMPL.PROF_DEPARTMENT_NUMBER=
	DEPT.PROF_DEPARTMENT_CODE LEFT
	OUTER JOIN
	EMPINST.COUNTRY CTRY ON" + "EMPL.PROF_ISO_COUNTRY_CODE=
	CTRY.PROF_ISO_COUNTRY_CODE LEFT
	OUTER JOIN
	EMPINST.ORGANIZATION ORG ON" + "EMPL.PROF_ORGANIZATION_IDENTIFIER=
	ORG.PROF_ORGANIZATION_CODE WHERE EMPL.PROF_MAIL_LOWER=?";

	// declarations
	private boolean valid = false;
	private String url = null;
	private String username = null;
	private String password = null;

	public void init() throws UserInfoException {
		try {
			// load class
			Class.forName("com.ibm.db2.jcc.DB2Driver");

			// read properties
			FileInputStream fin = new FileInputStream("LCUserInfoConfig.properties");
			Properties props = new Properties();
			props.load(fin);

			// read properties
			this.url = props.getProperty("url");
			this.username = props.getProperty("username");
			this.password = props.getProperty("password");

			// close file and mark valid
			fin.close();
			this.valid = true;

		} catch (Exception e) {
			e.printStackTrace();
			throw new UserInfoException("Unable to initialize");
		}
	}

	public Response processRequest(RequestContext ctx) throws UserInfoException {
  // declarations
  Connection conn = null;
  PreparedStatement ps = null;
  ResultSet rs = null;

  // create empty response
  Response response = new Response(ctx.getRequestID());

  // only process request if the blackbox is correctly configured
  if (!this.valid) {
  return response;
  }

  // get user id
  String userid = ctx.getUid();

  try {
    // perform database lookup

    conn = DriverManager.getConnection(this.url, this.username, this.password);
    ps = conn.prepareStatement(SQL);
    ps.setString(1, userid);
    rs = ps.executeQuery();

    if (rs.next()) {
    String display_name = rs.getString("dsp_name");
    String blog_url = rs.getString("blog_url");
    String title = rs.getString("title");
    String timezone = rs.getString("timezone");
    byte[] photo_bytes = rs.getBytes("photo_bytes"); String mimetype = rs.getString("photo_mimetype");

    // set data in response
    response.setRetrievedDetail("MailAddress",
    this.createTextDetailItem("MailAddress", userid));
    response.setRetrievedDetail("Name", this.createTextDetailItem("Name", display_name));
    response.setRetrievedDetail("Title", this.createTextDetailItem("Title", title));
    response.setRetrievedDetail("Telephone", this.createTextDetailItem("Telephone", timezone));
    response.setRetrievedDetail("Company", this.createTextDetailItem("Company", blog_url));
    response.setRetrievedDetail("Photo", this.createImageDetailItem("Photo", mimetype, photo_bytes));
    }
   }

   catch (Exception e) {
    e.printStackTrace();
    // return empty response return response;

   } finally {
   // release resources
if
} if
}if

}}

	// return
	return response;}

	public void terminate() {
 128. }129.

	private DetailItem createTextDetailItem(String name, String value) {
		DetailItem i = new DetailItem();
		i.setName(name);
		i.setType("text/plain");
		i.setTextValue(this.nullSafeString(value));
		return i;
	}

	private DetailItem createImageDetailItem(String name, String mimetype, byte[] bytes) {
		DetailItem i = new DetailItem();
		i.setName(name);
		if (null != mimetype && mimetype.equalsIgnoreCase("image/pjpeg")) {
			i.setType("image/jpeg");
		} else {
			i.setType("image/gif");
		}
		String base64 = Base64.encodeBytes(bytes, Base64.DONT_BREAK_LINES);
		i.setTextValue(base64);
		return i;
	}

	private String nullSafeString(String s) {
		return (null == s) ? "" : s;
	}
}
