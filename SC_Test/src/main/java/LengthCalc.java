import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author wangtao
 * @email wangtaovipone@sina.com
 * @version
 * 
 */
public class LengthCalc {

	private static String fileName = "input.txt";

	private String encoding = "UTF-8";

	private static final Logger LOGGER = Logger.getLogger(LengthCalc.class);

	/**
	 * 单位转换规则
	 */
	private Map<String, Float> Unit_Conversion = new LinkedHashMap<String, Float>();

	private Map<String, String> Formula = new LinkedHashMap<String, String>();

	public static void main(String[] args) {

		LengthCalc calc = new LengthCalc();
		String filePath = LengthCalc.class.getClass().getResource("/")
				.getPath()
				+ fileName;
		calc.readTxtFile(filePath);

	}

	public void readTxtFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				String endFlag = "";
				boolean isUnit = true;
				Map<String, String> t_map = new HashMap<String, String>();
				t_map.put("miles", "mile");
				t_map.put("yards", "yard");
				t_map.put("inches", "inch");
				t_map.put("feet", "foot");
				t_map.put("faths", "fath");
				DecimalFormat t_format = new DecimalFormat("####.##");
				while ((lineTxt = bufferedReader.readLine()) != null) {

					if (endFlag.equals(lineTxt)) {
						// System.out.println("ok");
						isUnit = false;
					} else {
						if (isUnit) {
							parseUnitConversion(lineTxt);
						} else {

							Formula.put(
									lineTxt,
									t_format.format(calcFormula(lineTxt, t_map))
											+ " m");
						}
					}
				}
				read.close();
				writeTxtFile();
			} else {
				LOGGER.error("找不到指定的文件");
			}
		} catch (Exception e) {
			LOGGER.error("读取文件内容出错", e);
		}

	}

	/**
	 * 写入文件
	 */
	public void writeTxtFile() {
		String filePath = LengthCalc.class.getClass().getResource("/")
				.getPath()
				+ "ouput.txt";
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(file), encoding);// 考虑到编码格式
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			bufferedWriter.write("wangtaovipone@sina.com");
			bufferedWriter.newLine();
			Iterator<String> iter = Formula.values().iterator();
			while (iter.hasNext()) {
				String type = iter.next();
				bufferedWriter.newLine();
				bufferedWriter.write(type);
			}
			bufferedWriter.flush();
			writer.close();

		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * 解析单位转换规则.
	 * 
	 * @param lineTxt
	 */
	private void parseUnitConversion(String lineTxt) {
		// String [] str=lineTxt.split("=");
		String[] strs = StringUtils.split(lineTxt, "=");
		if (strs != null && strs.length > 0) {
			// Unit_Conversion.put("m", 1.0f);
			for (int i = 0; i < strs.length; i++) {
				String[] keys = StringUtils.split(strs[0], " ");
				String[] values = StringUtils.split(strs[1], " ");

				Unit_Conversion.put(keys[1].trim(), Float.valueOf(values[0]));
			}

		}
	}

	/**
	 * 计算公式值.
	 * 
	 * @param lineTxt
	 * @param t_map
	 * @return
	 */
	private float calcFormula(String lineTxt, Map<String, String> t_map) {
		float result = 0;

		int index = lineTxt.indexOf("+");

		if (index > -1) {
			return calcFormula(lineTxt.substring(0, index), t_map)
					+ calcFormula(
							lineTxt.substring(index + 1, lineTxt.length()),
							t_map);

		} else if (lineTxt.indexOf("-") > -1) {
			index = lineTxt.indexOf("-");
			return calcFormula(lineTxt.substring(0, index), t_map)
					- calcFormula(
							lineTxt.substring(index + 1, lineTxt.length()),
							t_map);
		} else {
			// curCalc = "=";
		}
		String[] strs = StringUtils.split(lineTxt, " ");

		if (strs != null && strs.length > 0) {
			if (t_map.containsKey(strs[1])) {
				strs[1] = (String) t_map.get(strs[1]);
			}
			if (Unit_Conversion.containsKey(strs[1])) {
				Float value = Unit_Conversion.get(strs[1]);
				result = Float.valueOf(strs[0]) * value;
			}

		}

		return result;
	}

}
