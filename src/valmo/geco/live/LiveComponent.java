/**
 * Copyright (c) 2010 Simon Denier
 * Released under the MIT License (see LICENSE file)
 */
package valmo.geco.live;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import valmo.geco.model.Course;
import valmo.geco.model.RunnerRaceData;
import valmo.geco.model.impl.POFactory;
import valmo.geco.model.xml.CourseSaxImporter;
import valmo.geco.ui.SwingUtils;

/**
 * @author Simon Denier
 * @since Aug 26, 2010
 *
 */
public class LiveComponent {

	public JFrame jFrame;
	public LiveMapComponent map;
	public RunnerResultPanel runnerP;

	private LiveMapControl mapControl;
	private Map<String, Float[]> controlPos;
	private Collection<Course> courses;
	private LiveConfigPanel configP;


	public static void main(String[] args) {
		LiveComponent gecoLive = new LiveComponent().initWindow();
//		gecoLive.loadMapImage("hellemmes.jpg");
//		gecoLive.importCourseData("hellemmes.xml");
//		float factor = GecoLiveConfig.dpi2dpmmFactor(150);
//		gecoLive.createCourses(factor, factor, -18, -25);
		gecoLive.setStartDir("demo/hellemmes");
		gecoLive.openWindow();
	}

	public LiveComponent() {
		controlPos = Collections.emptyMap();
		courses = Collections.emptyList();
		mapControl = new LiveMapControl();
	}
	
	public void setStartDir(String dir) {
		File propFile = new File(dir + File.separator + "live.prop");
		if( propFile.exists() ) {
			Properties liveProp = new Properties();
		try {
			liveProp.load(new BufferedReader(new FileReader(propFile)));
			loadMapImage(dir + File.separator + liveProp.getProperty("MapFile"));
			importCourseData(dir + File.separator + liveProp.getProperty("CourseFile"));
			configP.setProperties(liveProp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}

	public LiveComponent initWindow() {
		jFrame = new JFrame();
		initGui(jFrame.getContentPane());
		jFrame.pack();
		return this;
	}
	public void openWindow() {
		jFrame.setVisible(true);
	}
	public void closeWindow() {
		jFrame.setVisible(false);
	}
	public boolean isShowing() {
		return jFrame.isShowing();
	}

	public Container initGui(Container mainContainer) {
		mainContainer.setLayout(new BorderLayout());
		mainContainer.add(initControlPanel(), BorderLayout.WEST);
		map = new LiveMapComponent();
		mainContainer.add(new JScrollPane(map), BorderLayout.CENTER);
		return mainContainer;
	}
	private Container initControlPanel() {
		JTabbedPane controlPanel = new JTabbedPane();
		configP = new LiveConfigPanel(jFrame, this);
		controlPanel.add("Config", SwingUtils.embed(configP));
		runnerP = new RunnerResultPanel();
		controlPanel.add("Runner", runnerP);
//		controlPanel.add("Runner", SwingUtils.embed(runnerP));
		return controlPanel;
	}
	
	public void loadMapImage(String mapfile) {
		map.loadMapImage(mapfile);
	}

	public void importCourseData(String filename) {
		try {
			controlPos.clear(); 
			courses.clear();
			CourseSaxImporter importer = new CourseSaxImporter(new POFactory()); // TODO: service
			importer.importFromXml(filename);
			controlPos = importer.controls();
			courses = importer.courses();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createCourses(float xFactor, float yFactor, int dx, int dy) {
		mapControl.setXFactor(xFactor);
		mapControl.setYFactor(yFactor);
		mapControl.createControlsFrom(controlPos, dx, dy);
		mapControl.createCoursesFrom(courses);
	}
	
	public Vector<String> coursenames() {
		Vector<String> coursenames = new Vector<String>();
		for (Course course : courses) {
			coursenames.add(course.getName());
		}
		return coursenames;
	}

	public void displayMap() {
		map.showControls(null);
	}
	
	public void displayAllControls() {
		mapControl.resetControls();
		map.showControls(mapControl.allControls());
	}
	
	public void displayCourse(String coursename) {
		LivePunch course = mapControl.startPunchForCourse(coursename);
		if( course!=null ) {
			mapControl.resetControls();
			map.showTrace(course);
		}
	}

	public void displayRunnerMap(RunnerRaceData runnerData) {
		if( runnerData!=null ) {
			runnerP.updateRunnerData(runnerData);
			displayTraceFor( runnerData );
		}
	}

	private void displayTraceFor(RunnerRaceData runnerData) {
		LivePunch course = mapControl.startPunchForCourse(runnerData.getCourse().getName());
		if( course!=null ) {
			mapControl.resetControls();
			if( runnerData.hasTrace() ) {
				map.showTrace( mapControl.createPunchTraceFor(course, runnerData.getResult().formatTrace().split(",")) );
			} else {
				map.showTrace(course);
			}
		}
	}
	
}