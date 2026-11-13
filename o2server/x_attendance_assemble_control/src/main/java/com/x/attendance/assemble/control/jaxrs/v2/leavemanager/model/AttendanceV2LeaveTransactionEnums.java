package com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model;

public class AttendanceV2LeaveTransactionEnums {
    
    public enum BizTypeEnum {
        GRANT("GRANT"), // 지급
        USE("USE"), // 사용
        CANCEL("CANCEL"), // 취소
        EXPIRE("EXPIRE"), // 만료
        CARRYFORWARD("CARRYFORWARD"), // 이월
        ADJUST("ADJUST"); // 조정

        private String value;
 
        BizTypeEnum(String value) {
            this.value = value;
        }   
        public String getValue() {
            return value;
        }
    }
}
