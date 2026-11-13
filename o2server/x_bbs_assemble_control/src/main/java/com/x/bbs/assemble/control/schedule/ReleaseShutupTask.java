package com.x.bbs.assemble.control.schedule;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.bbs.entity.BBSShutup;
import com.x.bbs.entity.BBSShutup_;
import org.quartz.JobExecutionContext;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;


/**
 * 정기 발언 금지 해제 처리 작업
 * @author LJ
 */
public class ReleaseShutupTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(ReleaseShutupTask.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) {
		try {
			logger.info("정기 발언 금지 해제 처리 작업 시작=======");
			List<String> list = this.getReleaseShutupList();
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				emc.beginTransaction(BBSShutup.class);
				emc.delete(BBSShutup.class, list);
				emc.commit();
			}
			logger.info("정기 발언 금지 해제 처리 작업 완료=======");
		} catch (Exception e) {
			logger.warn("정기 발언 금지 해제 처리 작업 예외: {}.", e.getMessage());
			logger.error(e);
		}
	}

	private List<String> getReleaseShutupList() throws Exception{
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			EntityManager em = emc.get( BBSShutup.class );
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery( String.class);
			Root<BBSShutup> root = cq.from( BBSShutup.class );
			Predicate p = cb.lessThan(root.get(BBSShutup_.unmuteDateTime), new Date());
			cq.select(root.get(BBSShutup_.id));
			return em.createQuery( cq.where(p)).getResultList();
		}
	}

}
