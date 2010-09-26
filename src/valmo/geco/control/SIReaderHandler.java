/**
 * Copyright (c) 2009 Simon Denier
 * Released under the MIT License (see LICENSE file)
 */
package valmo.geco.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import org.martin.sireader.common.PunchObject;
import org.martin.sireader.common.PunchRecordData;
import org.martin.sireader.common.ResultData;
import org.martin.sireader.server.IPunchObject;
import org.martin.sireader.server.IResultData;
import org.martin.sireader.server.PortMessage;
import org.martin.sireader.server.SIPortHandler;
import org.martin.sireader.server.SIReaderListener;

import valmo.geco.core.Announcer;
import valmo.geco.core.GecoRequestHandler;
import valmo.geco.core.TimeManager;
import valmo.geco.model.Punch;
import valmo.geco.model.Runner;
import valmo.geco.model.RunnerRaceData;
import valmo.geco.model.Stage;
import valmo.geco.model.Status;

/**
 * @author Simon Denier
 * @since Oct 8, 2009
 *
 */
public class SIReaderHandler extends Control
	implements Announcer.StageListener, SIReaderListener<PunchObject,PunchRecordData> {

	private GecoRequestHandler requestHandler;
	
	private SIPortHandler portHandler;

	private String portName;
	
	private long zeroTime = 32400000; // 9:00
	
	private int nbTry;
	
	private boolean starting;
	
	/**
	 * @param factory
	 * @param stage
	 * @param announcer
	 */
	public SIReaderHandler(GecoControl geco, GecoRequestHandler requestHandler) {
		super(geco);
		this.requestHandler = requestHandler;
		changePortName();
		changeZeroTime();
		geco.announcer().registerStageListener(this);
	}
	
	public void setRequestHandler(GecoRequestHandler requestHandler) {
		this.requestHandler = requestHandler;
	}

	public static String portNameProperty() {
		return "SIPortname";
	}

	public static String zerotimeProperty() {
		return "SIZeroTime";
	}
	
	public String defaultPortName() {
		return "/dev/tty.SLAB_USBtoUART";
	}

	private void changePortName() {
		String port = stage().getProperties().getProperty(portNameProperty());
		if( port!=null ) {
			setPortName(port);
		} else {
			setPortName(defaultPortName());
		}
	}
	
	private void changeZeroTime() {
		try {
			setNewZeroTime( Long.parseLong(stage().getProperties().getProperty(zerotimeProperty())) );			
		} catch (NumberFormatException e) {
			setNewZeroTime(32400000);
		}
	}
	
	public void setNewZeroTime(long newZerotime) {
		setZeroTime( newZerotime );			
		if( portHandler!=null )
			portHandler.setCourseZeroTime(getZeroTime());
	}
	
	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public long getZeroTime() {
		return zeroTime;
	}

	public void setZeroTime(long zeroTime) {
		this.zeroTime = zeroTime;
	}

	private void configure() {
		portHandler = new SIPortHandler(new ResultData());
		portHandler.addListener(this);
		portHandler.setPortName(getPortName());
		portHandler.setDebugDir(stage().getBaseDir());
		portHandler.setCourseZeroTime(getZeroTime());
	}

	public void start() {
		configure();
		nbTry = 0;
		starting = true;
		if (!portHandler.isAlive())
			portHandler.start();
		PortMessage m = new PortMessage(SIPortHandler.START);
		portHandler.sendMessage(m);
	}
	
	public void stop() {
		if( portHandler==null )
			return;
		try {
			portHandler.interrupt();
			portHandler.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void newCardRead(IResultData<PunchObject,PunchRecordData> card) {
		Runner runner = registry().findRunnerByChip(card.getSiIdent());
		if( runner!=null ) {
			RunnerRaceData runnerData = registry().findRunnerData(runner);
			if( runnerData.hasResult() ) {
				geco().log("READING AGAIN " + card.getSiIdent());
				String returnedCard = requestHandler.requestMergeExistingRunner(handleNewData(card), runner);
				if( returnedCard!=null ) {
					geco().announcer().announceCardReadAgain(returnedCard);
				}
			} else {
				handleData(runnerData, card);	
			}
		} else {
			geco().log("READING UNKNOWN " + card.getSiIdent());
			String returnedCard = requestHandler.requestMergeUnknownRunner(handleNewData(card), card.getSiIdent());
			if( returnedCard!=null ) {
				geco().announcer().announceUnknownCardRead(returnedCard);
			}
		}
		
	}
	
	private RunnerRaceData handleNewData(IResultData<PunchObject,PunchRecordData> card) {
		RunnerRaceData newData = factory().createRunnerRaceData();
		newData.setResult(factory().createRunnerResult());
		updateRaceDataWith(newData, card);
		// do not do any announcement here since the case is handled in the Merge dialog after that and depends on user decision
		return newData;
	}
	
	/**
	 * @param runnerData 
	 * @param card
	 */
	private void handleData(RunnerRaceData runnerData, IResultData<PunchObject,PunchRecordData> card) {
		updateRaceDataWith(runnerData, card);
		Status oldStatus = runnerData.getResult().getStatus();
		geco().checker().check(runnerData);
		geco().log("READING " + runnerData.infoString());
		if( runnerData.getResult().is(Status.MP) ) {
			geco().announcer().dataInfo(runnerData.getResult().formatTrace() + " (" + runnerData.getResult().getNbMPs() + " MP)");
		}
		geco().announcer().announceCardRead(runnerData.getRunner().getChipnumber());
		geco().announcer().announceStatusChange(runnerData, oldStatus);
	}

	/**
	 * @param runnerData
	 * @param card
	 */
	private void updateRaceDataWith(RunnerRaceData runnerData, IResultData<PunchObject,PunchRecordData> card) {
		runnerData.stampReadtime();
		runnerData.setErasetime(safeTime(card.getClearTime()));
		runnerData.setControltime(safeTime(card.getCheckTime()));		
		runnerData.setStarttime(safeTime(card.getStartTime()));
		runnerData.setFinishtime(safeTime(card.getFinishTime()));
		checkStartFinishTimes(runnerData, card.getSiIdent());
		handlePunches(runnerData, card.getPunches());
	}

	private Date safeTime(long siTime) {
		if( siTime>PunchObject.INVALID ) {
			return new Date(siTime);
		} else {
			return TimeManager.NO_TIME;
		}
	}
	
	private void checkStartFinishTimes(RunnerRaceData runnerData, String chipNumber) {
		if( runnerData.getStarttime().equals(TimeManager.NO_TIME) ) {
			geco().log("MISSING start time for " + chipNumber);
		}
		if( runnerData.getFinishtime().equals(TimeManager.NO_TIME) ) {
			geco().log("MISSING finish time for " + chipNumber);
		}
	}


	/**
	 * @param runnerData
	 * @param punches
	 */
	private void handlePunches(RunnerRaceData runnerData, ArrayList<PunchObject> punchArray) {
		Punch[] punches = new Punch[punchArray.size()];
		for(int i=0; i< punches.length; i++) {
			IPunchObject punchObject = punchArray.get(i);
			punches[i] = factory().createPunch();
			punches[i].setCode(punchObject.getCode());
			punches[i].setTime(new Date(punchObject.getTime()));
		}
		runnerData.setPunches(punches);
	}

	@Override
	public void portStatusChanged(String status) {
		if( status.equals("     Open      ") && starting ){
			geco().announcer().announceStationStatus("Ready");
			starting = false;
		}
		if( status.equals("     Connecting") ){
			nbTry++;
		}
		if( nbTry>=2 ) { // catch any tentative to re-connect after a deconnexion
			portHandler.interrupt(); // one last try, after interruption
			if( starting ) { // wrong port
				geco().announcer().announceStationStatus("NotFound");
			} else { // station was disconnected?
				geco().announcer().announceStationStatus("Failed");
			}
		}
	}
	
	
	@Override
	public void changed(Stage previous, Stage next) {
//		stop();
		changePortName();
		changeZeroTime();
	}

	@Override
	public void saving(Stage stage, Properties properties) {
		properties.setProperty(portNameProperty(), getPortName());
		properties.setProperty(zerotimeProperty(), Long.toString(getZeroTime()));
	}

	@Override
	public void closing(Stage stage) {
		stop();
	}
	
}
