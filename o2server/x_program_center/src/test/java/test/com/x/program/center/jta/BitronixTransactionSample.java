//package test.com.x.program.center.jta;
//
//import java.sql.PreparedStatement;
//
//import javax.sql.DataSource;
//
//import bitronix.tm.BitronixTransaction;
//import bitronix.tm.Configuration;
//import bitronix.tm.TransactionManagerServices;
//import bitronix.tm.resource.jdbc.PoolingDataSource;
//
//public class BitronixTransactionSample {
//
//	public static void main(String[] args) throws Exception {
//		System.out.println("!!!!!!!!!!!!!!!!!" + TransactionManagerServices.isTransactionManagerRunning());
//		Configuration conf = TransactionManagerServices.getConfiguration();
//		conf.setServerId("jvm-1");
//		conf.setLogPart1Filename("/data/Temp/part1.btm");
//		conf.setLogPart2Filename("/data/Temp/part2.btm");
//		// 1. Bitronix 관련 의존성 가져오기
//		// ...
//
//		// 2. 데이터 소스 두 개 설정, 하나는 주 DB, 하나는 보조 DB
//		DataSource masterDataSource = createDataSource("mysql1",
//				"jdbc:mysql://172.16.98.230:4006/test_ray?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8",
//				"admin", "password");
//		DataSource slaveDataSource = createDataSource("mysql2",
//				"jdbc:mysql://172.16.98.230:4006/test_ray_55?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8",
//				"admin", "password");
//
//		// 3. Bitronix 트랜잭션 관리자 설정
//		TransactionManagerServices.getTransactionManager().begin();
//		BitronixTransaction utx = (BitronixTransaction) TransactionManagerServices.getTransactionManager()
//				.getTransaction();
//		// 4. 트랜잭션 관리자로 트랜잭션 관리
//		try {
//			// 주 DB에서 작업 실행
//			PreparedStatement stmt1 = masterDataSource.getConnection()
//					.prepareStatement("INSERT INTO CTE_CODE (xid, xanswer,xmobile) VALUES (?,?,?)");
//			// 보조 DB에서 작업 실행
//			PreparedStatement stmt2 = slaveDataSource.getConnection()
//					.prepareStatement("INSERT INTO CTE_CODE (xid, xanswer,xmobile) VALUES (?,?,?)");
//
//			// 삽입할 데이터 설정
//			stmt1.setString(1, "123");
//			stmt1.setString(2, "123");
//			stmt1.setString(3, "123");
//
//			stmt2.setString(1, "123");
//			stmt2.setString(2, "123");
//			stmt2.setString(3, "123");
//
//			stmt2.execute();
//			stmt1.execute();
//			int x = 0;
//			x = 100 / x;
//			utx.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			utx.rollback();
//		}
//	}
//
//	private static DataSource createDataSource(String uniqueName, String url, String username, String password) {
//		PoolingDataSource dataSource = new PoolingDataSource();
//		dataSource.setUniqueName(uniqueName);
//		dataSource.setClassName("com.mysql.cj.jdbc.MysqlXADataSource");
//		dataSource.getDriverProperties().setProperty("url", url);
//		dataSource.getDriverProperties().setProperty("user", username);
//		dataSource.getDriverProperties().setProperty("password", password);
//		dataSource.setMinPoolSize(1);
//		dataSource.setMaxPoolSize(5);
//		return dataSource;
//	}
//}