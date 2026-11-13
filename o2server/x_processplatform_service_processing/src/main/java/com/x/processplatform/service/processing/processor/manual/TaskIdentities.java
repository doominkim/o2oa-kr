package com.x.processplatform.service.processing.processor.manual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.organization.Empower;
import com.x.base.core.project.tools.ListTools;

public class TaskIdentities extends ArrayList<TaskIdentity> {

    public TaskIdentities() {
    }

    public TaskIdentities addIdentity(String str) {
        if (StringUtils.isNotEmpty(str)) {
            for (TaskIdentity taskIdentity : this) {
                if (StringUtils.equals(taskIdentity.getIdentity(), str)) {
                    return this;
                }
            }
            TaskIdentity taskIdentity = new TaskIdentity();
            taskIdentity.setIdentity(str);
            this.add(taskIdentity);
        }
        return this;
    }

    public TaskIdentities(List<String> list) {
        for (String str : ListTools.trim(list, true, true)) {
            TaskIdentity taskIdentity = new TaskIdentity();
            taskIdentity.setIdentity(str);
            this.add(taskIdentity);
        }
    }

    public boolean containEmpower() {
        for (TaskIdentity o : this) {
            if (StringUtils.isNotEmpty(o.getFromIdentity())) {
                return true;
            }
        }
        return false;
    }

    public void empower(List<Empower> list) {
        for (Empower empower : list) {
            if (StringUtils.isNotEmpty(empower.getFromIdentity()) && StringUtils.isNotEmpty(empower.getToIdentity())
                    && (!StringUtils.equals(empower.getFromIdentity(), empower.getToIdentity()))) {
                for (TaskIdentity taskIdentity : this) {
                    if (BooleanUtils.isNotTrue(taskIdentity.getIgnoreEmpower())
                            && StringUtils.equals(taskIdentity.getIdentity(), empower.getFromIdentity())) {
                        taskIdentity.setIdentity(empower.getToIdentity());
                        taskIdentity.setFromIdentity(empower.getFromIdentity());
                        break;
                    }
                }
            }
        }
    }

    public TaskIdentities removeIdentity(String str) {
        Iterator<TaskIdentity> iterator = this.iterator();
        while (iterator.hasNext()) {
            TaskIdentity taskIdentity = iterator.next();
            if (StringUtils.equals(taskIdentity.getIdentity(), str)) {
                this.remove(taskIdentity);
                return this;
            }
        }
        return this;
    }

    public TaskIdentities removeIdentities(Collection<String> collections) {
        Iterator<TaskIdentity> iterator = this.iterator();
        while (iterator.hasNext()) {
            TaskIdentity taskIdentity = iterator.next();
            if (collections.contains(taskIdentity.getIdentity())) {
                this.remove(taskIdentity);
            }
        }
        return this;
    }

    public TaskIdentities addIdentities(Collection<String> collections) {
        for (String str : collections) {
            this.addIdentity(str);
        }
        return this;
    }

    /**
     * 위임 판단 후 중복된 사용자가 있을 수 있음, 예: b가 c에게 위임하고 처리자로 a, b, c, d를 선택하면 중복 제거하지 않을 경우 b, c가 모두 걸러지지 않음
     * 
     * @return
     */
    public List<String> identities() {
        List<String> list = new ArrayList<>();
        for (TaskIdentity taskIdentity : this) {
            list.add(taskIdentity.getIdentity());
        }
        return list.stream().distinct().collect(Collectors.toList());
    }

    private static final long serialVersionUID = -5874962038380255744L;

}