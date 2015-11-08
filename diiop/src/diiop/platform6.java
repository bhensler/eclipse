package diiop;

import lotus.domino.*;

public class platform6 implements Runnable {
	String host = null, user = "", pwd = "";

	public static void main(String argv[]) {
		if (argv.length < 1) {
			System.out.println("Need to supply Domino server name");
			return;
		}
		platform6 t = new platform6(argv);
		Thread nt = new Thread((Runnable) t);
		nt.start();
	}

	public platform6(String argv[]) {
		host = argv[0];
		if (argv.length >= 2)
			user = argv[1];
		if (argv.length >= 3)
			pwd = argv[2];
	}

	public void run() {
		try {
			Session s = NotesFactory.createSession(host, user, pwd);
			DbDirectory dir = s.getDbDirectory(null);
			Database db = dir.openMailDatabase();
			View view = db.getView("($InBox)");

			String p = s.getPlatform();
			String server = s.getServerName();
			String user = dir.getName();
			String filename = db.getFileName();

			System.out.println("Platform = " + p + "\n" + server + "\n" + user + "\n" + filename);
			System.out.println("($InBox) has " + view.getColumns().size() + " columns");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
