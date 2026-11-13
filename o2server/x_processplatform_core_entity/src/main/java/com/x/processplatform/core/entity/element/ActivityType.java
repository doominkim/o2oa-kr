package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JpaObject;

/**
 * 프로세스 노드 열거형 agent: 스크립트 노드 begin: 시작 노드 cancel: 취소 노드 choice: 선택 활동 노드 delay: 지연 활동 노드
 * end: 종료 노드 embed: 프로세스 호출 노드 invoke: 서비스 호출 노드 manual: 수동 노드 merge: 병합 활동 노드 message:
 * parallel: 병렬 활동 노드 service: 서비스 노드 split: 분할 노드 publish: 데이터 게시 노드
 * 
 * @author sword
 */
public enum ActivityType {
    agent, begin, cancel, choice, delay, end, embed, invoke, manual, merge, parallel, publish, service, split,;

    public static final int length = JpaObject.length_16B;

    public static Class<? extends Activity> getClassOfActivityType(ActivityType activityType) {
        switch (activityType) {
            case agent:
                return Agent.class;
            case begin:
                return Begin.class;
            case cancel:
                return Cancel.class;
            case choice:
                return Choice.class;
            case delay:
                return Delay.class;
            case end:
                return End.class;
            case embed:
                return Embed.class;
            case invoke:
                return Invoke.class;
            case manual:
                return Manual.class;
            case merge:
                return Merge.class;
            case parallel:
                return Parallel.class;
            case publish:
                return Publish.class;
            case service:
                return Service.class;
            case split:
                return Split.class;
            default:
                return null;
        }
    }
}
