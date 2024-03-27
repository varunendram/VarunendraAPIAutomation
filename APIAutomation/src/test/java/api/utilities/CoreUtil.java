package api.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ResourceBundle;

import org.json.JSONObject;
import org.json.JSONTokener;

public class CoreUtil {
	static ResourceBundle data = null;
	public static ResourceBundle getConfigParam() {
		if (data == null) {
			data = ResourceBundle.getBundle("enviornment");
		}
		return data;
	}

	public static String getProperty(String key) {
		ResourceBundle data = getConfigParam();
		String value = data.getString(key);
		return value;
	}
	
	public static JSONObject getJSONObject(String filepath) {
		File f = new File(filepath);
		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		JSONTokener jt = new JSONTokener(fr);
		JSONObject jo = new JSONObject(jt);
		return jo;
	}
}
