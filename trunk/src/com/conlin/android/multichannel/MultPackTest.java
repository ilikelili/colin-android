package com.conlin.android.multichannel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class MultPackTest {
	
	private Project project;

	private final static String projectBasePath = "F:\\Downloads\\android_multchannel_package\\vince";// 要打包的项目根目录
	private final static String copyApkPath = "D:\\android";// 保存打包apk的根目录
	private final static String signApk = "vince-release.apk";// 这里的文件名必须是准确的项目名！
	private final static String reNameApk = "vince_%1$s_%2$s.apk";// 重命名的项目名称前缀(地图项目不用改)
	private final static String placeHolder = "@market@";// 需要修改manifest文件的地方(占位符)

	
	private final static String[] markets = {
			 "gfan_market",
			 "mumayi_market",
			 "liqucn_market",
			 "appchina_market",
			 "fpwap.market",
			 "hiapk_market",
			 "qq_market",
			 "ztems_market",
			 "eoe_market",
			 "nduoa_market" };
	 

	public static void main(String args[]) {
		long startTime = 0L;
		long endTime = 0L;
		long totalTime = 0L;
		Calendar date = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

		try {
			System.out.println("---------ant批量自动化打包开始----------");
			startTime = System.currentTimeMillis();
			date.setTimeInMillis(startTime);
			System.out.println("开始时间为:" + sdf.format(date.getTime()));

//			BufferedReader br = new BufferedReader(new FileReader("market.txt"));
			String flag = null;
//			while ((flag = br.readLine()) != null) {
			for(int i = 0; i < markets.length; i++){
				flag = markets[i];
				
				// 先修改manifest文件:读取临时文件中的@market@修改为市场标识,然后写入manifest.xml中
				String tempFilePath = projectBasePath + File.separator
						+ "AndroidManifest.xml.temp";
				String filePath = projectBasePath + File.separator
						+ "AndroidManifest.xml";
				write(filePath, read(tempFilePath, flag.trim()));
				// 执行打包命令
				MultPackTest mytest = new MultPackTest();
				mytest.init(projectBasePath + File.separator + "build.xml",
						projectBasePath);
				mytest.runTarget("clean");
				mytest.runTarget("release");
				// 打完包后执行重命名加拷贝操作
				File file = new File(projectBasePath + File.separator + "bin"
						+ File.separator + signApk);// bin目录下签名的apk文件

				File renameFile = new File(copyApkPath
						+ File.separator
						+ String.format(reNameApk, flag,
								sdf2.format(date.getTime())));

				if (!renameFile.getParentFile().exists()) {
					renameFile.getParentFile().mkdirs();
				}

				boolean renametag = file.renameTo(renameFile);
				System.out.println("rename------>" + renametag);
				System.out.println("file ------>" + file.getAbsolutePath());
				System.out.println("rename------>"
						+ renameFile.getAbsolutePath());
			}
			System.out.println("---------ant批量自动化打包结束----------");
			endTime = System.currentTimeMillis();
			date.setTimeInMillis(endTime);
			System.out.println("结束时间为:" + sdf.format(date.getTime()));
			totalTime = endTime - startTime;
			System.out.println("耗费时间为:" + getBeapartDate(totalTime));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("---------ant批量自动化打包中发生异常----------");
			endTime = System.currentTimeMillis();
			date.setTimeInMillis(endTime);
			System.out.println("发生异常时间为:" + sdf.format(date.getTime()));
			totalTime = endTime - startTime;
			System.out.println("耗费时间为:" + getBeapartDate(totalTime));
		}
	}

	public void init(String _buildFile, String _baseDir) throws Exception {
		project = new Project();

		project.init();

		DefaultLogger consoleLogger = new DefaultLogger();
		consoleLogger.setErrorPrintStream(System.err);
		consoleLogger.setOutputPrintStream(System.out);
		consoleLogger.setMessageOutputLevel(Project.MSG_INFO);
		project.addBuildListener(consoleLogger);

		// Set the base directory. If none is given, "." is used.
		if (_baseDir == null)
			_baseDir = new String(".");

		project.setBasedir(_baseDir);

		if (_buildFile == null)
			_buildFile = new String(projectBasePath + File.separator
					+ "build.xml");

		// ProjectHelper.getProjectHelper().parse(project, new
		// File(_buildFile));
		// <SPAN style="COLOR: #ff0000">// 关键代码</SPAN>
		ProjectHelper.configureProject(project, new File(_buildFile));
	}

	public void runTarget(String _target) throws Exception {
		// Test if the project exists
		if (project == null)
			throw new Exception(
					"No target can be launched because the project has not been initialized. Please call the 'init' method first !");
		// If no target is specified, run the default one.
		if (_target == null)
			_target = project.getDefaultTarget();

		// Run the target
		project.executeTarget(_target);

	}

	/**
	 * 根据所秒数,计算相差的时间并以**时**分**秒返回
	 * 
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static String getBeapartDate(long m) {
		m = m / 1000;
		String beapartdate = "";
		int nDay = (int) m / (24 * 60 * 60);
		int nHour = (int) (m - nDay * 24 * 60 * 60) / (60 * 60);
		int nMinute = (int) (m - nDay * 24 * 60 * 60 - nHour * 60 * 60) / 60;
		int nSecond = (int) m - nDay * 24 * 60 * 60 - nHour * 60 * 60 - nMinute
				* 60;
		beapartdate = nDay + "天" + nHour + "小时" + nMinute + "分" + nSecond + "秒";

		return beapartdate;
	}

	public static String read(String filePath, String replaceStr) {
		BufferedReader br = null;
		String line = null;
		StringBuffer buf = new StringBuffer();

		try {
			// 根据文件路径创建缓冲输入流
			br = new BufferedReader(new FileReader(filePath));

			// 循环读取文件的每一行, 对需要修改的行进行修改, 放入缓冲对象中
			while ((line = br.readLine()) != null) {
				// 此处根据实际需要修改某些行的内容
				if (line.contains(placeHolder)) {
					line = line.replace(placeHolder, replaceStr);
					buf.append(line);
				} else {
					buf.append(line);
				}
				buf.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					br = null;
				}
			}
		}

		return buf.toString();
	}

	/**
	 * 将内容回写到文件中
	 * 
	 * @param filePath
	 * @param content
	 */
	public static void write(String filePath, String content) {
		BufferedWriter bw = null;
		try {
			// 根据文件路径创建缓冲输出流
			bw = new BufferedWriter(new FileWriter(filePath));
			// 将内容写入文件中
			bw.write(content);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭流
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					bw = null;
				}
			}
		}
	}
	
	
}
