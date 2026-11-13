package com.x.attendance.entity.v2;




import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2LeavePolicyGrantAmountTypeTenureLeaveRule extends JsonProperties {
 
    private static final long serialVersionUID = -7000765123966414738L;

    public AttendanceV2LeavePolicyGrantAmountTypeTenureLeaveRule(Double minYears, Double maxYears, Double amount) {
        if (minYears == null && maxYears == null) {
            throw new IllegalArgumentException("minYears와 maxYears를 동시에 null로 둘 수 없습니다");
        }
        if (minYears != null && maxYears != null && minYears >= maxYears) {
            throw new IllegalArgumentException("minYears는 maxYears보다 작아야 합니다");
        }
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("amount는 음수가 아니어야 합니다");
        }
        this.minYears = minYears;
        this.maxYears = maxYears;
        this.amount = amount;
    }

    @FieldDescribe("최소 근속 연수(포함), null은 하한 없음")
    private Double minYears;

    @FieldDescribe("최대 근속 연수(미포함), null은 상한 없음")
    private Double maxYears;

    @FieldDescribe("지급 한도")
    private Double amount;

    public boolean match(double years) {
        boolean lowerOk = (minYears == null) || (years >= minYears);
        boolean upperOk = (maxYears == null) || (years < maxYears);
        return lowerOk && upperOk;
    }

    public Double getMinYears() {
        return minYears;
    }

    public void setMinYears(Double minYears) {
        this.minYears = minYears;
    }

    public Double getMaxYears() {
        return maxYears;
    }

    public void setMaxYears(Double maxYears) {
        this.maxYears = maxYears;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

     
    

}