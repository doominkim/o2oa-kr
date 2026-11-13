package com.x.attendance.assemble.control.processor.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.x.attendance.assemble.control.processor.AbStractDataForOperator;
import com.x.attendance.assemble.control.processor.EntityAnalyseData;
import com.x.attendance.assemble.control.processor.EntityImportDataDetail;
import com.x.attendance.assemble.control.processor.EntitySupplementData;


public class DataProcessThreadFactory {
	
	private static DataProcessThreadFactory single = null;
	/** 
     * 작업이 execute(Runnable) 메서드로 스레드 풀에 추가될 때: 
     * 1. 스레드 풀의 스레드 수가 corePoolSize보다 작으면, 풀의 스레드가 모두 유휴 상태여도 추가된 작업을 처리할 새 스레드를 생성. 
     * 2. 스레드 풀의 스레드 수가 corePoolSize와 같지만 버퍼 큐 workQueue가 가득 차지 않았으면 작업을 버퍼 큐에 넣음. 
     * 3. 스레드 풀의 스레드 수가 corePoolSize보다 크고 버퍼 큐 workQueue가 가득 찼으며 풀의 스레드 수가 maximumPoolSize보다 작으면 추가된 작업을 처리할 새 스레드를 생성. 
     * 4. 스레드 풀의 스레드 수가 corePoolSize보다 크고 버퍼 큐 workQueue가 가득 찼으며 풀의 스레드 수가 maximumPoolSize와 같으면 다음을 통해 
     * handler가 지정한 정책으로 이 작업을 처리한다. 즉, 작업 처리 우선순위는 코어 스레드 corePoolSize, 작업 큐 workQueue, 최대 스레드 maximumPoolSize 
     * , 세 가지가 모두 가득 차면 handler로 거부된 작업을 처리한다.  
     *  
     * 5. 스레드 풀의 스레드 수가 corePoolSize보다 클 때, 어떤 스레드의 유휴 시간이 keepAliveTime을 넘으면 해당 스레드를 종료한다. 이렇게 스레드 풀은 풀 안의 스레드 수를 동적으로 조정할 수 있다. 
     */  
	private static ThreadPoolExecutor threadpool = null;
	 
	private DataProcessThreadFactory() {
		/** 
	     * Param:  
	     * corePoolSize - 풀에 유지하는 스레드 수(유휴 스레드 포함).  
	     * maximumPoolSize - 풀에서 허용하는 최대 스레드 수(LinkedBlockingQueue 사용 시 효과 없음).  
	     * keepAliveTime - 스레드 수가 코어 수보다 클 때, 초과된 유휴 스레드가 종료 전 새 작업을 기다리는 최대 시간(스레드 풀이 유지하는 스레드의 허용 유휴 시간).  
	     * unit - keepAliveTime 매개변수의 시간 단위, 스레드 풀이 유지하는 스레드의 허용 유휴 시간 단위: 초.  
	     * workQueue - 실행 전 작업을 보관하는 큐(버퍼 큐). 이 큐는 execute 메서드로 제출된 Runnable 작업만 보관한다.  
	     * RejectedExecutionHandler - 스레드 풀의 거부 작업 처리 정책(현재 작업 재추가 시도, execute() 메서드 자동 반복 호출) 
	     */
		threadpool = new ThreadPoolExecutor( 20, 40, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardOldestPolicy() );
	}
	
	public static DataProcessThreadFactory getInstance() {
		if( single == null ) {
			single = new DataProcessThreadFactory();
		}
		return single;
	}
	
	public long getAliveThreadCount() {
		return threadpool.getActiveCount();
	}
	
	public long getTaskCount() {
		return threadpool.getTaskCount();
	}
	
	public void showdown() {
		threadpool.shutdownNow();
	}
	
	public void submit( AbStractDataForOperator data, Boolean debugger ) {
		if( "excel".equals( data.getData_type() )) {
			threadpool.execute( new OperatorDataValidator( ( EntityImportDataDetail)data, debugger ) );
		}else if( "saveData".equals( data.getData_type() ) ) {
			threadpool.execute( new OperatorDataSaver( ( EntityImportDataDetail)data, debugger ) );
		}else if( "supplement".equals( data.getData_type() ) ) {
			threadpool.execute( new OperatorDataSupplement( ( EntitySupplementData)data, debugger ) );
		}else if( "analyse".equals( data.getData_type() ) ) {
			threadpool.execute( new OperatorDataAnalyse( ( EntityAnalyseData )data, debugger ) );
		}
	}
}
